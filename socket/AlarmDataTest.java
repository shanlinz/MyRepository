package socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.persistence.Id;

/**
 * Created by Administrator on 2019/6/25.
 */
@Entity
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class AlarmDataTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "alarmSeq")
    private Integer alarmSeq;
    @Column(name = "alarmTitle")
    private String alarmTitle;
    @Column(name = "alarmStatus")
    private Integer alarmStatus;
    @Column(name = "alarmType")
    private String alarmType;
    @Column(name = "origSeverity")
    private Integer origSeverity;
    @Column(name = "eventTime")
    private String eventTime;
    @Column(name = "omcReceivedTime")
    private String omcReceivedTime;
    @Column(name = "alarmId")
    private String alarmId;
    @Column(name = "specificProblemId")
    private String specificProblemId;
    @Column(name = "specificProblem")
    private String specificProblem;
    @Column(name = "omcUID")
    private String omcUID;
    @Column(name = "neUID")
    private String neUID;
    @Column(name = "neName")
    private String neName;
    @Column(name = "neType")
    private String neType;
    @Column(name = "objectUID")
    private String objectUID;
    @Column(name = "objectName")
    private String objectName;
    @Column(name = "objectType")
    private String objectType;
    @Column(name = "locationInfo")
    private String locationInfo;
    @Column(name = "eSerialNum")
    private String eSerialNum;
    @Column(name = "addInfo")
    private String addInfo;
    @Column(name = "rNeUID")
    private String rNeUID;
    @Column(name = "rNeName")
    private String rNeName;
    @Column(name = "rNeType")
    private String rNeType;

}
