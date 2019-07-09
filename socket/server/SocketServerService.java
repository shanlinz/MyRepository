package socket.server;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socket.AlarmDataTest;
import socket.enums.MsgTypeEnum;
import socket.service.AlarmDataTestService;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2019/6/26.
 */
@Slf4j
@Service("socketServerService")
public class SocketServerService {

    //public static final int port = 8888;
    public boolean isLogin = false;

    public int alarmSeq = 1;

    @Autowired
    private AlarmDataTestService alarmDataTestService;

    public void start(Integer port) {
        try {
            int count = alarmDataTestService.getCount();
            //创建serversocket
            ServerSocket serverSocket = new ServerSocket(port);
            //从请求队列中取出链接
            Socket socket = serverSocket.accept();
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
                System.out.println("qqqqqqqqqqqqqqqqqqqqqqq"+s);
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
                        Thread.sleep(5000);
                    }else{
                        outputstream.write("ackLoginAlarm;result=fail;resDesc=user-error".getBytes());
                    }
                } else if (strings[0].equals(MsgTypeEnum.reqSyncAlarmMsg.getValue())) {
                    outputstream.write("ackSyncAlarmMsg;reqId=33;resDesc=null".getBytes());
                    String[] split = strings[2].split("alarmSeq=");
                    alarmSeq = Integer.valueOf(split[1]);
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
                    //判断是否登录
                    AlarmDataTest alarmDataTest = alarmDataTestService.findByAlarmSeq(alarmSeq);
                    AlarmDataTest alarmDataTest1 = new AlarmDataTest();
                    alarmDataTest1.setAlarmSeq(count+alarmSeq);
                    alarmDataTest1.setObjectName(alarmDataTest.getObjectName());
                    alarmDataTest1.setObjectUID(alarmDataTest.getObjectUID());
                    alarmDataTest1.setAddInfo(alarmDataTest.getAddInfo());
                    alarmDataTest1.setAlarmId(alarmDataTest.getAlarmId());
                    alarmDataTest1.setAlarmStatus(alarmDataTest.getAlarmStatus());
                    alarmDataTest1.setAlarmTitle(alarmDataTest.getAlarmTitle());
                    alarmDataTest1.setAlarmType(alarmDataTest.getAlarmType());
                    alarmDataTest1.setESerialNum(alarmDataTest.getESerialNum());
                    alarmDataTest1.setEventTime(alarmDataTest.getEventTime());
                    alarmDataTest1.setLocationInfo(alarmDataTest.getLocationInfo());
                    alarmDataTest1.setNeName(alarmDataTest.getNeName());
                    alarmDataTest1.setNeType(alarmDataTest.getNeType());
                    alarmDataTest1.setNeUID(alarmDataTest.getNeUID());
                    alarmDataTest1.setRNeType(alarmDataTest.getRNeType());
                    alarmDataTest1.setRNeName(alarmDataTest.getRNeName());
                    alarmDataTest1.setRNeUID(alarmDataTest.getRNeUID());
                    alarmDataTest1.setOrigSeverity(alarmDataTest.getOrigSeverity());
                    alarmDataTest1.setOmcReceivedTime(alarmDataTest.getOmcReceivedTime());
                    alarmDataTest1.setSpecificProblem(alarmDataTest.getSpecificProblem());
                    alarmDataTest1.setSpecificProblemId(alarmDataTest.getSpecificProblemId());
                    alarmDataTest1.setOmcUID(alarmDataTest.getOmcUID());
                    alarmDataTest1.setObjectType(alarmDataTest.getObjectType());

                    alarmDataTestService.save(alarmDataTest1);
                    if (alarmDataTest != null) {
                        alarmSeq++;
                        JSONObject jsonObject = JSONObject.fromObject(alarmDataTest);
                        System.out.println(jsonObject.toString());
                        outputstream.write(("realTimeAlarm:" + jsonObject.toString()).getBytes());
                        Thread.sleep(60000);
                    } else {
                        alarmSeq = alarmSeq + 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
