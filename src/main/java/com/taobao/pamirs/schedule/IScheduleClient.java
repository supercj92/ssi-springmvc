
package com.taobao.pamirs.schedule;

import java.util.List;

import javax.jws.WebService;

import com.taobao.pamirs.schedule.ScheduleServer;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleTaskTypeRunningInfo;
import com.taobao.pamirs.schedule.TaskQueueInfo;

/**
 * 
 * 类名称：IScheduleClient
 * 类描述：提供Web服务的接口类
 * 创建人：jishao
 * 创建时间：2010-12-29 上午10:43:49
 * 
 */
@WebService
public interface IScheduleClient {
	public List<ScheduleTaskType> getAllTaskTypeBaseInfo()throws Exception ;

	/**
	 * 清除一个任务类型的运行期信息
	 * @param baseTaskType
	 * @throws Exception
	 */
	public void clearTaskType(String baseTaskType) throws Exception;
	/**
	 *  创建一个新的任务类型
	 * @param baseTaskType
	 * @param queueIds
	 * @throws Exception
	 */
    public void createBaseTaskType(ScheduleTaskType baseTaskType, String[] queueIds) throws Exception ;
    public List<ScheduleTaskTypeRunningInfo> getAllTaskTypeRunningInfo(String baseTaskType) throws Exception;

    /**
     * 删除一个任务类型
     * @param baseTaskType
     * @throws Exception
     */
    public void deleteTaskType(String baseTaskType) throws Exception;
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
	 * 获取所有的有效服务器信息
	 *
	 * @param taskInfo
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectAllValidScheduleServer(String taskType) throws Exception;

	/**
	 * 根据条件查询当前调度服务
	 * @param baseTaskType
	 * @param ownSign
	 * @param ip
	 * @param orderStr
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectScheduleServer(String baseTaskType, String ownSign, String ip, String orderStr)
			throws Exception;

	/**
	 * 查询调度服务的历史记录
	 * @param baseTaskType
	 * @param ownSign
	 * @param ip
	 * @param orderStr
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectHistoryScheduleServer(String baseTaskType, String ownSign, String ip,
        String orderStr)
	throws Exception;

}
