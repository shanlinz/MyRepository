package socket.enums;

/**
 * Created by Administrator on 2019/6/25.
 */
public enum MsgTypeEnum {
    realTimeAlarm(0,"realTimeAlarm"),//实时告警消息
    reqLoginAlarm(1,"reqLoginAlarm"),//登录请求
    ackLoginAlarm(2,"ackLoginAlarm"),//登录请求响应
    reqSyncAlarmMsg(3,"reqSyncAlarmMsg"),//消息同步告警请求
    ackSyncAlarmMsg(4,"ackSyncAlarmMsg"),//消息同步告警请求响应
    reqSyncAlarmFile(5,"reqSyncAlarmFile"),//文件告警请求
    ackSyncAlarmFile(6,"ackSyncAlarmFile"),//无结果的立即应答文件告警请求响应
    ackSyncAlarmFileResult(7,"ackSyncAlarmFileResult"),//含有文件结果的文件告警请求应答
    reqHeartBeat(8,"reqHeartBeat"),//心跳消息
    ackHeartBeat(9,"ackHeartBeat"),//心跳消息响应
    closeConnAlarm(10,"closeConnAlarm");//客户端发送的关闭连接的通知

    Integer name;
    String value;

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    MsgTypeEnum(Integer name, String value){
        this.name = name;
        this.value = value;
    }
}
