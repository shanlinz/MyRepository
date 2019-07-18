package com.wisdombud.alarmmgr.collection.service;

import com.wisdombud.alarmmgr.collection.common.util.CommonUtil;
import com.wisdombud.alarmmgr.collection.domain.socketServer.AlarmDataTest;
import com.wisdombud.alarmmgr.collection.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zsl on 2019/6/26.
 */
@Slf4j
@Service("socketServerService")
public class SocketServerService {

    public boolean isLogin = false;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AlarmDataTestService alarmDataTestService;

    public void start(Integer port,Integer id) throws Exception {

        redisTemplate.boundValueOps("socketServer"+id).set("1");

        ServerSocket serverSocket = new ServerSocket(port);

        Socket socket = null;

        while (true){
            try {
                //从请求队列中取出链接
                socket = serverSocket.accept();

                while (true) {

                    //获取客户端信息
                    InputStream inputStream = socket.getInputStream();

                    byte[] bytes = new byte[1024];
                    int len;
                    String s = new String();

                    //读取客户端请求信息
                    while (inputStream.available() != 0 && (len = inputStream.read(bytes)) != -1) {
                        s = new String(bytes, 0, len, "UTF-8");
                    }
                    String[] strings = s.split(";");

                    //回复客户端
                    OutputStream outputstream = socket.getOutputStream();

                    //判断请求类型 为reqLoginAlarm 登录请求
                    if (strings[0].equals(MsgTypeEnum.reqLoginAlarm.getValue())) {
                        String[] split = strings[1].split("=");
                        //判断用户名密码是否正确
                        if(split[1].equals("user")){
                            outputstream.write("ackLoginAlarm;result=succ;resDesc=null".getBytes());
                            isLogin = true;
                        }else{
                            outputstream.write("ackLoginAlarm;result=fail;resDesc=user-error".getBytes());
                        }
                    } else if (strings[0].equals(MsgTypeEnum.reqSyncAlarmMsg.getValue())) {
                        outputstream.write("ackSyncAlarmMsg;reqId=33;resDesc=null".getBytes());
                        String[] split = strings[2].split("alarmSeq=");
                        redisTemplate.boundValueOps("socketServer"+id).set(split[1]);
                        Thread.sleep(5000L);
                    } else if (strings[0].equals(MsgTypeEnum.reqSyncAlarmFile.getValue())) {
                        outputstream.write("ackSyncAlarmFile;reqId=33;result=succ;resDesc=null".getBytes());
                        outputstream.write("ackSyncAlarmFile;reqId=33;result=succ;filename=/usr/local/unicom_alarm/sql.zip;resDesc=null".getBytes());
                    } else if (strings[0].equals(MsgTypeEnum.reqHeartBeat.getValue())) {
                        outputstream.write("ackHeartBat;reqId=33".getBytes());
                    } else if (strings[0].equals(MsgTypeEnum.closeConnAlarm.getValue())) {
                        outputstream.close();
                    }

                    if (isLogin == true) {
                        AlarmDataTest alarmDataTest = new AlarmDataTest();
                        alarmDataTest.setAlarmSeq(Integer.valueOf(redisTemplate.boundValueOps("socketServer"+id).get()));
                        alarmDataTest.setAlarmTitle("小区无话务量告警"+ CommonUtil.getRandom(3));
                        alarmDataTest.setAlarmStatus(1);
                        alarmDataTest.setAlarmType("Signaling System"+ CommonUtil.getRandom(3));
                        alarmDataTest.setOrigSeverity(3);
                        alarmDataTest.setEventTime("2016-04-14 16:25:43");
                        alarmDataTest.setOmcReceivedTime("2016-04-14 16:25:45");
                        alarmDataTest.setAlarmId(CommonUtil.getUUID());
                        alarmDataTest.setSpecificProblemId("29242"+ CommonUtil.getRandom(3));
                        alarmDataTest.setSpecificProblem("设备问题"+ CommonUtil.getRandom(3));
                        alarmDataTest.setOmcUID("10100MOBILE0008A"+ CommonUtil.getRandom(3));
                        alarmDataTest.setNeUID("DC=www.excample.cn,SubNetwork=EG_EUTRAN_SYSTEM,SubNetwork=0,ManagedElement=2,EnbFunction=1"+ CommonUtil.getRandom(3));
                        alarmDataTest.setNeName("LTE北向基站"+ CommonUtil.getRandom(3));
                        alarmDataTest.setNeType("ENB"+ CommonUtil.getRandom(3));
                        alarmDataTest.setObjectUID("NE=528,eNodeBCell=0"+ CommonUtil.getRandom(3));
                        alarmDataTest.setObjectName("小区0"+ CommonUtil.getRandom(3));
                        alarmDataTest.setObjectType("EutranCellTdd"+ CommonUtil.getRandom(3));
                        alarmDataTest.setLocationInfo("本地小区标识=0,小区双工模式=0"+ CommonUtil.getRandom(3));
                        alarmDataTest.setESerialNum("ABCD1234"+ CommonUtil.getRandom(3));
                        alarmDataTest.setAddInfo("eNodeBid=490937"+ CommonUtil.getRandom(3));
                        alarmDataTest.setRNeUID("NE=001"+ CommonUtil.getRandom(3));
                        alarmDataTest.setRNeName("BBU金融街"+ CommonUtil.getRandom(3));
                        alarmDataTest.setRNeType("BBU-CU"+ CommonUtil.getRandom(3));

                        JSONObject jsonObject = JSONObject.fromObject(alarmDataTest);
                        outputstream.write(("realTimeAlarm:" + jsonObject.toString()).getBytes());
                        Integer i = alarmDataTest.getAlarmSeq() + 1;
                        redisTemplate.boundValueOps("socketServer"+id).set(i.toString());
                        Thread.sleep(2000);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                socket.close();
            }
        }

    }
}
