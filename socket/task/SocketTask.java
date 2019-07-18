package com.wisdombud.alarmmgr.collection.socket;

import com.wisdombud.alarmmgr.collection.domain.socketClient.SocketServerInfo;
import com.wisdombud.alarmmgr.collection.service.SocketClientService;
import com.wisdombud.alarmmgr.collection.service.SocketServerInfoService;
import com.wisdombud.alarmmgr.collection.service.SocketServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by zsl
 */

@Component
public class SocketTask {

    ExecutorService executorServiceClient = Executors.newFixedThreadPool(10);

    @Autowired
    SocketServerInfoService socketServerInfoService;
    @Autowired
    SocketServerService socketServerService;
    @Autowired
    SocketClientService socketclientservice;
    @Autowired
    RedisTemplate<String,String> redisTemplate;

    //服务端
    @Scheduled(initialDelay = 5000, fixedRate = 1000 * 60 * 60 * 24 * 666)
    public void getSocketServerInfo() throws Exception{
        //---- 8047 ----9047 9048 192.168.160.49
        socketServerService.start(9048,1);
    }

    //客户端启动
    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60 * 1)
    public void startClient() {
        System.out.println("------启动客户端定时任务-------");
        //获取ip，port
        List<SocketServerInfo> allServerInfo = socketServerInfoService.getAllServerInfo();
        if (allServerInfo.size() != 0) {
            //每有一个服务器，客户端开启一个线程去链接，互不影响
            for (SocketServerInfo socketServerInfo : allServerInfo) {
                //判断状态是否为离线
                if (socketServerInfo.getStatus() == 0) {
                    String s = redisTemplate.boundValueOps( socketServerInfo.getUniqueKey()+"02").get();
                    //判断redis开关状态是否为在线
                    if (s.equals("1")) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Socket socket = null;

                                try {
                                    //建立链接
                                    socket = new Socket(socketServerInfo.getHost(), socketServerInfo.getPort());
                                    socketServerInfoService.update(socketServerInfo.getId());
                                    //登录请求消息
                                    String msg = "reqLoginAlarm;" + "user=" + socketServerInfo.getUser() + ";" + "key=" + socketServerInfo.getKey() + ";" + "type=msg";
                                    //客户端发送接收消息
                                    socketclientservice.receiveAlarmData(msg, socket,socketServerInfo);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        executorServiceClient.submit(runnable);
                    }
                }
            }
            //executorServiceClient.shutdown();
        }
    }
}
