package com.taobao.pamirs.schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.taobao.pamirs.schedule.ScheduleServer;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleTaskTypeRunningInfo;
import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.TBScheduleManager;
import com.taobao.pamirs.schedule.TaskQueueInfo;

/**
 * 基于数据库的调度配置中心客户端
 * 
 * @author xuannan
 * 
 */
public class ScheduleConfigCenterClientInner {
	Map<String, String> tableMap;
	/**
	 * 配置中心的数据可类型
	 */
	String dataBaseType = null;

	String backServerSql = null;
	
	protected ScheduleConfigCenterClientInner(String aDataBaseType,
			Map<String, String> aTableMap) {
		this.dataBaseType = aDataBaseType;
		this.tableMap = aTableMap;
	}

	public String getBackServerSql(Connection conn) {
		if (backServerSql == null) {
			synchronized (this) {
				if (backServerSql == null) {
					backServerSql = "insert into "
							+ transferTableName(conn,"PAMIRS_SCHEDULE_SERVER_HIS")
							+ "(ID,UUID,TASK_TYPE,OWN_SIGN,BASE_TASK_TYPE,IP,HOST_NAME,MANAGER_PORT,THREAD_NUM,REGISTER_TIME,"
							+ "HEARTBEAT_TIME,VERSION,JMX_URL,DEALINFO_DESC,NEXT_RUN_START_TIME,"
							+ "NEXT_RUN_END_TIME,GMT_CREATE,GMT_MODIFIED) "
							+ "SELECT ID,UUID,TASK_TYPE,OWN_SIGN,BASE_TASK_TYPE,IP,HOST_NAME,MANAGER_PORT,THREAD_NUM,REGISTER_TIME,"
							+ "HEARTBEAT_TIME,VERSION,JMX_URL,DEALINFO_DESC,NEXT_RUN_START_TIME,"
							+ "NEXT_RUN_END_TIME,GMT_CREATE,GMT_MODIFIED FROM "
							+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER");
				}
			}
		}
		return backServerSql;
	}
	/**
	 * 判断一种类型的任务是否有服务器在运行
	 * @param conn
	 * @param baseTaskType
	 * @return
	 * @throws Exception
	 */
	public boolean hasRunningServer(Connection conn,String baseTaskType) throws Exception {
		    long deadIntervale = 60 * 1000;
			double deadInterval = 0;
			String dataType = this.getDataBaseType(conn);
			String sql ="SELECT COUNT(*) as num FROM " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER");
			if ("oracle".equalsIgnoreCase(dataType)) {
				sql = sql +   "  WHERE BASE_TASK_TYPE = ? "
						+ "  AND (sysdate - HEARTBEAT_TIME) < ? ";
				deadInterval = deadIntervale * 1.0
						/ (24 * 3600 * 1000);
			} else if ("mysql".equalsIgnoreCase(dataType)) {
				sql = sql +   "  WHERE BASE_TASK_TYPE = ? "
						+ "  AND (UNIX_TIMESTAMP(now()) -UNIX_TIMESTAMP( HEARTBEAT_TIME)) < ? ";
				deadInterval = deadIntervale / 1000.0;
			} else {
				throw new Exception("不支持的数据库类型：" + dataType);
			}

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1,baseTaskType);
			statement.setDouble(2, deadInterval);
			ResultSet set = statement.executeQuery();
			set.next();
			int num = set.getInt("num");
			statement.close();
			if(num > 0){
				return true;
			}else{
				return false;
			}
	}

/**
 * 删除一个任务类型的所有信息
 * @param conn
 * @param baseTaskType
 * @throws Exception
 */
public void deleteTaskType(Connection conn,String baseTaskType) throws Exception{
	
	if(this.hasRunningServer(conn, baseTaskType) == true){
		throw new Exception("任务：" + baseTaskType + "还有服务器在运行，不能删除任务");
	}
	
	String[] sqls = { 
			this.getBackServerSql(conn) + " where BASE_TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_TASKTYPE") +" where TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN") +" where BASE_TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE") +" where BASE_TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER") +" where BASE_TASK_TYPE = ? "};
	PreparedStatement deleteStatement = null;
	for(String sql :sqls){
		deleteStatement = conn.prepareStatement( sql);
		deleteStatement.setString(1,baseTaskType);
		deleteStatement.executeUpdate();
		deleteStatement.close();
	}
}

/**
 * 清除一个任务类型的运行期信息
 * @param conn
 * @param baseTaskType
 * @throws Exception
 */
public void clearTaskType(Connection conn,String baseTaskType) throws Exception{
	if(this.hasRunningServer(conn, baseTaskType) == true){
		throw new Exception("任务：" + baseTaskType + "还有服务器在运行，不能删除清除信息");
	}
	String[] sqls = {
			this.getBackServerSql(conn) + " where BASE_TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN") +" where BASE_TASK_TYPE = ? ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE") +" where BASE_TASK_TYPE = ? AND OWN_SIGN <> 'BASE' ",
			"delete from " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER") +" where BASE_TASK_TYPE = ? "};
	PreparedStatement deleteStatement = null;
	for(String sql :sqls){
		deleteStatement = conn.prepareStatement( sql);
		deleteStatement.setString(1,baseTaskType);
		deleteStatement.executeUpdate();
		deleteStatement.close();
	}
}

/**
 * 创建一个新的任务
 * @param conn
 * @param baseTaskType
 * @param queueIds
 * @throws Exception
 */
