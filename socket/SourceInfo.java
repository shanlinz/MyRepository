package socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by Administrator on 2019/7/8.
 */
@Entity
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "CONF_SENSOR_TEMP")
public class SourceInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NE_UID")
    private String NE_UID;

    @Column(name = "OBJ_LDN")
    private String OBJ_LDN;

    @Column(name = "PROV_ID")
    private String PROV_ID;

    @Column(name = "CITY_ID")
    private String CITY_ID;

    @Column(name = "POSIT_ID")
    private String POSIT_ID;

    @Column(name = "EQP_NAME")
    private String EQP_NAME;

    @Column(name = "EQP_TYPE_ID")
    private String EQP_TYPE_ID;

    @Column(name = "MRF_ID")
    private String MRF_ID;

    @Column(name = "PV_FLAG")
    private String PV_FLAG;

    @Column(name = "SW_VER")
    private String SW_VER;

    @Column(name = "VNF_VER")
    private String VNF_VER;

    @Column(name = "HDWE_VER")
    private String HDWE_VER;

    @Column(name = "SH_IP")
    private String SH_IP;

    @Column(name = "ISC_IP")
    private String ISC_IP;

    @Column(name = "MB_IP")
    private String MB_IP;

    @Column(name = "UT_IP")
    private String UT_IP;

    @Column(name = "SCP_POINT_CODE")
    private String SCP_POINT_CODE;

    @Column(name = "SCP_MSC_ID")
    private String SCP_MSC_ID;

    @Column(name = "SIP_INF_EQP_MARK1")
    private String SIP_INF_EQP_MARK1;

    @Column(name = "SIP_INF_EQP_MARK2")
    private String SIP_INF_EQP_MARK2;

    @Column(name = "SCC_AS_FQDN")
    private String SCC_AS_FQDN;

    @Column(name = "MMTEL_AS_FQDN")
    private String MMTEL_AS_FQDN;

    @Column(name = "ATU_STI")
    private String ATU_STI;

    @Column(name = "CONF_FACT_URI")
    private String CONF_FACT_URI;

    @Column(name = "MMTEL_HOST_NAME")
    private String MMTEL_HOST_NAME;

    @Column(name = "SCC_AS_POOL_HOST_NAME")
    private String SCC_AS_POOL_HOST_NAME;

    @Column(name = "MAX_CAPA")
    private String MAX_CAPA;

    @Column(name = "ACT_CAPA")
    private String ACT_CAPA;

    @Column(name = "ADMIN_REGION")
    private String ADMIN_REGION;

    @Column(name = "APPROVAL_NO")
    private String APPROVAL_NO;

    @Column(name = "APPROVAL_TIME")
    private String APPROVAL_TIME;

    @Column(name = "NOTES")
    private String NOTES;

    @Column(name = "BEGIN_TIME")
    private String BEGIN_TIME;


}
