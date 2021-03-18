package com.taobao.pamirs.schedule;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.sql.DataSource;

import com.taobao.pamirs.schedule.IScheduleClient;
import com.taobao.pamirs.schedule.IScheduleConfigCenterClient;
import com.taobao.pamirs.schedule.ScheduleConfigCenterClientInner;
import com.taobao.pamirs.schedule.ScheduleServer;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleTaskTypeRunningInfo;
import com.taobao.pamirs.schedule.TaskQueueInfo;
import org.springframework.beans.factory.InitializingBean;

/**
 * 基于数据库的调度配置中心客户端
 * 
 * @author xuannan@taobao.com
 * 
 */
@WebService
public class ScheduleConfigCenterClientByDatabase implements IScheduleConfigCenterClient,IScheduleClient,InitializingBean {
	Map<String, String> tableMap;
	/**
	 * 配置中心数据库的数据源
	 */
	DataSource dataSource;

	/**
	 * 配置中心的数据可类型
	 */
	String dataBaseType = null;

	ScheduleConfigCenterClientInner clientInner;

	//在Spring对象创建完毕后，创建内部对象
	public void afterPropertiesSet() throws Exception {
		clientInner = new ScheduleConfigCenterClientInner(this.dataBaseType,this.tableMap);
	}

	public void setTableMap(Map<String, String> aTableMap) {
		this.tableMap = new HashMap<String, String>();
		for (Object e : aTableMap.keySet()) {
			String key = ((String) e).toUpperCase();
			if (aTableMap.get(e) != null
					&& aTableMap.get(e).toString().trim().length() > 0) {
				this.tableMap.put(key.trim(), aTableMap.get(e).toString()
						.trim());
			}
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setDataBaseType(String dataBaseType) {
		this.dataBaseType = dataBaseType;
	}
    private Connection getConnection() throws SQLException{
    	Connection result = this.dataSource.getConnection();
    	if(result.getAutoCommit() == true){
    		result.setAutoCommit(false);
    	}
    	return result;
    }
    public List<ScheduleTaskType> getAllTaskTypeBaseInfo()throws Exception {
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<ScheduleTaskType> result =  clientInner.getAllTaskTypeBaseInfo(conn);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
    }
    public List<ScheduleTaskTypeRunningInfo> getAllTaskTypeRunningInfo(String baseTaskType) throws Exception {
		Connection conn = null;
		try {
			conn = this.getConnection();
			List<ScheduleTaskTypeRunningInfo> result = clientInner.getAllTaskTypeRunningInfo(conn, baseTaskType);
			conn.commit();
			return result;
		} catch (Throwable e) {
			if (conn != null) {
				conn.rollback();
			}
			if (e instanceof Exception) {
				throw (Exception) e;
			} else {
				throw new Exception(e);
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
    }

    public void clearTaskType(String baseTaskType) throws Exception{
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.clearTaskType(conn, baseTaskType);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
    }
    public void deleteTaskType(String baseTaskType) throws Exception{
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.deleteTaskType(conn, baseTaskType);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
    }
    public void createBaseTaskType(ScheduleTaskType baseTaskType,String[] queueIds) throws Exception {
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.createBaseTaskType(conn, baseTaskType, queueIds);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
   }
	public List<String> reloadDealQueue(String taskType, String uuid)
			throws Exception {
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<String> result = clientInner.reloadDealQueue(conn,taskType,uuid);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public List<TaskQueueInfo> loadAllQueue(String taskType) throws Exception {
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<TaskQueueInfo> result = clientInner.loadAllQueue(conn,taskType);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public void releaseDealQueue(String taskType, String uuid) throws Exception {
		 Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.releaseDealQueue(conn,taskType,uuid);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public int queryQueueCount(String taskType) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 int result = clientInner.queryQueueCount(conn,taskType);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public ScheduleTaskType loadTaskTypeBaseInfo(String taskType)
			throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 ScheduleTaskType result = clientInner.loadTaskTypeBaseInfo(conn,taskType);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public ScheduleTaskTypeRunningInfo loadTaskTypeRunningInfo(
			String baseTaskType, String ownSign, String serverUUID)
			throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 ScheduleTaskTypeRunningInfo result = clientInner.loadTaskTypeRunningInfo(conn, baseTaskType, ownSign, serverUUID);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public void lockTaskTypeRunningInfo(String taskType, String lockServerUuid)
			throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.lockTaskTypeRunningInfo(conn, taskType, lockServerUuid);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }		
	}

	public int clearExpireScheduleServer(ScheduleTaskType taskInfo,
			ScheduleTaskTypeRunningInfo runningInfo) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 int result = clientInner.clearExpireScheduleServer(conn, taskInfo, runningInfo);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public int clearTaskQueueInfo(String taskType,
			List<ScheduleServer> serverList) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 int result = clientInner.clearTaskQueueInfo(conn, taskType, serverList);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public List<ScheduleServer> selectAllValidScheduleServer(String taskType)
			throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<ScheduleServer> result = clientInner.selectAllValidScheduleServer(conn, taskType);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public void assignQueue(String taskType, String currentUuid,
			List<ScheduleServer> serverList) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.assignQueue(conn, taskType,currentUuid,serverList);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }		
	}

	public void refreshScheduleServer(ScheduleServer server) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.refreshScheduleServer(conn, server);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }
	}

	public void registerScheduleServer(ScheduleServer server) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.registerScheduleServer(conn, server);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }		
	}

	public void unRegisterScheduleServer(String serverUUID) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.unRegisterScheduleServer(conn, serverUUID);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }				
	}

	public void clearExpireTaskTypeRunningInfo(String baseTaskType,
			String serverUUID, double expireDateInternal) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 clientInner.clearExpireTaskTypeRunningInfo(conn, baseTaskType,serverUUID,expireDateInternal);
			 conn.commit();
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }				
	}
	public List<ScheduleServer> selectHistoryScheduleServer(String baseTaskType, String ownSign, String ip, String orderStr)
			throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<ScheduleServer> result = clientInner.selectScheduleServer(conn,true,baseTaskType,ownSign,ip,orderStr);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }		
	}

	@Override
	public List<ScheduleServer> selectScheduleServer(String baseTaskType,
			String ownSign, String ip, String orderStr) throws Exception {
		Connection conn = null;
		 try{
			 conn = this.getConnection();
			 List<ScheduleServer> result = clientInner.selectScheduleServer(conn,false,baseTaskType,ownSign,ip,orderStr);
			 conn.commit();
			 return result;
		 }catch(Throwable e){
			 if(conn != null){
			     conn.rollback();
			 }
			 if(e instanceof Exception){
				 throw (Exception)e;
			 }else{
				 throw new Exception(e);
			 }			 
		 }finally{
			 if(conn!= null){
				 conn.close();
			 }
		 }		
	}
}