public void createBaseTaskType(Connection conn,
		ScheduleTaskType baseTaskType,String[] queueIds) throws Exception {
	String insertSql = "insert into "
			+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTYPE")
			+ "(ID,TASK_TYPE,DEAL_BEAN_NAME,HEARTBEAT_RATE,JUDGE_DEAD_INTERVAL,"
			+ "THREAD_NUMBER,EXECUTE_NUMBER,FETCH_NUMBER,SLEEP_TIME_NODATA,"
			+ "PROCESSOR_TYPE,PERMIT_RUN_START_TIME,"
			+ "PERMIT_RUN_END_TIME,SLEEP_TIME_INTERVAL,"
			+ "EXPIRE_OWN_SIGN_INTERVAL,GMT_CREATE,GMT_MODIFIED) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
			+ getDataBaseSysdateString(conn) + ","
			+ getDataBaseSysdateString(conn) + ")";
	PreparedStatement insertStatement = conn.prepareStatement(insertSql);
	long id = Math.abs((long) baseTaskType.hashCode()
			+ (long) UUID.randomUUID().hashCode());
	insertStatement.setLong(1, id);
	insertStatement.setString(2, baseTaskType.getBaseTaskType());
	insertStatement.setString(3, baseTaskType.getDealBeanName());
	insertStatement.setDouble(4, baseTaskType.getHeartBeatRate()/1000.0);
	insertStatement.setDouble(5, baseTaskType.getJudgeDeadInterval()/1000.0);
	insertStatement.setLong(6, baseTaskType.getThreadNumber());
	insertStatement.setLong(7, baseTaskType.getExecuteNumber());
	insertStatement.setLong(8, baseTaskType.getFetchDataNumber());
	insertStatement.setDouble(9, baseTaskType.getSleepTimeNoData()/1000.0);
	insertStatement.setString(10, baseTaskType.getProcessorType());
	insertStatement.setString(11, baseTaskType.getPermitRunStartTime());
	insertStatement.setString(12, baseTaskType.getPermitRunEndTime());
	insertStatement.setDouble(13, baseTaskType.getSleepTimeInterval()/1000.0);
	insertStatement.setDouble(14, baseTaskType.getExpireOwnSignInterval());

	insertStatement.executeUpdate();
	insertStatement.close();
	this.createScheduleQueue(conn, baseTaskType.getBaseTaskType(), "BASE", queueIds);
}

