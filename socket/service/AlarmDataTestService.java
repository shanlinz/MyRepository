package socket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import socket.AlarmDataTest;
import socket.AlarmDataTestRespository;

/**
 * Created by Administrator on 2019/6/26.
 */
@Service("alarmDataTestService")
@Slf4j
public class AlarmDataTestService {

    @Autowired
    private AlarmDataTestRespository respository;

    public void save(AlarmDataTest alarmData){
        respository.save(alarmData);
    }

    public int getCount(){
        return respository.getCount();
    }

    public AlarmDataTest findByAlarmSeq(Integer alarmSeq){
        return respository.findByAlarmSeq(alarmSeq);
    }
}
