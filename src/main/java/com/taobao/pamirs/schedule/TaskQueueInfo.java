package com.taobao.pamirs.schedule;

/**
 * 任务队列类型
 * @author xuannan
 *
 */
public class TaskQueueInfo {
/**
 * 处理任务类型
 */
  private String taskType;
  
	/**
	 * 原始任务类型
	 */
	private String baseTaskType;

  public String getBaseTaskType() {
		return baseTaskType;
	}

	public void setBaseTaskType(String baseTaskType) {
		this.baseTaskType = baseTaskType;
	}

/**
   * 队列的环境标识
   */
  private String ownSign;
  
  /**
   * 任务队列ID
   */
  private String taskQueueId;
  /**
   * 持有当前任务队列的任务处理器
   */
  private String currentScheduleServer;
  /**
   * 正在申请此任务队列的任务处理器
   */
  private String requestScheduleServer;
  
  /**
   * 数据版本号
   */
  private long version;

public String getTaskType() {
	return taskType;
}

public void setTaskType(String taskType) {
	this.taskType = taskType;
}

public String getTaskQueueId() {
	return taskQueueId;
}

public void setTaskQueueId(String taskQueueId) {
	this.taskQueueId = taskQueueId;
}

public String getCurrentScheduleServer() {
	return currentScheduleServer;
}

public void setCurrentScheduleServer(String currentScheduleServer) {
	this.currentScheduleServer = currentScheduleServer;
}

public String getRequestScheduleServer() {
	return requestScheduleServer;
}

public void setRequestScheduleServer(String requestScheduleServer) {
	this.requestScheduleServer = requestScheduleServer;
}

public long getVersion() {
	return version;
}

public void setVersion(long version) {
	this.version = version;
}

public String getOwnSign() {
	return ownSign;
}

public void setOwnSign(String ownSign) {
	this.ownSign = ownSign;
}
  
  
}
