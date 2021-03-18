package com.taobao.pamirs.schedule;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.TBScheduleManager;
import com.taobao.pamirs.schedule.TimestampAdapter;

/**
 * 调度服务器信息定义
 * @author xuannan
 *
 */
public class ScheduleServer {
/*
 * 全局唯一编号
 */
private String uuid;
private long id;
  /**
   * 任务类型
   */
  private String taskType;
  
	/**
	 * 原始任务类型
	 */
	private String baseTaskType;

  private String ownSign;
  /**
   * 机器IP地址
   */
  private String ip;
  
  /**
   * 机器名称
   */
  private String hostName;
  
  /**
   * 调度服务器远程控制端口
   */
  int managerPort;
  
  String jmxUrl;
  /**
   * 数据处理线程数量
   */
  private int threadNum;
  /**
   * 服务开始时间
   */
  private Timestamp registerTime;
  /**
   * 最后一次心跳通知时间
   */
  private Timestamp heartBeatTime;
  /**
   * 处理描述信息，例如读取的任务数量，处理成功的任务数量，处理失败的数量，处理耗时
   * FetchDataCount=4430,FetcheDataNum=438570,DealDataSucess=438570,DealDataFail=0,DealSpendTime=651066
   */
  private String dealInfoDesc;
  
  private String nextRunStartTime;
  
  private String nextRunEndTime;  
  /**
   * 配置中心的当前时间
   */
  private Timestamp centerServerTime;
  
  /**
   * 数据版本号
   */
  private long version;

  public ScheduleServer(){
	  
  }
  public static com.taobao.pamirs.schedule.ScheduleServer createScheduleServer(String aBaseTaskType,String aOwnSign,int aThreadNum,int aManagerPort,String aJmxUrl) throws Exception{
	  com.taobao.pamirs.schedule.ScheduleServer result = new com.taobao.pamirs.schedule.ScheduleServer();
	  result.baseTaskType = aBaseTaskType;
	  result.ownSign = aOwnSign;
	  result.taskType = TBScheduleManager.getTaskTypeByBaseAndOwnSign(aBaseTaskType ,aOwnSign);
	  result.ip = ScheduleUtil.getLocalIP();
	  result.hostName = ScheduleUtil.getLocalHostName();
	  result.managerPort = aManagerPort;
	  result.registerTime = new Timestamp(ScheduleUtil.getCurrentTimeMillis());
	  result.threadNum = aThreadNum;
	  result.heartBeatTime = null;
	  result.dealInfoDesc = "调度初始化";
	  result.version = 0;
	  result.jmxUrl = aJmxUrl;
	  result.uuid = result.ip + "$" + (UUID.randomUUID().toString().replaceAll("-","").toUpperCase());
	  SimpleDateFormat DATA_FORMAT_yyyyMMdd = new SimpleDateFormat("yyMMdd");
	  String s = DATA_FORMAT_yyyyMMdd.format(new Date(ScheduleUtil.getCurrentTimeMillis()));
	  result.id = Long.parseLong(s) * 100000000 + Math.abs(result.uuid.hashCode()% 100000000);
	  return result;
  }

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Timestamp registerTime) {
		this.registerTime = registerTime;
	}
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getHeartBeatTime() {
		return heartBeatTime;
	}

	public void setHeartBeatTime(Timestamp heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}

	public String getDealInfoDesc() {
		return dealInfoDesc;
	}

	public void setDealInfoDesc(String dealInfoDesc) {
		this.dealInfoDesc = dealInfoDesc;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getManagerPort() {
		return managerPort;
	}

	public void setManagerPort(int managerPort) {
		this.managerPort = managerPort;
	}
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getCenterServerTime() {
		return centerServerTime;
	}
	public void setCenterServerTime(Timestamp centerServerTime) {
		this.centerServerTime = centerServerTime;
	}
	public String getJmxUrl() {
		return jmxUrl;
	}
	public void setJmxUrl(String jmxUrl) {
		this.jmxUrl = jmxUrl;
	}
	public String getNextRunStartTime() {
		return nextRunStartTime;
	}
	public void setNextRunStartTime(String nextRunStartTime) {
		this.nextRunStartTime = nextRunStartTime;
	}
	public String getNextRunEndTime() {
		return nextRunEndTime;
	}
	public void setNextRunEndTime(String nextRunEndTime) {
		this.nextRunEndTime = nextRunEndTime;
	}
	public String getOwnSign() {
		return ownSign;
	}
	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}
	public String getBaseTaskType() {
		return baseTaskType;
	}
	public void setBaseTaskType(String baseTaskType) {
		this.baseTaskType = baseTaskType;
	}
	public long getId() {
		return id;
	}

}
