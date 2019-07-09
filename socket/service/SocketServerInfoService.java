package socket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socket.SocketServerInfo;
import socket.SocketServerInfoRespository;

import java.util.List;

/**
 * Created by Administrator on 2019/6/29.
 */
@Service("socketServerInfoService")
@Slf4j
public class SocketServerInfoService {

    @Autowired
    private SocketServerInfoRespository socketServerInfoRespository;

    public List<SocketServerInfo> getAllServerInfo(){
        return socketServerInfoRespository.findAll();
    }

    public void update(Integer id){
        socketServerInfoRespository.update(id);
    }
}
