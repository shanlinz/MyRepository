package com.wisdombud.alarmmgr.collection.service;

import com.wisdombud.alarmmgr.collection.common.util.CommonUtil;
import com.wisdombud.alarmmgr.collection.domain.acc.AlarmInfoCusin;
import com.wisdombud.alarmmgr.collection.domain.acc.AlarmInfoCusinRepository;
import com.wisdombud.alarmmgr.collection.domain.socketClient.AlarmInfo;
import com.wisdombud.alarmmgr.collection.domain.socketClient.AlarmInfoRespository;
import com.wisdombud.alarmmgr.collection.domain.socketClient.SocketServerInfo;
import com.wisdombud.alarmmgr.collection.domain.socketClient.SourceInfo;
import com.wisdombud.alarmmgr.collection.domain.socketServer.AlarmDataTest;
import com.wisdombud.alarmmgr.collection.http.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * created by zsl
 */
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
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    private SocketServerInfoService socketServerInfoService;

    private static Integer reqId = 1;

    List<AlarmInfo> alarmInfos = new ArrayList<AlarmInfo>();

    List<AlarmInfoCusin> alarmInfoCusins = new ArrayList<AlarmInfoCusin>();

    //客户端发送请求
    public void send(String msg, Socket socket) {
        try {
            System.out.println("ip地址"+socket.getInetAddress().getHostAddress());
            OutputStream outputStream = socket.getOutputStream();
            byte[] sendBytes = msg.getBytes("UTF-8");
            outputStream.write(sendBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //客户端发送心跳
    public void heartBeat(Socket socket) throws Exception {
        OutputStream outputStream = socket.getOutputStream();
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
                    System.out.println("socket is cloesed, stop send heartBeat");
                }
            }
        }).start();
    }

    //接收服务端响应
    public void receiveAlarmData(String msg, Socket socket, SocketServerInfo socketServerInfo) {
        try {
            //登录请求
            send(msg, socket);
            Thread.sleep(3000L);
            //心跳
            heartBeat(socket);
            Thread.sleep(3000L);

            //获取输出流
            OutputStream outputStream = socket.getOutputStream();
            //获取输入流
            InputStream is = socket.getInputStream();

            Map<String, Integer> map = new HashMap<String, Integer>();

            boolean b = true;
            while (b) {
                //判断手动开关为离线，关闭socket
                String unique = redisTemplate.boundValueOps(socketServerInfo.getUniqueKey() + "02").get();
                if (unique.equals("0")){
                    socketServerInfoService.close(socketServerInfo.getId());
                    socketServerInfo.setCauseInfo("auto closed");
                    socketServerInfoService.save(socketServerInfo);
                    outputStream.write("close socket".getBytes());
                    Thread.sleep(3000);
                    b = false;
                    outputStream.close();
                    is.close();
                    socket.close();
                    break;
                }


                byte[] inputBytes = new byte[1024 * 8];
                int len;
                String s;

                //监听输入流
                while (is.available() != 0 && (len = is.read(inputBytes)) != -1) {
                    //判断手动开关为离线，关闭socket
                    String unique2 = redisTemplate.boundValueOps(socketServerInfo.getUniqueKey() + "02").get();
                    if (unique2.equals("0")){
                        socketServerInfoService.close(socketServerInfo.getId());
                        socketServerInfo.setCauseInfo("auto closed");
                        socketServerInfoService.save(socketServerInfo);
                        outputStream.write("close  socket".getBytes());
                        Thread.sleep(3000);
                        b = false;
                        outputStream.close();
                        is.close();
                        socket.close();
                        break;
                    }

                    //s为接受到的服务端的响应
                    s = new String(inputBytes, 0, len, "UTF-8");

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
            //这个地方记得把告警的departmentID、NAME存到CONF_SENSOR_TEMP里
        }
    }

    //保存到数据库
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
        alarmInfo.setFirstOccurTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        alarmInfo.setCauseId(alarmDataTest.getSpecificProblemId());
        alarmInfo.setCauseName(alarmDataTest.getSpecificProblem());
        alarmInfo.setObjectId(CommonUtil.getRandom(4));
        alarmInfo.setObjectName("test" + CommonUtil.getRandom(3));
        alarmInfo.setResName("test" +  CommonUtil.getRandom(3));
        alarmInfo.setTopObjectId(CommonUtil.getRandom(4));
        alarmInfo.setTopObjectName("test" + CommonUtil.getRandom(3));
        alarmInfo.setTopObjectResName("test" + CommonUtil.getRandom(3));
        alarmInfo.setResId(107001000000000L);
        alarmInfo.setTopObjectUserLabel("test" + CommonUtil.getRandom(3));
        alarmInfo.setTopObjectResId(107001000000000L);
        alarmInfo.setDepartmentId(Integer.valueOf(CommonUtil.getRandom(4)));
        alarmInfo.setDepartmentName("test" + CommonUtil.getRandom(3));
        alarmInfo.setDictAlarmLevelName("test" + CommonUtil.getRandom(3));
        alarmInfo.setDictAlarmSourceValue(Integer.valueOf(CommonUtil.getRandom(4)));
        alarmInfo.setDictAlarmSourceName("test" + CommonUtil.getRandom(3));
        alarmInfo.setOccurCount(Integer.valueOf(CommonUtil.getRandom(4)));
        alarmInfo.setLastUpdateTime(new Date());
        alarmInfo.setLastOccurTime(new Date());
        alarmInfo.setDictAlarmClearName("test" + CommonUtil.getRandom(3));
        alarmInfo.setClearTime(new Date());
        alarmInfo.setDictAlarmAckName("test" + CommonUtil.getRandom(3));
        alarmInfo.setAckTime(new Date());
        alarmInfo.setObjectUserLabel("test" + CommonUtil.getRandom(3));
        alarmInfo.setAlarmId(alarmDataTest.getAlarmId());

        alarmInfoCusin.setAlarmSeq(alarmDataTest.getAlarmSeq());
        alarmInfoCusin.setAlarmType(alarmDataTest.getAlarmType());
        alarmInfoCusin.setOmcReceivedTime(new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date()));
        alarmInfoCusin.setAlarmId(alarmDataTest.getAlarmId());
        alarmInfoCusin.setOmcUid(alarmDataTest.getOmcUID());
        alarmInfoCusin.setLocationInfo(alarmDataTest.getLocationInfo());
        alarmInfoCusin.setESerialNum(alarmDataTest.getESerialNum());
        alarmInfoCusin.setAddInfo(alarmDataTest.getAddInfo());
        alarmInfoCusin.setRNeUID(alarmDataTest.getRNeUID());
        alarmInfoCusin.setRNeName(alarmDataTest.getRNeName());
        alarmInfoCusin.setRNeType(alarmDataTest.getRNeType());
        alarmInfoCusin.setNeUID(alarmDataTest.getNeUID());
        alarmInfoCusin.setNeName(alarmDataTest.getNeName());
        alarmInfoCusin.setNeType(alarmDataTest.getNeType());
        alarmInfoCusin.setObjectUID(alarmDataTest.getObjectUID());
        alarmInfoCusin.setObjectName(alarmDataTest.getObjectName());
        alarmInfoCusin.setObjetType(alarmDataTest.getObjectType());

        alarmInfos.add(alarmInfo);
        alarmInfoCusins.add(alarmInfoCusin);

        if (alarmInfos.size() == 100){
            alarmInfoCusinRepository.saveAll(alarmInfoCusins);
            alarmInfoRespository.saveAll(alarmInfos);

            alarmInfos.clear();
            alarmInfoCusins.clear();
        }
    }
}
