package socket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socket.SourceInfo;
import socket.SourceInfoRespository;


@Service("sourceInfoService")
@Slf4j
public class SourceInfoService {
    @Autowired
    private SourceInfoRespository sourceInfoRespository;

    public SourceInfo getSource(String neUID){
        SourceInfo source = sourceInfoRespository.getSource(neUID);
        return source;
    }
}
