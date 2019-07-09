package socket.service;

import com.wisdombud.alarmmgr.collection.common.Constants;
import com.wisdombud.alarmmgr.collection.domain.acc.AlarmDataRepository;
import com.wisdombud.alarmmgr.collection.domain.acc.AlarmInfoCusin;
import com.wisdombud.alarmmgr.collection.domain.acc.AlarmInfoCusinRepository;
import com.wisdombud.alarmmgr.collection.domain.socketClient.AlarmInfo;
import com.wisdombud.alarmmgr.collection.domain.socketClient.AlarmInfoRespository;
import com.wisdombud.alarmmgr.collection.ftp.FileIOUtil;
import com.wisdombud.alarmmgr.collection.ftp.SFTPProcessor;
import com.wisdombud.alarmmgr.collection.ftp.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 功能: 报警 .<br/>
 * date: 2019年5月5日 上午11:03:45 <br/>
 *
 * @author ChenJianbing
 * @since JDK 1.7
 */
@Service("alarmDataService")
@Slf4j
public class AlarmDataService {

    String[] ignoreFields = {"id", "createTime"};

    @Autowired
    private AlarmDataRepository alarmDataRepository;

    @Autowired
    private AlarmInfoCusinRepository alarmInfoCusinRepository;

    @Autowired
    private AlarmInfoRespository alarmInfoRespository;

    @Transactional
    public String saveOrUpdate(String filePath) throws Exception {
        /*String remoteFile = "sql.zip";
        String remotePath = "/usr/local/unicom_alarm/";
        String localFile = "D:\\upload\\sql.zip";*/
        String remoteFile = new File(filePath).getName();
        String remotePath = filePath.substring(0, filePath.length() - remoteFile.length());
        String localFile = Constants.LOCAL_ADDRESS + remoteFile;

        SFTPProcessor sftpProcessor = new SFTPProcessor();
        sftpProcessor.todownloadFile(remoteFile, remotePath, localFile);
        SFTPProcessor.closeChannel();

        String destDirPath = Constants.LOCAL_ADDRESS;
        ZipUtil.unZip(localFile, destDirPath);

        File file = new File(filePath);
        String name = file.getName();
        File zipFile = new File(Constants.LOCAL_ADDRESS, name);
        zipFile.delete();
        /**
         * 获取upload文件夹下面的文件
         */
        File file1 = new File(Constants.LOCAL_ADDRESS);
        File[] array = file1.listFiles();
        String unZipName = null;
        for (int i = 0; i < array.length; i++) {
            unZipName = array[i].getName();
        }

        String ioMessage = FileIOUtil.readFileByLines(destDirPath + unZipName);
        /**
         * 读取完后删除upload文件下的.txt
         */
        File unZipFile = new File(Constants.LOCAL_ADDRESS, unZipName);
        unZipFile.delete();
        //处理json类型字符串
        String[] split = ioMessage.split("\\\\r\\\\n");
        List<AlarmInfoCusin> alarmInfoCusinList = new ArrayList<>();
        List<AlarmInfo> alarmInfoList = new ArrayList<>();
        for (String s : split) {
            JSONArray json = JSONArray.fromObject("[" + s + "]");

            JSONObject job = json.getJSONObject(0);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
            AlarmInfoCusin alarmInfoCusin = new AlarmInfoCusin();

            alarmInfoCusin.setId(3000L);
            alarmInfoCusin.setAlarmStatus(Integer.valueOf(String.valueOf(job.get("alarmStatus"))));
            alarmInfoCusin.setAlarmType(String.valueOf(job.get("alarmType")));
            alarmInfoCusin.setOmcReceivedTime(String.valueOf(job.get("omcReceivedTime")));
            alarmInfoCusin.setAlarmId(String.valueOf(job.get("alarmId")));
            alarmInfoCusin.setOmcUid(String.valueOf(job.get("omcUID")));
            alarmInfoCusin.setLocationInfo(String.valueOf(job.get("locationInfo")));
            alarmInfoCusin.setESerialNum(String.valueOf(job.get("eSerialNum")));
            alarmInfoCusin.setAddInfo(String.valueOf(job.get("addInfo")));
            alarmInfoCusin.setRNeUID(String.valueOf(job.get("rNeUID")));
            alarmInfoCusin.setRNeName(String.valueOf(job.get("rNeName")));
            alarmInfoCusin.setRNeType(String.valueOf(job.get("rNeType")));
            AlarmInfoCusin save = alarmInfoCusinRepository.save(alarmInfoCusin);
            alarmInfoCusinList.add(save);

            AlarmInfo alarmInfo = new AlarmInfo();

            alarmInfo.setAlarmSerial(String.valueOf(job.get("alarmSeq")));
            alarmInfo.setDetail(String.valueOf(job.get("alarmTitle")));
            alarmInfo.setDictAlarmLevelValue(Long.valueOf(String.valueOf(job.get("origSeverity"))));
            alarmInfo.setFirstOccurTime(String.valueOf(job.get("eventTime")));
            alarmInfo.setCauseId(String.valueOf(job.get("specificProblemID")));
            alarmInfo.setCauseName(String.valueOf(job.get("specificProblem")));
            alarmInfo.setObjectId(String.valueOf(job.get("neUID")));
            alarmInfo.setObjectName(String.valueOf(job.get("neName")));
            alarmInfo.setResName(String.valueOf(job.get("neType")));
            alarmInfo.setTopObjectId(String.valueOf(job.get("objectUID")));
            alarmInfo.setTopObjectName(String.valueOf(job.get("objectName")));
            alarmInfo.setTopObjectResName(String.valueOf(job.get("objectType")));
            alarmInfo.setResId(107001000000000L);
            alarmInfo.setTopObjectUserLabel("test" + Math.random());
            alarmInfo.setTopObjectResId(107001000000000L);
            alarmInfo.setDepartmentId(new Random().nextInt(1000));
            alarmInfo.setDepartmentName("test" + Math.random());
            alarmInfo.setDictAlarmLevelName("test" + Math.random());
            alarmInfo.setDictAlarmSourceValue(new Random().nextInt(1000));
            alarmInfo.setDictAlarmSourceName("test" + Math.random());
            alarmInfo.setOccurCount(new Random().nextInt(1000));
            alarmInfo.setLastUpdateTime(new Date());
            alarmInfo.setLastOccurTime(new Date());
            alarmInfo.setDictAlarmClearName("test" + Math.random());
            alarmInfo.setClearTime(new Date());
            alarmInfo.setDictAlarmAckName("test" + Math.random());
            alarmInfo.setAckTime(new Date());
            alarmInfo.setObjectUserLabel("test" + Math.random());
            AlarmInfo save1 = alarmInfoRespository.save(alarmInfo);
            alarmInfoList.add(save1);
        }
        String msg;
        if (alarmInfoList.size() > 0 && alarmInfoCusinList.size() > 0) {
            msg = "保存成功";
        } else {
            msg = "保存失败";
        }
        return msg;
    }

}