public List<ScheduleTaskTypeRunningInfo> getAllTaskTypeRunningInfo(Connection conn,String baseTaskType) throws Exception {

	List<ScheduleTaskTypeRunningInfo> resultList = new ArrayList<ScheduleTaskTypeRunningInfo>();
		String sql = "SELECT ID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,LAST_ASSIGN_TIME,LAST_ASSIGN_UUID,GMT_CREATE,GMT_MODIFIED "
				+ " from "
				+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN")
				+ " where BASE_TASK_TYPE = ? order by TASK_TYPE ";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, baseTaskType);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			ScheduleTaskTypeRunningInfo result = new ScheduleTaskTypeRunningInfo();
			result.setId(resultSet.getLong("ID"));
			result.setTaskType(resultSet.getString("TASK_TYPE"));
			result.setOwnSign(resultSet.getString("OWN_SIGN"));
			result.setBaseTaskType(resultSet.getString("BASE_TASK_TYPE"));
			result.setLastAssignTime(resultSet
					.getTimestamp("LAST_ASSIGN_TIME"));
			result.setLastAssignUUID(resultSet
					.getString("LAST_ASSIGN_UUID"));
			result.setGmtCreate(resultSet.getTimestamp("GMT_CREATE"));
			result.setGmtModified(resultSet.getTimestamp("GMT_MODIFIED"));
			resultList.add(result);
		}
		resultSet.close();
		statement.close();
		return resultList;
}

	/**
	 * 重新装载当前server需要处理的数据队列
	 *
	 * @param taskType
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public List<String> reloadDealQueue(Connection conn,String taskType, String uuid)
			throws Exception {
		List<String> result = new ArrayList<String>();
			String sqlQueue = " SELECT TASK_TYPE,QUEUE_ID,CUR_SERVER,REQ_SERVER,OWN_SIGN FROM "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ " WHERE TASK_TYPE = ? and CUR_SERVER = ? ORDER BY QUEUE_ID";
			PreparedStatement stmtQueue = conn.prepareStatement(sqlQueue);
			stmtQueue.setString(1, taskType);
			stmtQueue.setString(2, uuid);
			ResultSet setQueue = stmtQueue.executeQuery();
			while (setQueue.next()) {
				result.add(setQueue.getString("QUEUE_ID"));
			}
			setQueue.close();
			stmtQueue.close();
		return result;
	}

	public List<TaskQueueInfo> loadAllQueue(Connection conn,String taskType) throws Exception {
		List<TaskQueueInfo> result = new ArrayList<TaskQueueInfo>();
		TaskQueueInfo item;
			String sqlQueue = " SELECT TASK_TYPE,BASE_TASK_TYPE,QUEUE_ID,CUR_SERVER,REQ_SERVER,OWN_SIGN FROM "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ " WHERE TASK_TYPE = ?  ORDER BY QUEUE_ID";
			PreparedStatement stmtQueue = conn.prepareStatement(sqlQueue);
			stmtQueue.setString(1, taskType);
			ResultSet setQueue = stmtQueue.executeQuery();
			while (setQueue.next()) {
				item = new TaskQueueInfo();
				item.setTaskType(setQueue.getString("TASK_TYPE"));
				item.setBaseTaskType(setQueue.getString("BASE_TASK_TYPE"));
				item.setTaskQueueId(setQueue.getString("QUEUE_ID"));
				item.setCurrentScheduleServer(setQueue.getString("CUR_SERVER"));
				item.setRequestScheduleServer(setQueue.getString("REQ_SERVER"));
				item.setOwnSign(setQueue.getString("OWN_SIGN"));
				result.add(item);
			}
			setQueue.close();
			stmtQueue.close();
		return result;
	}

	/**
	 * 释放自己把持，别人申请的队列
	 *
	 * @param taskType
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public void releaseDealQueue(Connection conn,String taskType, String uuid) throws Exception {
		String querySql = "select QUEUE_ID from "
			+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
			+ " WHERE TASK_TYPE = ? and CUR_SERVER = ?  AND  REQ_SERVER IS NOT NULL ";
		PreparedStatement stmtQueue = conn.prepareStatement(querySql);
		stmtQueue.setString(1, taskType);
		stmtQueue.setString(2, uuid);
		ResultSet set = stmtQueue.executeQuery();
		List<String> queueIds = new ArrayList<String>();
		while(set.next()){
			queueIds.add(set.getString("QUEUE_ID"));
		}
        set.close();
        stmtQueue.close();

		String sqlQueue = " update "
			+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
			+ " set CUR_SERVER = REQ_SERVER,REQ_SERVER = NULL, GMT_MODIFIED = "
			+ getDataBaseSysdateString(conn)
			+ " WHERE TASK_TYPE = ? and CUR_SERVER = ? AND QUEUE_ID = ?  AND  REQ_SERVER IS NOT NULL ";

		for(String queueId:queueIds){
			stmtQueue = conn.prepareStatement(sqlQueue);
			stmtQueue.setString(1, taskType);
			stmtQueue.setString(2, uuid);
			stmtQueue.setString(3, queueId);
			stmtQueue.executeUpdate();
			stmtQueue.close();
			conn.commit();
		}
	}


	/**
	 * 获取一共任务类型的处理队列数量
	 *
	 * @param taskType
	 * @return
	 * @throws Exception
	 */
	public int queryQueueCount(Connection conn,String taskType) throws Exception {
		int result = 0;
			String sqlQueue = " SELECT count(*) as num FROM "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ " WHERE TASK_TYPE = ? ";
			PreparedStatement stmtQueue = conn.prepareStatement(sqlQueue);
			stmtQueue.setString(1, taskType);
			ResultSet setQueue = stmtQueue.executeQuery();
			if (setQueue.next()) {
				result = setQueue.getInt("num");
			}
			setQueue.close();
			stmtQueue.close();
		return result;
	}

	/**
	 * 装载任务运行期信息
	 * @param taskType
	 * @param ownSign
	 * @return
	 * @throws Exception
	 */
	public ScheduleTaskTypeRunningInfo loadTaskTypeRunningInfo(
			Connection conn,String baseTaskType, String ownSign, String serverUUID)
			throws Exception {

		//lock基本信息，需要判断，如果没有取到，则需要新建
		this.lockTaskTypeBaseInfoPrivate(conn,baseTaskType, serverUUID);

		ScheduleTaskTypeRunningInfo result = new ScheduleTaskTypeRunningInfo();
		boolean hasTaskTypeRunningInfo = true;
			String sql = "SELECT ID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,LAST_ASSIGN_TIME,LAST_ASSIGN_UUID,GMT_CREATE,GMT_MODIFIED "
					+ " from "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN")
					+ " where BASE_TASK_TYPE = ? AND OWN_SIGN =? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, baseTaskType);
			statement.setString(2, ownSign);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				result.setId(resultSet.getLong("ID"));
				result.setTaskType(resultSet.getString("TASK_TYPE"));
				result.setOwnSign(resultSet.getString("OWN_SIGN"));
				result.setBaseTaskType(resultSet.getString("BASE_TASK_TYPE"));
				result.setLastAssignTime(resultSet
						.getTimestamp("LAST_ASSIGN_TIME"));
				result.setLastAssignUUID(resultSet
						.getString("LAST_ASSIGN_UUID"));
				result.setGmtCreate(resultSet.getTimestamp("GMT_CREATE"));
				result.setGmtModified(resultSet.getTimestamp("GMT_MODIFIED"));
			} else {
				hasTaskTypeRunningInfo = false;
			}
			resultSet.close();
			statement.close();
		if (hasTaskTypeRunningInfo == false) {
			//创建当前环境的专用任务信息
			result = this.createScheduleTaskTypeRunningInfo(conn,baseTaskType,
					ownSign, serverUUID);
		}
		//检查，并创建PAMIRS_SCHEDULE_QUEUE表数据
		checkAndCreateQueue(conn,baseTaskType, ownSign);

		return result;
	}

	private void checkAndCreateQueue(Connection conn,String baseTaskType, String ownSign)
			throws Exception {
		List<TaskQueueInfo> baseQueues = loadAllQueue(conn,baseTaskType);
		if (baseQueues.size() == 0) {
			throw new Exception("没有为任务类型：" + baseTaskType
					+ "配置OWN_SIGN ='BASE' 的基础任务队列");
		}
		String[] baseQueueIds = new String[baseQueues.size()];
		for(int i=0;i<baseQueueIds.length;i++){
			baseQueueIds[i] = baseQueues.get(i).getTaskQueueId();
		}
		List<TaskQueueInfo> ownSignQueues = loadAllQueue(conn,TBScheduleManager
				.getTaskTypeByBaseAndOwnSign(baseTaskType, ownSign));
		if (ownSignQueues.size() == 0) {
			//还没有创建任务队列，
			createScheduleQueue(conn,baseTaskType, ownSign, baseQueueIds);
		} else {
			String errorMessage = "任务：" + baseTaskType + ",OWN_SIGN=" + ownSign
					+ "的队列数据配置与OWN_SIGN=" + TBScheduleManager.OWN_SIGN_BASE
					+ "的配置数据不一致，请先停止" + ownSign + "的所有调度服务，并删除数据后重新启动";
			if (baseQueues.size() != ownSignQueues.size()) {
				throw new Exception("数据条数不对:" + errorMessage);
			}
			for (int i = 0; i < baseQueues.size(); i++) {
				TaskQueueInfo base = baseQueues.get(i);
				TaskQueueInfo own = ownSignQueues.get(i);
				if (base.getTaskQueueId().equals(own.getTaskQueueId()) == false) {
					throw new Exception("数据不匹配QUEUE_ID "
							+ base.getTaskQueueId() + " != "
							+ own.getTaskQueueId() + ":" + errorMessage);
				}
			}
		}
	}

	private ScheduleTaskTypeRunningInfo createScheduleTaskTypeRunningInfo(
			Connection conn,String baseTaskType, String ownSign, String serverUUID)
			throws Exception {
		//		if(ownSign.equals(TBScheduleManager.OWN_SIGN_BASE) == true){
		//			throw new Exception("不能设置当前环境为 OWN_SIGN=" + TBScheduleManager.OWN_SIGN_BASE +"");
		//		}
		ScheduleTaskTypeRunningInfo result = new ScheduleTaskTypeRunningInfo();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			// 需要创建一个当前环境下的运行期任务信息
			String insertSql = "insert into "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN")
					+ "(ID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,LAST_ASSIGN_TIME,LAST_ASSIGN_UUID,GMT_CREATE,GMT_MODIFIED) "
					+ "values(?,?,?,?," + getDataBaseSysdateString(conn)
					+ ",?," + getDataBaseSysdateString(conn) + ","
					+ getDataBaseSysdateString(conn) + ")";
			PreparedStatement insertStatement = conn
					.prepareStatement(insertSql);
			long id = Math.abs((long) TBScheduleManager.getTaskTypeByBaseAndOwnSign(
					baseTaskType, ownSign).hashCode())
					+ Math.abs((long) UUID.randomUUID().hashCode());
			insertStatement.setLong(1, id);
			insertStatement.setString(2, TBScheduleManager
					.getTaskTypeByBaseAndOwnSign(baseTaskType, ownSign));
			insertStatement.setString(3, baseTaskType);
			insertStatement.setString(4, ownSign);
			insertStatement.setString(5, serverUUID);
			insertStatement.executeUpdate();
			insertStatement.close();

			// 构建对象
			result.setId(id);
			result.setTaskType(TBScheduleManager.getTaskTypeByBaseAndOwnSign(
					baseTaskType, ownSign));
			result.setBaseTaskType(baseTaskType);
			result.setOwnSign(ownSign);
			result.setLastAssignTime(now);
			result.setLastAssignUUID(serverUUID);
			result.setGmtCreate(now);
			result.setGmtModified(now);
		return result;
	}

	/**
	 * 创建一个当前环境下的运行期任务信息
	 * @param taskType
	 * @param ownSign
	 * @throws Exception
	 */
	public void createScheduleQueue(Connection conn,String baseTaskType, String ownSign,
			String[] baseQueueIds) throws Exception {
			String insertSql = "insert into "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ "(ID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,QUEUE_ID,GMT_CREATE,GMT_MODIFIED) "
					+ "values(?,?,?,?,?," + getDataBaseSysdateString(conn)
					+ "," + getDataBaseSysdateString(conn) + ")";
			PreparedStatement insertStatement = conn
					.prepareStatement(insertSql);
			for (String queueId : baseQueueIds) {
				long id = Math.abs((long) TBScheduleManager
						.getTaskTypeByBaseAndOwnSign(baseTaskType, ownSign)
						.hashCode()
						+ (long) UUID.randomUUID().hashCode());
				insertStatement.setLong(1, id);
				insertStatement.setString(2, TBScheduleManager
						.getTaskTypeByBaseAndOwnSign(baseTaskType, ownSign));
				insertStatement.setString(3, baseTaskType);
				insertStatement.setString(4, ownSign);
				insertStatement.setString(5, queueId);
				insertStatement.addBatch();
			}
			insertStatement.executeBatch();
			insertStatement.close();
	}

	/**
	 * 装载任务类型相关信息
	 *
	 * @param taskType
	 * @throws Exception
	 */
	public ScheduleTaskType loadTaskTypeBaseInfo(Connection conn,String baseTaskType)
			throws Exception {
		ScheduleTaskType result = new ScheduleTaskType();
			String sql = " SELECT TASK_TYPE,LAST_ASSIGN_TIME,LAST_ASSIGN_UUID,HEARTBEAT_RATE,JUDGE_DEAD_INTERVAL,"
					+ "PROCESSOR_TYPE,THREAD_NUMBER,FETCH_NUMBER,EXECUTE_NUMBER,"
					+ "SLEEP_TIME_NODATA,SLEEP_TIME_INTERVAL,PERMIT_RUN_START_TIME,PERMIT_RUN_END_TIME,EXPIRE_OWN_SIGN_INTERVAL,DEAL_BEAN_NAME "
					+ " from "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTYPE")
					+ " where TASK_TYPE = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, baseTaskType);
			ResultSet resultSet = statement.executeQuery();
			boolean hasInfo = true;
			if (resultSet.next()) {
				result.setBaseTaskType(resultSet.getString("TASK_TYPE"));
				result
						.setHeartBeatRate(resultSet.getLong("HEARTBEAT_RATE") * 1000);
				result.setJudgeDeadInterval(resultSet
						.getLong("JUDGE_DEAD_INTERVAL") * 1000);
				result.setProcessorType(resultSet.getString("PROCESSOR_TYPE"));
				result.setSleepTimeNoData((int) (resultSet
						.getFloat("SLEEP_TIME_NODATA") * 1000));
				result.setSleepTimeInterval((int) (resultSet
						.getFloat("SLEEP_TIME_INTERVAL") * 1000));
				result.setThreadNumber(resultSet.getInt("THREAD_NUMBER"));
				result.setFetchDataNumber(resultSet.getInt("FETCH_NUMBER"));
				result.setExecuteNumber(resultSet.getInt("EXECUTE_NUMBER"));
				result.setPermitRunStartTime(resultSet
						.getString("PERMIT_RUN_START_TIME"));
				result.setPermitRunEndTime(resultSet
						.getString("PERMIT_RUN_END_TIME"));
				result.setDealBeanName(resultSet.getString("DEAL_BEAN_NAME"));
				double tmpFloat = resultSet
						.getDouble("EXPIRE_OWN_SIGN_INTERVAL");
				if (tmpFloat <= 0) {
					tmpFloat = 1;
				}
				result.setExpireOwnSignInterval(tmpFloat);
			} else {
				hasInfo = false;
			}
			resultSet.close();
			statement.close();
			if (hasInfo == false) {
				throw new Exception("没有查询到任务类型相关的信息，TASK_TYPE=" + baseTaskType);
			}
		return result;
	}
    /**
     * 获取所有的任务类型
     * @param conn
     * @return
     * @throws Exception
     */
	public List<ScheduleTaskType> getAllTaskTypeBaseInfo(Connection conn)
			throws Exception {
		List<ScheduleTaskType> resultList = new ArrayList<ScheduleTaskType>();
		String sql = " SELECT TASK_TYPE,LAST_ASSIGN_TIME,LAST_ASSIGN_UUID,HEARTBEAT_RATE,JUDGE_DEAD_INTERVAL,"
				+ "PROCESSOR_TYPE,THREAD_NUMBER,FETCH_NUMBER,EXECUTE_NUMBER,"
				+ "SLEEP_TIME_NODATA,SLEEP_TIME_INTERVAL,PERMIT_RUN_START_TIME,PERMIT_RUN_END_TIME,EXPIRE_OWN_SIGN_INTERVAL,DEAL_BEAN_NAME "
				+ " from "
				+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTYPE")
				+ " order by TASK_TYPE ";
		PreparedStatement statement = conn.prepareStatement(sql);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			ScheduleTaskType result = new ScheduleTaskType();
			result.setBaseTaskType(resultSet.getString("TASK_TYPE"));
			result.setHeartBeatRate(resultSet.getLong("HEARTBEAT_RATE") * 1000);
			result.setJudgeDeadInterval(resultSet
					.getLong("JUDGE_DEAD_INTERVAL") * 1000);
			result.setProcessorType(resultSet.getString("PROCESSOR_TYPE"));
			result.setSleepTimeNoData((int) (resultSet
					.getFloat("SLEEP_TIME_NODATA") * 1000));
			result.setSleepTimeInterval((int) (resultSet
					.getFloat("SLEEP_TIME_INTERVAL") * 1000));
			result.setThreadNumber(resultSet.getInt("THREAD_NUMBER"));
			result.setFetchDataNumber(resultSet.getInt("FETCH_NUMBER"));
			result.setExecuteNumber(resultSet.getInt("EXECUTE_NUMBER"));
			result.setPermitRunStartTime(resultSet
					.getString("PERMIT_RUN_START_TIME"));
			result.setPermitRunEndTime(resultSet
					.getString("PERMIT_RUN_END_TIME"));
			result.setDealBeanName(resultSet.getString("DEAL_BEAN_NAME"));
			double tmpFloat = resultSet.getDouble("EXPIRE_OWN_SIGN_INTERVAL");
			if (tmpFloat <= 0) {
				tmpFloat = 1;
			}
			result.setExpireOwnSignInterval(tmpFloat);
			resultList.add(result);
		}
		resultSet.close();
		statement.close();
		return resultList;
	}

	/**
	 * 锁定调度信息，然后开始重新进行调度服务器的分配
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	private void lockTaskTypeBaseInfoPrivate(Connection conn,String baseTaskType,
			String lockServerUuid) throws Exception {
			String sql = " UPDATE "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTYPE")
					+ " set LAST_ASSIGN_TIME = "
					+ getDataBaseSysdateString(conn)
					+ ",LAST_ASSIGN_UUID = ? , GMT_MODIFIED = "
					+ getDataBaseSysdateString(conn) + " where TASK_TYPE = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, lockServerUuid);
			statement.setString(2, baseTaskType);
			statement.executeUpdate();
			statement.close();
	}

	public void lockTaskTypeRunningInfo(Connection conn,String taskType, String lockServerUuid)
			throws Exception {
			String sql = " UPDATE "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN")
					+ " set LAST_ASSIGN_TIME = "
					+ getDataBaseSysdateString(conn)
					+ ",LAST_ASSIGN_UUID = ? , GMT_MODIFIED = "
					+ getDataBaseSysdateString(conn) + " where TASK_TYPE = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, lockServerUuid);
			statement.setString(2, taskType);
			statement.executeUpdate();
			statement.close();
	}

	/**
	 * 清除任务过期无用的环境的自动生成的信息。超过2天没有服务器使用的，视为过去。如果再次启动，会自动创建
	 * @param taskType
	 * @throws Exception
	 */
	public void clearExpireTaskTypeRunningInfo(Connection conn,String baseTaskType,
			String serverUUID, double expireDateInternal) throws Exception {
		if (expireDateInternal < 1 ) {
			//	throw new Exception("清除失效无用环境的时间间隔不能小于1个小时,当前设置是：" + expireDateInternal);
		}
		this.lockTaskTypeBaseInfoPrivate(conn,baseTaskType, serverUUID);
			//查询最近两天内还在工作的TASK_TYPE,OWN_SIGN组合
			String sql = "SELECT DISTINCT T.TASK_TYPE,T.OWN_SIGN " + " FROM "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
					+ " P ,"
					+ transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN")
					+ " T " + " WHERE T.BASE_TASK_TYPE = ? "
					+ " AND T.OWN_SIGN = P.OWN_SIGN "
					+ " AND T.TASK_TYPE = P.TASK_TYPE "
					+ " AND P.HEARTBEAT_TIME > ? ";
			PreparedStatement stmt = conn.prepareStatement(sql);
			Timestamp expireDate = new Timestamp(System.currentTimeMillis()
					- (long) (expireDateInternal * 24 * 3600 * 1000));
			stmt.setString(1, baseTaskType);
			stmt.setTimestamp(2, expireDate);
			ResultSet rs = stmt.executeQuery();
			List<String> ownSignlist = new ArrayList<String>();
			while (rs.next()) {
				ownSignlist.add(rs.getString("OWN_SIGN"));
			}
			rs.close();
			stmt.close();

			//删除过期的PAMIRS_SCHEDULE_SERVER,PAMIRS_SCHEDULE_TASKTRUN表数据

			String[] baseSqls = new String[] {
					this.getBackServerSql(conn) + " where BASE_TASK_TYPE = ? ",//备份数据,顺序不能颠倒
					"DELETE FROM " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER") +"  WHERE BASE_TASK_TYPE = ? ",
					"DELETE FROM " + transferTableName(conn, "PAMIRS_SCHEDULE_TASKTRUN") +"  WHERE BASE_TASK_TYPE = ? "
				};
			for (String baseSql : baseSqls) {
				String exeSql = baseSql;
				if (ownSignlist.size() > 0) {
					exeSql = exeSql + " AND OWN_SIGN NOT IN (";
					for (int i = 0; i < ownSignlist.size(); i++) {
						if (i > 0) {
							exeSql = exeSql + ",";
						}
						exeSql = exeSql + "?";
					}
					exeSql = exeSql + ")";
				}
				PreparedStatement deleteQueueStmt = conn
						.prepareStatement(exeSql);
				deleteQueueStmt.setString(1, baseTaskType);
				if (ownSignlist.size() > 0) {
					for (int i = 0; i < ownSignlist.size(); i++) {
						deleteQueueStmt.setString(i + 2, ownSignlist.get(i));
					}
				}
				deleteQueueStmt.executeUpdate();
				deleteQueueStmt.close();
			}

			//不能删除BASE的队列信息
			ownSignlist.add(TBScheduleManager.OWN_SIGN_BASE);
			String deleteQueueSql = "DELETE from "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ " WHERE BASE_TASK_TYPE = ?  AND OWN_SIGN NOT IN (";
			for (int i = 0; i < ownSignlist.size(); i++) {
				if (i > 0) {
					deleteQueueSql = deleteQueueSql + ",";
				}
				deleteQueueSql = deleteQueueSql + "?";
			}
			deleteQueueSql = deleteQueueSql + ")";

			PreparedStatement deleteQueueStmt = conn
					.prepareStatement(deleteQueueSql);
			deleteQueueStmt.setString(1, baseTaskType);
			for (int i = 0; i < ownSignlist.size(); i++) {
				deleteQueueStmt.setString(i + 2, ownSignlist.get(i));
			}
			deleteQueueStmt.executeUpdate();
			deleteQueueStmt.close();
	}

	/**
	 * 清除已经过期的调度服务器信息
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	public int clearExpireScheduleServer(Connection conn,ScheduleTaskType taskInfo,
			ScheduleTaskTypeRunningInfo runningInfo) throws Exception {
		    int result = 0;
			double deadInterval = 0;
			String dataType = this.getDataBaseType(conn);
			String backSql;
			String deleteSql;
			String selectSql;
			if ("oracle".equalsIgnoreCase(dataType)) {
				selectSql ="select UUID from " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
						+ "  WHERE TASK_TYPE = ? "
						+ "  AND (sysdate - HEARTBEAT_TIME) > ? ";

				backSql = this.getBackServerSql(conn)
						+ "  WHERE  UUID = ? ";

				deleteSql = " DELETE FROM "
						+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
						+ "  WHERE  UUID = ? ";
				deadInterval = taskInfo.getJudgeDeadInterval() * 1.0
						/ (24 * 3600 * 1000);
			} else if ("mysql".equalsIgnoreCase(dataType)) {
				selectSql ="select UUID from " + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
						+ "  WHERE TASK_TYPE = ?  "
						+ "  AND (UNIX_TIMESTAMP(now()) -UNIX_TIMESTAMP( HEARTBEAT_TIME)) > ? ";
				backSql = this.getBackServerSql(conn)
						+ "  WHERE UUID = ? ";
				deleteSql = "  DELETE FROM "
						+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
						+ "  WHERE UUID = ? ";
				deadInterval = taskInfo.getJudgeDeadInterval() / 1000.0;
			} else {
				throw new Exception("不支持的数据库类型：" + dataType);
			}

			PreparedStatement selectStatement = conn.prepareStatement(selectSql);
			selectStatement.setString(1, runningInfo.getTaskType());
			selectStatement.setDouble(2, deadInterval);
			ResultSet set = selectStatement.executeQuery();
			List<String> servers = new ArrayList<String>();
			while(set.next()){
				servers.add(set.getString("UUID"));
			}
			set.close();
			selectStatement.close();

			for(String uuid:servers){
				PreparedStatement backStatement = conn.prepareStatement(backSql);
				backStatement.setString(1,uuid);
				backStatement.executeUpdate();
				backStatement.close();

				PreparedStatement deleteStatement = conn.prepareStatement(deleteSql);
				deleteStatement.setString(1, uuid);
				result = deleteStatement.executeUpdate();
				deleteStatement.close();
				conn.commit();
			}
		return result;
	}

	/**
	 * 清除PAMIRS_SCHEDULE_QUEUE中的信息，服务器已经不存在的时候
	 *
	 * @param taskInfo
	 * @throws Exception
	 */
	public int clearTaskQueueInfo(Connection conn, String taskType,
			List<ScheduleServer> serverList) throws Exception {
		int result = 0;
		// 清除调度队列信息
		String sql = " UPDATE "
				+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
				+ " SET CUR_SERVER = null,REQ_SERVER = NULL,"
				+ " GMT_MODIFIED = " + getDataBaseSysdateString(conn)
				+ " WHERE TASK_TYPE = ? ";
		if (serverList.size() > 0) {
			sql = sql + " AND CUR_SERVER not in (";
			for (int i = 0; i < serverList.size(); i++) {
				if (i > 0) {
					sql = sql + ",";
				}
				sql = sql + "?";
			}
			sql = sql + ")";
		}
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, taskType);
		for (int i = 0; i < serverList.size(); i++) {
			stmt.setString(i + 2, serverList.get(i).getUuid());
		}
		result = stmt.executeUpdate();
		stmt.close();
		return result;
	}

	/**
	 * 获取所有的有效服务器信息
	 *
	 * @param taskInfo
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectAllValidScheduleServer(Connection conn,String taskType)
			throws Exception {
		List<ScheduleServer> result = new ArrayList<ScheduleServer>();
			String sql = " SELECT UUID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,IP,HOST_NAME,MANAGER_PORT," +
					"THREAD_NUM,REGISTER_TIME,HEARTBEAT_TIME,NEXT_RUN_START_TIME,NEXT_RUN_END_TIME,VERSION,DEALINFO_DESC,JMX_URL, "
					+ this.getDataBaseSysdateString(conn)
					+ " as CenterServerTime   FROM  "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
					+ " WHERE TASK_TYPE = ?  ORDER BY REGISTER_TIME";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, taskType);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				ScheduleServer server = new ScheduleServer();
				server.setUuid(resultSet.getString("UUID"));
				server.setTaskType(resultSet.getString("TASK_TYPE"));
				server.setBaseTaskType(resultSet.getString("BASE_TASK_TYPE"));
				server.setOwnSign(resultSet.getString("OWN_SIGN"));
				server.setIp(resultSet.getString("IP"));
				server.setHostName(resultSet.getString("HOST_NAME"));
				server.setManagerPort(resultSet.getInt("MANAGER_PORT"));
				server.setThreadNum(resultSet.getInt("THREAD_NUM"));
				server.setRegisterTime(resultSet.getTimestamp("REGISTER_TIME"));
				server.setHeartBeatTime(resultSet
						.getTimestamp("HEARTBEAT_TIME"));

				server.setNextRunStartTime(resultSet.getString("NEXT_RUN_START_TIME"));
				server.setNextRunEndTime(resultSet.getString("NEXT_RUN_END_TIME"));

				server.setVersion(resultSet.getInt("VERSION"));
				server.setCenterServerTime(resultSet
						.getTimestamp("CenterServerTime"));
				server.setDealInfoDesc(resultSet.getString("DEALINFO_DESC"));
				server.setJmxUrl(resultSet.getString("JMX_URL"));
				result.add(server);
			}
			resultSet.close();
			stmt.close();
		return result;
	}

	/**
	 * 重新分配任务处理队列
	 *
	 * @param taskType
	 * @param serverList
	 * @throws Exception
	 */
	public void assignQueue(Connection conn,String taskType, String currentUuid,
			List<ScheduleServer> serverList) throws Exception {
		this.lockTaskTypeRunningInfo(conn,taskType, currentUuid);
			String sqlQueue = " SELECT TASK_TYPE,QUEUE_ID,CUR_SERVER,REQ_SERVER FROM "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
					+ " WHERE TASK_TYPE = ? ORDER BY QUEUE_ID";
			PreparedStatement stmtQueue = conn.prepareStatement(sqlQueue);
			stmtQueue.setString(1, taskType);
			ResultSet setQueue = stmtQueue.executeQuery();
			int point = 0;
			int taskCount = 0;
			while (setQueue.next()) {
				PreparedStatement stmtUpdateQueue = null;
				String sqlModifyQueue = "";
				if (setQueue.getString("CUR_SERVER") == null) {
					sqlModifyQueue = " UPDATE "
							+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
							+ " SET CUR_SERVER = ?,REQ_SERVER = null,GMT_MODIFIED = "
							+ getDataBaseSysdateString(conn)
							+ " WHERE TASK_TYPE = ? and QUEUE_ID = ? ";
					stmtUpdateQueue = conn.prepareStatement(sqlModifyQueue);
					stmtUpdateQueue.setString(1, serverList.get(point)
							.getUuid());
					stmtUpdateQueue.setString(2, taskType);
					stmtUpdateQueue
							.setString(3, setQueue.getString("QUEUE_ID"));
					stmtUpdateQueue.executeUpdate();
					stmtUpdateQueue.close();
				} else if (!(serverList.get(point).getUuid().equalsIgnoreCase(
						setQueue.getString("CUR_SERVER")) == true && setQueue
						.getString("REQ_SERVER") == null)) {
					sqlModifyQueue = " UPDATE "
							+ transferTableName(conn, "PAMIRS_SCHEDULE_QUEUE")
							+ " SET REQ_SERVER = ? ,GMT_MODIFIED = "
							+ getDataBaseSysdateString(conn)
							+ " WHERE TASK_TYPE = ? and QUEUE_ID = ? ";
					stmtUpdateQueue = conn.prepareStatement(sqlModifyQueue);
					stmtUpdateQueue.setString(1, serverList.get(point)
							.getUuid());
					stmtUpdateQueue.setString(2, taskType);
					stmtUpdateQueue
							.setString(3, setQueue.getString("QUEUE_ID"));
					stmtUpdateQueue.executeUpdate();
					stmtUpdateQueue.close();
				} else {
					// 不需要修改当前记录的信息
				}
				taskCount = taskCount + 1;
				if (point >= serverList.size() - 1) {
					point = 0;
				} else {
					point = point + 1;
				}
			}
			setQueue.close();
			stmtQueue.close();
			if (taskCount == 0) {
				throw new Exception("没有对任务类型配置数据处理队列，TASK_TYPE = " + taskType);
			}
	}

	/**
	 * 发送心跳信息
	 *
	 * @param server
	 * @throws Exception
	 */
	public void refreshScheduleServer(Connection conn,ScheduleServer server) throws Exception {
			String REFRESH_SQL_UPDATE = " UPDATE "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
					+ " SET HEARTBEAT_TIME = "
					+ getDataBaseSysdateString(conn)
					+ ",VERSION = VERSION + 1,DEALINFO_DESC = ?,"
					+ " NEXT_RUN_START_TIME =?,NEXT_RUN_END_TIME =?,GMT_MODIFIED = "
					+ getDataBaseSysdateString(conn) + " WHERE UUID = ? ";

			PreparedStatement statement = conn
					.prepareStatement(REFRESH_SQL_UPDATE);
			Timestamp heartBeatTime = new Timestamp(ScheduleUtil
					.getCurrentTimeMillis());
			statement.setString(1, server.getDealInfoDesc());
			statement.setString(2, server.getNextRunStartTime());
			statement.setString(3, server.getNextRunEndTime());
			statement.setString(4, server.getUuid());
			int count = statement.executeUpdate();
			statement.close();
			if (count == 0) {// 数据可能被清除，重新注册数据
				this.registerScheduleServer(conn,server);
			} else {
				server.setHeartBeatTime(heartBeatTime);
				server.setVersion(server.getVersion() + 1);
			}
	}

	/**
	 * 注册服务器
	 * 
	 * @param server
	 * @throws Exception
	 */
	public void registerScheduleServer(Connection conn,ScheduleServer server) throws Exception {
		
			// UUID VARCHAR2(100) 调度服务器唯一编号
			// TASK_TYPE VARCHAR2(50) 任务类型
			// IP VARCHAR2(50) IP地址
			// HOST_NAME VARCHAR2(50) 主机名称
			// MANAGER_PORT NUMBER(10) 远程管理端口
			// THREAD_NUM NUMBER(5) 线程数量
			// REGISTER_TIME DATE 注册时间
			// HEARTBEAT_TIME DATE 最后一次心跳时间
			// VERSION NUMBER(12) 版本号
			// DEALINFO_DESC VARCHAR2(1000) Y 调度处理描述
			String REGISTER_SQL_INSERT = "insert into "
					+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
					+ "(ID,UUID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,IP,HOST_NAME,MANAGER_PORT,THREAD_NUM,REGISTER_TIME,HEARTBEAT_TIME,VERSION,DEALINFO_DESC,JMX_URL,NEXT_RUN_START_TIME,NEXT_RUN_END_TIME,GMT_CREATE,GMT_MODIFIED)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,"
					+ getDataBaseSysdateString(conn) + ","
					+ getDataBaseSysdateString(conn) + ",?,?,?,?,?,"
					+ getDataBaseSysdateString(conn) + ","
					+ getDataBaseSysdateString(conn) + ")";
			PreparedStatement statement = conn
					.prepareStatement(REGISTER_SQL_INSERT);
			Timestamp heartBeatTime = new Timestamp(ScheduleUtil
					.getCurrentTimeMillis());
			long newVersion = server.getVersion() + 1;
			statement.setLong(1,server.getId());
			statement.setString(2, server.getUuid());
			statement.setString(3, server.getTaskType());
			statement.setString(4, server.getBaseTaskType());
			statement.setString(5, server.getOwnSign());
			statement.setString(6, server.getIp());
			statement.setString(7, server.getHostName());
			statement.setInt(8, server.getManagerPort());
			statement.setInt(9, server.getThreadNum());
			statement.setLong(10, newVersion);
			statement.setString(11, server.getDealInfoDesc());
			statement.setString(12, server.getJmxUrl());
			statement.setString(13, server.getNextRunStartTime());
			statement.setString(14, server.getNextRunEndTime());
			statement.execute();
			statement.close();
			server.setHeartBeatTime(heartBeatTime);
			server.setVersion(newVersion);
	}

	public void unRegisterScheduleServer(Connection conn, String serverUUID)
			throws Exception {
		// 先备份服务器数据到历史表
		String[] sqls = new String[2];
		sqls[0] = this.getBackServerSql(conn) + " where UUID = ? ";
		sqls[1] = "delete from "
				+ transferTableName(conn, "PAMIRS_SCHEDULE_SERVER")
				+ " where UUID = ? ";
		for (String sql : sqls) {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, serverUUID);
			statement.executeUpdate();
			statement.close();
		}

	}

	/**
	 *  查询调度服务器的历史信息
	 * @param conn
	 * @param baseTaskType
	 * @param ownSign
	 * @param ip
	 * @param orderStr
	 * @return
	 * @throws Exception
	 */
	public List<ScheduleServer> selectScheduleServer(Connection conn,boolean isHistoryData,
			String baseTaskType, String ownSign, String ip, String orderStr)
			throws Exception {
		List<ScheduleServer> result = new ArrayList<ScheduleServer>();
		String sql = " SELECT UUID,TASK_TYPE,BASE_TASK_TYPE,OWN_SIGN,IP,HOST_NAME,MANAGER_PORT,THREAD_NUM,REGISTER_TIME,HEARTBEAT_TIME,NEXT_RUN_START_TIME,NEXT_RUN_END_TIME,VERSION,DEALINFO_DESC,JMX_URL, "
				+ this.getDataBaseSysdateString(conn)
				+ " as CenterServerTime   FROM  ";
		if(isHistoryData == false){
			sql = sql + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER");
		}else{
			sql = sql + transferTableName(conn, "PAMIRS_SCHEDULE_SERVER_HIS");
		}
		String whereSql = "";
		if (baseTaskType != null) {
			whereSql = whereSql + " BASE_TASK_TYPE = ? ";
		}
		if (ownSign != null) {
			if(whereSql.length() > 0) {
				whereSql = whereSql + " AND ";
			}
			whereSql = whereSql + " OWN_SIGN = ? ";
		}
		if (ip != null) {
			if(whereSql.length() > 0) {
				whereSql = whereSql + " AND ";
			}
			whereSql = whereSql + " IP = ? ";
		}

		if (whereSql.length() > 0) {
			sql = sql + " WHERE " + whereSql;
		}
		if(orderStr == null){
			orderStr = "TASK_TYPE,OWN_SIGN,REGISTER_TIME DESC,HEARTBEAT_TIME DESC,IP";
		}
		sql = sql + " ORDER BY " + orderStr;

		PreparedStatement stmt = conn.prepareStatement(sql);

		int index = 1;
		if (baseTaskType != null) {
			stmt.setString(index, baseTaskType);
			index = index + 1;
		}
		if (ownSign != null) {
			stmt.setString(index, ownSign);
			index = index + 1;
		}
		if (ip != null) {
			stmt.setString(index, ip);
		}

		ResultSet resultSet = stmt.executeQuery();
		while (resultSet.next()) {
			ScheduleServer server = new ScheduleServer();
			server.setUuid(resultSet.getString("UUID"));
			server.setTaskType(resultSet.getString("TASK_TYPE"));
			server.setBaseTaskType(resultSet.getString("BASE_TASK_TYPE"));
			server.setOwnSign(resultSet.getString("OWN_SIGN"));
			server.setIp(resultSet.getString("IP"));
			server.setHostName(resultSet.getString("HOST_NAME"));
			server.setManagerPort(resultSet.getInt("MANAGER_PORT"));
			server.setThreadNum(resultSet.getInt("THREAD_NUM"));
			server.setRegisterTime(resultSet.getTimestamp("REGISTER_TIME"));
			server.setHeartBeatTime(resultSet.getTimestamp("HEARTBEAT_TIME"));
			server.setNextRunStartTime(resultSet.getString("NEXT_RUN_START_TIME"));
			server.setNextRunEndTime(resultSet.getString("NEXT_RUN_END_TIME"));			
			server.setVersion(resultSet.getInt("VERSION"));
			server.setCenterServerTime(resultSet
					.getTimestamp("CenterServerTime"));
			server.setDealInfoDesc(resultSet.getString("DEALINFO_DESC"));
			server.setJmxUrl(resultSet.getString("JMX_URL"));
			result.add(server);
		}
		resultSet.close();
		stmt.close();
		return result;
	}
		
	public String getDataBaseType(Connection conn) throws SQLException {
		if (dataBaseType == null) {
			dataBaseType = conn.getMetaData().getDatabaseProductName();
			if ("oracle".equalsIgnoreCase(dataBaseType) == false
					&& "mysql".equalsIgnoreCase(dataBaseType) == false) {
				throw new SQLException("不支持的数据库类型：" + dataBaseType+",请设置数据库属性dataBaseType：oracle or mysql");
			}
		}
		return dataBaseType;
	}

	/**
	 * 获取不同数据库获取系统时间的方法字符串
	 * 
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public String getDataBaseSysdateString(Connection conn) throws Exception {
		String type = this.getDataBaseType(conn);
		if ("oracle".equalsIgnoreCase(type)) {
			return "sysdate";
		} else if ("mysql".equalsIgnoreCase(type)) {
			return "now()";
		} else {
			throw new Exception("不支持的数据库类型：" + type);
		}
	}

	/**
	 * 获取配置中心的当前时间
	 * 
	 * @return
	 * @throws Exception
	 */
	public Timestamp getConfigCenterCurrentTime(Connection conn) throws Exception {
			String type = this.getDataBaseType(conn);
			String sql = "";
			if ("oracle".equalsIgnoreCase(type)) {
				sql = "select sysdate from dual";
			} else if ("mysql".equalsIgnoreCase(type)) {
				sql = "select now()";
			} else {
				throw new Exception("不支持的数据库类型：" + type);
			}
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet set = statement.executeQuery();
			Timestamp result = null;
			if (set.next()) {
				result = set.getTimestamp(1);
			} else {
				throw new Exception("取系统时间错误");
			}
			set.close();
			statement.close();
			return result;
	}
	public String transferTableName(Connection conn, String name) {
		if (this.tableMap == null) {
			return name.toLowerCase();
		} else if (this.tableMap.containsKey(name)) {
			return tableMap.get(name).toLowerCase();
		} else {
			return name.toLowerCase();
		}
	}

}
