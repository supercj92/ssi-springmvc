package com.taobao.pamirs.schedule;

/**
 * 调度服务器的调度监控接口
 * @author xuannan
 *
 */
public interface IScheduleAlert {
  /**
   * 超过10个心跳周期还没有获取到调度队列
   * @param taskType
   * @param scheduleServerUUID
   * @param message
   */
	public void noTaskQueue(String taskType, String scheduleServerUUID, String message);

	/**
	 * 超过10个心跳周期，还没有进行重新装载操作
	 * @param taskType
	 * @param scheduleServerUUID
	 * @param message
	 */
	public void noReloadTaskQueue(String taskType, String scheduleServerUUID, String message);

}
