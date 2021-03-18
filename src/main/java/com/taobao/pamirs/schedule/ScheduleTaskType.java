package com.taobao.pamirs.schedule;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 调度任务类型
 * @author xuannan
 *
 */
public class ScheduleTaskType {
	/**
	 * 任务类型
	 */
	private String baseTaskType;
    /**
     * 向配置中心更新心跳信息的频率
     */
    private long heartBeatRate = 5*1000;//1分钟
    
    /**
     * 判断一个服务器死亡的周期。为了安全，至少是心跳周期的两倍以上
     */
    private long judgeDeadInterval = 1*60*1000;//2分钟
    
    /**
     * 当没有数据的时候，休眠的时间
     * 
     */
    private int sleepTimeNoData = 500;
    
    /**
     * 在每次数据处理晚后休眠的时间
     */
    private int sleepTimeInterval = 0;
    
    
    /**
     * 任务队列数量
     */
    private int taskQueueNumber = -1;
    
    /**
     * 每次获取数据的数量
     */
    private int fetchDataNumber = 500;
    
    /**
     * 在批处理的时候，每次处理的数据量
     */
    private int executeNumber =1;
    
    private int threadNumber = 5;
    
    /**
     * 调度器类型
     */
    private String processorType="NOTSLEEP" ;
    /**
     * 允许执行的开始时间
     */
    private String permitRunStartTime;
    /**
     * 允许执行的开始时间
     */
    private String permitRunEndTime;
    
    /**
     * 清除过期环境信息的时间间隔,以天为单位
     */
    private double expireOwnSignInterval = 1;
    
    /**
     * 处理任务的BeanName
     */
    private String dealBeanName;
    /**
     * 版本号
     */
    private long version;
    
	
	public int getTaskQueueNumber() {
		return taskQueueNumber;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	
	public String getBaseTaskType() {
		return baseTaskType;
	}
	public void setBaseTaskType(String baseTaskType) {
		this.baseTaskType = baseTaskType;
	}
	public void setTaskQueueNumber(int taskQueueNumber) {
		this.taskQueueNumber = taskQueueNumber;
	}
	public long getHeartBeatRate() {
		return heartBeatRate;
	}
	public void setHeartBeatRate(long heartBeatRate) {
		this.heartBeatRate = heartBeatRate;
	}

	public long getJudgeDeadInterval() {
		return judgeDeadInterval;
	}

	public void setJudgeDeadInterval(long judgeDeadInterval) {
		this.judgeDeadInterval = judgeDeadInterval;
	}

	public int getFetchDataNumber() {
		return fetchDataNumber;
	}

	public void setFetchDataNumber(int fetchDataNumber) {
		this.fetchDataNumber = fetchDataNumber;
	}

	public int getExecuteNumber() {
		return executeNumber;
	}

	public void setExecuteNumber(int executeNumber) {
		this.executeNumber = executeNumber;
	}

	public int getSleepTimeNoData() {
		return sleepTimeNoData;
	}

	public void setSleepTimeNoData(int sleepTimeNoData) {
		this.sleepTimeNoData = sleepTimeNoData;
	}

	public int getSleepTimeInterval() {
		return sleepTimeInterval;
	}

	public void setSleepTimeInterval(int sleepTimeInterval) {
		this.sleepTimeInterval = sleepTimeInterval;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public String getPermitRunStartTime() {
		return permitRunStartTime;
	}

	public String getProcessorType() {
		return processorType;
	}

	public void setProcessorType(String processorType) {
		this.processorType = processorType;
	}

	public void setPermitRunStartTime(String permitRunStartTime) {
		this.permitRunStartTime = permitRunStartTime;
		if(this.permitRunStartTime != null && this.permitRunStartTime.trim().length() ==0){
			this.permitRunStartTime = null;
		}	
	}

	public String getPermitRunEndTime() {
		return permitRunEndTime;
	}

	public double getExpireOwnSignInterval() {
		return expireOwnSignInterval;
	}
	public void setExpireOwnSignInterval(double expireOwnSignInterval) {
		this.expireOwnSignInterval = expireOwnSignInterval;
	}
	
	public String getDealBeanName() {
		return dealBeanName;
	}
	public void setDealBeanName(String dealBeanName) {
		this.dealBeanName = dealBeanName;
	}
	public void setPermitRunEndTime(String permitRunEndTime) {
		this.permitRunEndTime = permitRunEndTime;
		this.permitRunEndTime = permitRunEndTime;
		if(this.permitRunEndTime != null && this.permitRunEndTime.trim().length() ==0){
			this.permitRunEndTime = null;
		}	

	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
