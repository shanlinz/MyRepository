package socket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Administrator on 2019/6/26.
 */
public interface AlarmDataTestRespository extends JpaRepository<AlarmDataTest,Integer> {

    @Query(value = " from AlarmDataTest where alarmSeq = :alarmSeq")
    AlarmDataTest findByAlarmSeq(@Param("alarmSeq") Integer alarmSeq);

    @Query(value = "select count(1) from AlarmDataTest")
    int getCount();
}
