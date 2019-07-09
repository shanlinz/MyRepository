package socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2019/6/26.
 */
@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Table(name = "ALARM_INFO")
public class AlarmInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "ALARM_SERIAL")
    private String alarmSerial;
    @Column(name = "RES_ID")
    private Long resId;
    @Column(name = "DETAIL")
    private String detail;
    @Column(name = "DICT_ALARM_LEVEL_VALUE")
    private Long dictAlarmLevelValue;
    @Column(name = "FIRST_OCCUR_TIME")
    private String firstOccurTime;
    @Column(name = "CAUSE_ID")
    private String causeId;
    @Column(name = "CAUSE_NAME")
    private String causeName;
    @Column(name = "OBJECT_ID")
    private String objectId;
    @Column(name = "OBJECT_NAME")
    private String objectName;
    @Column(name = "RES_NAME")
    private String resName;
    @Column(name = "TOP_OBJECT_ID")
    private String topObjectId;
    @Column(name = "TOP_OBJECT_NAME")
    private String topObjectName;
    @Column(name = "TOP_OBJECT_RES_NAME")
    private String topObjectResName;
    @Column(name = "OBJECT_USER_LABEL")
    private String objectUserLabel;
    @Column(name = "TOP_OBJECT_RES_ID")
    private Long topObjectResId;
    @Column(name = "TOP_OBJECT_USER_LABEL")
    private String topObjectUserLabel;
    @Column(name = "DEPARTMENT_ID")
    private Integer departmentId;
    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;
    @Column(name = "DICT_ALARM_LEVEL_NAME")
    private String dictAlarmLevelName;
    @Column(name = "DICT_ALARM_SOURCE_VALUE")
    private Integer dictAlarmSourceValue;
    @Column(name = "DICT_ALARM_SOURCE_NAME")
    private String dictAlarmSourceName;
    @Column(name = "OCCUR_COUNT")
    private Integer occurCount;
    @Column(name = "LAST_UPDATE_TIME")
    private Date lastUpdateTime;
    @Column(name = "LAST_OCCUR_TIME")
    private Date lastOccurTime;
    @Column(name = "DICT_ALARM_CLEAR_VALUE")
    private Integer dictAlarmClearValue;
    @Column(name = "DICT_ALARM_CLEAR_NAME")
    private String dictAlarmClearName;
    @Column(name = "CLEAR_TIME")
    private Date clearTime;
    @Column(name = "CLEAR_USER")
    private String clearUser;
    @Column(name = "CLEAR_REMARK")
    private String clearRemark;
    @Column(name = "DICT_ALARM_ACK_VALUE")
    private Integer dictAlarmAckValue;
    @Column(name = "DICT_ALARM_ACK_NAME")
    private String dictAlarmAckName;
    @Column(name = "ACK_TIME")
    private Date ackTime;
    @Column(name = "ACK_USER")
    private String ackUser;
    @Column(name = "ACK_REMARK")
    private String ackRemark;

}
