package com.taobao.pamirs.schedule;

import java.util.List;

import com.taobao.pamirs.schedule.IScheduleClient;
import com.taobao.pamirs.schedule.ScheduleServer;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleTaskTypeRunningInfo;
import com.taobao.pamirs.schedule.TaskQueueInfo;

/**
 * 调度配置中心客户端接口，可以有基于数据库的实现，可以有基于ConfigServer的实现
 * 
 * @author xuannan
 * 
 */
public interface IScheduleConfigCenterClient extends IScheduleClient {
	/**
	 * 重新装载当前server需要处理的数据队列
	 * 
	 * @param taskType
	 *            任务类型
	 * @param uuid
	 *            当前server的UUID
	 * @return
	 * @throws Exception
	 */
	public List<String> reloadDealQueue(String taskType, String uuid) throws Exception;

	/**
	 * 装载所有的任务队列信息
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	public List<TaskQueueInfo> loadAllQueue(String taskType) throws Exception;

	/**
	 * 释放自己把持，别人申请的队列
	 *
	 * @param taskType
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public void releaseDealQueue(String taskType, String uuid) throws Exception;

	/**
	 * 获取一共任务类型的处理队列数量
	 *
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	public int queryQueueCount(String taskType) throws Exception;

	/**
	 * 装载任务类型相关信息
	 *
	 * @param taskType
	 * @throws Exception
	 */
	public ScheduleTaskType loadTaskTypeBaseInfo(String taskType) throws Exception;

	/**
	 * 装载任务类型运行期信息
	 *
	 * @param taskType
	 * @param ownSign
	 * @return
	 * @throws Exception
	 */
	public ScheduleTaskTypeRunningInfo loadTaskTypeRunningInfo(String baseTaskType, String ownSign, String serverUUID) throws Exception;

	/**
	 * 锁定调度信息，然后开始重新进行调度服务器的分配
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	//public void lockTaskTypeBaseInfo(String taskType, String lockServerUuid) throws Exception;

	/**
	 * 锁定制定环境的任务信息
	 * @param taskType
	 * @param ownSign
	 * @param lockServerUuid
	 * @throws Exception
	 */
	public void lockTaskTypeRunningInfo(String taskType, String lockServerUuid) throws Exception;
	/**
	 * 清除已经过期的调度服务器信息
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	public int clearExpireScheduleServer(ScheduleTaskType taskInfo, ScheduleTaskTypeRunningInfo runningInfo) throws Exception;

	/**
	 * 清除PAMIRS_SCHEDULE_QUEUE中的信息，服务器已经不存在的时候
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	public int clearTaskQueueInfo(String taskType, List<ScheduleServer> serverList) throws Exception;

	/**
	 * 获取所有的有效服务器信息
	 *
	 * @param taskInfo
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectAllValidScheduleServer(String taskType) throws Exception;

	/**
	 * 重新分配任务处理队列
	 *
	 * @param taskType
	 * @param serverList
	 * @throws Exception
	 */
	public void assignQueue(String taskType, String currentUuid, List<ScheduleServer> serverList) throws Exception;

	/**
	 * 发送心跳信息
	 *
	 * @param server
	 * @throws Exception
	 */
	public void refreshScheduleServer(ScheduleServer server) throws Exception;

	/**
	 * 注册服务器
	 *
	 * @param server
	 * @throws Exception
	 */
	public void registerScheduleServer(ScheduleServer server) throws Exception;

	/**
	 * 注销服务器
	 * @param serverUUID
	 * @throws Exception
	 */
	public void unRegisterScheduleServer(String serverUUID) throws Exception;
	/**
	 * 清除已经过期的OWN_SIGN的自动生成的数据
	 * @param taskType 任务类型
	 * @param serverUUID 服务器
	 * @param expireDateInternal 过期时间，以天为单位
	 * @throws Exception
	 */
	public void clearExpireTaskTypeRunningInfo(String baseTaskType, String serverUUID, double expireDateInternal)throws Exception;

}
