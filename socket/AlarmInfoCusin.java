package socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ChenJianbing
 */
@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Table(name = "ALARM_INFO_CUSIN")
public class AlarmInfoCusin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ALARM_SEQ")
    private Integer alarmSeq;
    @Column(name = "ALARM_STATUS")
    private Integer alarmStatus;
    @Column(name = "ALARM_TYPE")
    private String alarmType;
    @Column(name = "ALARM_ID")
    private String alarmId;
    @Column(name = "OMC_UID")
    private String omcUid;
    @Column(name = "OMC_RECEIVED_TIME")
    private String omcReceivedTime;
    @Column(name = "LOCATION_INFO")
    private String locationInfo;
    @Column(name = "E_SERIAL_NUM")
    private String eSerialNum;
    @Column(name = "ADD_INFO")
    private String addInfo;
    @Column(name = "R_NE_UID")
    private String rNeUID;
    @Column(name = "R_NE_NAME")
    private String rNeName;
    @Column(name = "R_NE_TYPE")
    private String rNeType;
    @Column(name = "NE_UID")
    private String neUID;
    @Column(name = "NE_NAME")
    private String neName;
    @Column(name = "NE_TYPE")
    private String neType;
    @Column(name = "OBJECT_UID")
    private String objectUID;
    @Column(name = "OBJECT_NAME")
    private String objectName;
    @Column(name = "OBJECT_TYPE")
    private String objetType;
}
