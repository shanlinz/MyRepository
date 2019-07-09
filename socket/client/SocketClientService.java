package socket.client;


import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socket.*;
import socket.httpUtil.HttpUtils;
import socket.service.AlarmDataService;
import socket.service.SourceInfoService;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service("socketClientService")
@Slf4j
public class SocketClientService {
    @Autowired
    private AlarmInfoRespository alarmInfoRespository;
    @Autowired
    private AlarmInfoCusinRepository alarmInfoCusinRepository;
    @Autowired
    private AlarmDataService alarmDataService;
    @Autowired
    private SourceInfoService sourceInfoService;

    private static Integer reqId = 1;

    //客户端发送请求
    public void send(String msg, Socket socket) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] sendBytes = msg.getBytes("UTF-8");
            outputStream.write(sendBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heartBeat(Socket socket) throws Exception {
        final OutputStream outputStream = socket.getOutputStream();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        String msg = "reqHeartBeat;reqId=" + reqId;
                        outputStream.write(msg.getBytes());
                        reqId++;
                        Thread.sleep(60000L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //接收服务端响应
    public void receiveAlarmData(String msg, Socket socket) {
        try {
            //登录请求
            send(msg, socket);

            //心跳
            heartBeat(socket);
            while (true) {
                OutputStream outputStream = socket.getOutputStream();
                Map<String, Integer> map = new HashMap<String, Integer>();
                InputStream is = socket.getInputStream();
                byte[] inputBytes = new byte[1024 * 8];
                int len;
                String s;
                //监听输入流
                while (is.available() != 0 && (len = is.read(inputBytes)) != -1) {
                    //s为接受到的服务端的响应
                    s = new String(inputBytes, 0, len, "UTF-8");
                    System.out.println("!!!!!!!" + s);

                    //如果登录失败继续发送登录请求
                    if(s.contains("ackLoginAlarm;result=fail")){
                        send(msg, socket);
                    //包含realTimeAlarm为服务端发送的实时告警消息
                    }else if (s.contains("realTimeAlarm:")) {
                        String[] strs = s.split("realTimeAlarm:");
                        String string = strs[1];
                        JSONObject jsonObject = JSONObject.fromObject(string);
                        AlarmDataTest alarmDataTest = (AlarmDataTest) JSONObject.toBean(jsonObject, AlarmDataTest.class);
                        //获取告警流水号，判断是否连续
                        //第一条 map里为空，直接保存             当告警序号大于2^31时从1重新开始
                        if (null == map.get("alarmSeq") || alarmDataTest.getAlarmSeq() == 1) {
                            map.put("alarmSeq", Integer.valueOf(alarmDataTest.getAlarmSeq()));
                            alarmDataSave(alarmDataTest);
                            //调用查询资源方法
                            //getSource(alarmDataTest);
                        } else {
                            //不为空 进行判断，等于1， 连续，保存
                            Integer i = map.get("alarmSeq");
                            if (1 == Integer.valueOf(alarmDataTest.getAlarmSeq()) - i) {
                                alarmDataSave(alarmDataTest);
                                map.put("alarmSeq", Integer.valueOf(alarmDataTest.getAlarmSeq()));
                                //调用查询资源方法
                                //getSource(alarmDataTest);
                            } else if (Integer.valueOf(alarmDataTest.getAlarmSeq()) - i > 1 && Integer.valueOf(alarmDataTest.getAlarmSeq()) - i < 1000) {
                                // 大于1 小于1000 调用 消息方式同步告警请求 把最新告警流水号传递过去
                                int alarmSeq = i + 1;
                                outputStream.write(("reqSyncAlarmMsg;reqId=" + reqId + ";alarmSeq=" + alarmSeq + ";").getBytes());
                                reqId++;
                            } else if (Integer.valueOf(alarmDataTest.getAlarmSeq()) - i > 1000) {
                                outputStream.write(("reqSyncAlarmFile;reqId=" + reqId + ";alarmSeq=4;syncSource=1").getBytes());
                                reqId++;
                                //getFileName(socket);//大于1000 调用文件方式告警请求
                            }
                        }
                        //当客户端发送文件方式请求后，会接受到服务端带有文件名的响应
                    } else if (s.contains("filename=")) {
                        int i = s.indexOf("/");
                        String filename = s.substring(i, s.indexOf(";", i));
                        //把文件路径传给ftp下载
                        alarmDataService.saveOrUpdate(filename);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //资源查询
    public void getSource(AlarmDataTest alarmDataTest){
        SourceInfo source = sourceInfoService.getSource(alarmDataTest.getNeUID());
        if (null == source){
            String url = "http://127.0.0.1:8080/resweb/unicom_res_service/query/";
            Map<String,String> map = new HashMap<String,String>();
            map.put("province","sd");
            map.put("neuid",alarmDataTest.getNeUID());
            String result = HttpUtils.sendGet(url, map);
            System.out.println(result);
        }
    }

    public void alarmDataSave(AlarmDataTest alarmDataTest) {
        AlarmInfo alarmInfo = new AlarmInfo();
        AlarmInfoCusin alarmInfoCusin = new AlarmInfoCusin();

        alarmInfo.setAlarmSerial(alarmDataTest.getAlarmSeq().toString());
        alarmInfo.setDetail(alarmDataTest.getAlarmTitle() + new Random().nextInt(100));
        if (alarmDataTest.getAlarmSeq()>30 && alarmDataTest.getAlarmSeq() <= 60 ){
            alarmInfo.setDictAlarmLevelValue(6110010000000002L);
            alarmInfoCusin.setAlarmStatus(0);
        } else if(alarmDataTest.getAlarmSeq()>60 && alarmDataTest.getAlarmSeq() <= 90){
            alarmInfo.setDictAlarmLevelValue(6110010000000003L);
            alarmInfoCusin.setAlarmStatus(1);
        } else if(alarmDataTest.getAlarmSeq()>90 && alarmDataTest.getAlarmSeq() <= 120){
            alarmInfo.setDictAlarmLevelValue(6110010000000004L);
            alarmInfoCusin.setAlarmStatus(0);
        } else {
            alarmInfo.setDictAlarmLevelValue(6110010000000001L);
            alarmInfoCusin.setAlarmStatus(1);
        }
        alarmInfo.setFirstOccurTime(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
        alarmInfo.setCauseId(alarmDataTest.getSpecificProblemId() + new Random().nextInt(100));
        alarmInfo.setCauseName(alarmDataTest.getSpecificProblem() + new Random().nextInt(100));
        alarmInfo.setObjectId("test" + new Random().nextInt(1000));
        alarmInfo.setObjectName("test" + new Random().nextInt(1000));
        alarmInfo.setResName("test" + new Random().nextInt(1000));
        alarmInfo.setTopObjectId("test" + new Random().nextInt(1000));
        alarmInfo.setTopObjectName("test" + new Random().nextInt(1000));
        alarmInfo.setTopObjectResName("test" + new Random().nextInt(1000));
        alarmInfo.setResId(107001000000000L);
        alarmInfo.setTopObjectUserLabel("test" + new Random().nextInt(1000));
        alarmInfo.setTopObjectResId(107001000000000L);
        alarmInfo.setDepartmentId(new Random().nextInt(1000));
        alarmInfo.setDepartmentName("test" + new Random().nextInt(1000));
        alarmInfo.setDictAlarmLevelName("test" + new Random().nextInt(1000));
        alarmInfo.setDictAlarmSourceValue(new Random().nextInt(1000));
        alarmInfo.setDictAlarmSourceName("test" + new Random().nextInt(1000));
        alarmInfo.setOccurCount(new Random().nextInt(1000));
        alarmInfo.setLastUpdateTime(new Date());
        alarmInfo.setLastOccurTime(new Date());
        alarmInfo.setDictAlarmClearName("test" + new Random().nextInt(1000));
        alarmInfo.setClearTime(new Date());
        alarmInfo.setDictAlarmAckName("test" + new Random().nextInt(1000));
        alarmInfo.setAckTime(new Date());
        alarmInfo.setObjectUserLabel("test" + new Random().nextInt(1000));
        alarmInfoRespository.save(alarmInfo);

        alarmInfoCusin.setAlarmSeq(alarmDataTest.getAlarmSeq());
        alarmInfoCusin.setAlarmType(alarmDataTest.getAlarmType() + new Random().nextInt(100));
        alarmInfoCusin.setOmcReceivedTime(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
        alarmInfoCusin.setAlarmId(alarmDataTest.getAlarmId() + new Random().nextInt(100));
        alarmInfoCusin.setOmcUid(alarmDataTest.getOmcUID() + new Random().nextInt(100));
        alarmInfoCusin.setLocationInfo(alarmDataTest.getLocationInfo() + new Random().nextInt(100));
        alarmInfoCusin.setESerialNum(alarmDataTest.getESerialNum() + new Random().nextInt(100));
        alarmInfoCusin.setAddInfo(alarmDataTest.getAddInfo() + new Random().nextInt(100));
        alarmInfoCusin.setRNeUID(alarmDataTest.getRNeUID() + new Random().nextInt(100));
        alarmInfoCusin.setRNeName(alarmDataTest.getRNeName() + new Random().nextInt(100));
        alarmInfoCusin.setRNeType(alarmDataTest.getRNeType() + new Random().nextInt(100));
        alarmInfoCusin.setNeUID(alarmDataTest.getNeUID());
        alarmInfoCusin.setNeName(alarmDataTest.getNeName() + new Random().nextInt(100));
        alarmInfoCusin.setNeType(alarmDataTest.getNeType() + new Random().nextInt(100));
        alarmInfoCusin.setObjectUID(alarmDataTest.getObjectUID());
        alarmInfoCusin.setObjectName(alarmDataTest.getObjectName() + new Random().nextInt(100));
        alarmInfoCusin.setObjetType(alarmDataTest.getObjectType() + new Random().nextInt(100));
        alarmInfoCusinRepository.save(alarmInfoCusin);
    }
}
