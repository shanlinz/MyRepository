package socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Administrator on 2019/6/29.
 */
@Entity
@Data
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
@Table(name = "ALARM_SERVER_INFO")
public class SocketServerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "HOST")
    private String host;

    @Column(name = "PORT")
    private Integer port;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "USER")
    private String user;

    @Column(name = "KEY")
    private String key;

    @Column(name = "TIME")
    private Date time;
}
