package socket.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import socket.SocketServerInfo;
import socket.client.SocketClientService;
import socket.server.SocketServerService;
import socket.service.SocketServerInfoService;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SocketTask {

    //ExecutorService executorServiceServer = Executors.newFixedThreadPool(5);
    ExecutorService executorServiceClient = Executors.newFixedThreadPool(5);
    /*public static void main(String[] args) {
        //线程池

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
            Socket socket = null;
            try {
                //和服务端建立链接
                socket = new Socket(socketServerInfo.getHost(),socketServerInfo.getPort());
                //登录请求消息
                String msg = "reqLoginAlarm;" + "user=" + socketServerInfo.getUser() + ";" + "key=" + socketServerInfo.getKey()+ ";" + "type=msg" ;
                //客户端发送接收消息
                socketclientservice.receiveAlarmData(msg,socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        };
        executorService.submit(runnable);
        executorService.shutdown();
    }*/


    @Autowired
    private SocketServerInfoService socketServerInfoService;
    @Autowired
    private SocketServerService socketServerService;
    @Autowired
    private SocketClientService socketclientservice;

    //服务端
    @Scheduled(initialDelay = 5000, fixedRate = 1000 * 60 * 60 * 24 * 666)
    public void getSocketServerInfo() {
        //socketServerService.start(9048);
        socketServerService.start(8047);
    }

    //客户端启动
    @Scheduled(initialDelay = 10000, fixedRate = 1000 * 60 * 60 * 24 * 666)
    public void startClient() {
        //获取ip，port
        List<SocketServerInfo> allServerInfo = socketServerInfoService.getAllServerInfo();
        //每有一个服务器，客户端开启一个线程去链接，互不影响
        for (final SocketServerInfo socketServerInfo : allServerInfo) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Socket socket = null;
                    try {
                        //和服务端建立链接
                        socket = new Socket(socketServerInfo.getHost(), socketServerInfo.getPort());
                        //登录请求消息
                        String msg = "reqLoginAlarm;" + "user=" + socketServerInfo.getUser() + ";" + "key=" + socketServerInfo.getKey() + ";" + "type=msg";
                        //客户端发送接收消息
                        socketclientservice.receiveAlarmData(msg, socket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            executorServiceClient.submit(runnable);
        }
        executorServiceClient.shutdown();
    }
}
