package com.taobao.pamirs.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import com.taobao.pamirs.schedule.CronExpression;
import com.taobao.pamirs.schedule.IScheduleAlert;
import com.taobao.pamirs.schedule.IScheduleConfigCenterClient;
import com.taobao.pamirs.schedule.IScheduleProcessor;
import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.ScheduleServer;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleTaskTypeRunningInfo;
import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.TBScheduleProcessorNotSleep;
import com.taobao.pamirs.schedule.TBScheduleProcessorSleep;
import com.taobao.pamirs.schedule.TaskQueueInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 1、任务调度分配器的目标：	让所有的任务不重复，不遗漏的被快速处理。
 * 2、一个Manager只管理一种任务类型的一组工作线程。
 * 3、在一个JVM里面可能存在多个处理相同任务类型的Manager，也可能存在处理不同任务类型的Manager。
 * 4、在不同的JVM里面可以存在处理相同任务的Manager 
 * 5、调度的Manager可以动态的随意增加和停止
 * 
 * 主要的职责：
 * 1、定时向集中的数据配置中心更新当前调度服务器的心跳状态
 * 2、向数据配置中心获取所有服务器的状态来重新计算任务的分配。这么做的目标是避免集中任务调度中心的单点问题。
 * 3、在每个批次数据处理完毕后，检查是否有其它处理服务器申请自己把持的任务队列，如果有，则释放给相关处理服务器。
 *  
 * 其它：
 * 	 如果当前服务器在处理当前任务的时候超时，需要清除当前队列，并释放已经把持的任务。并向控制主动中心报警。
 * 
 * @author xuannan
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TBScheduleManager {
	private static transient Log log = LogFactory.getLog(com.taobao.pamirs.schedule.TBScheduleManager.class);
	public static String OWN_SIGN_BASE ="BASE";
	/**
	 * 用户标识不同线程的序号
	 */
	private static int nextSerialNumber = 0;
 
	private int currentSerialNumber=0;
	/**
	 * 调度任务类型信息
	 */
	private ScheduleTaskType taskTypeInfo;

	/**
	 * 运行器信息,通过own_sign区分不同环境
	 */
	private ScheduleTaskTypeRunningInfo taskTypeRunningInfo;

	/**
	 * 当前调度服务的信息
	 */
	private ScheduleServer currenScheduleServer;

	/**
	 * 队列处理器
	 */
	IScheduleTaskDeal queueDealTask;

    /**
     * 多线程任务处理器
     */
	IScheduleProcessor processor;
    com.taobao.pamirs.schedule.StatisticsInfo statisticsInfo = new com.taobao.pamirs.schedule.StatisticsInfo();

    boolean isPauseSchedule = true;
    String pauseMessage="";
    boolean isNeedReloadQueue = true;
    /**
     * 告警接口
     */
    IScheduleAlert scheduleAlert;
    /**
     *  当前处理任务队列清单
     */
    private List<String> currentTaskQueue = new ArrayList<String>();
    /**
     * 最近一起重新装载调度任务的时间。
     * 当前实际  - 上此装载时间  > intervalReloadTaskQueue，则向配置中心请求最新的任务分配情况
     */
    private long lastReloadTaskQueueTime=0;

    private String mBeanName;
    /**
     * 向配置中心更新信息的定时器
     */
    private Timer heartBeatTimer;

    private IScheduleConfigCenterClient scheduleCenter;

    private String startErrorInfo = null;

	TBScheduleManager(String baseTaskType,String ownSign,int managerPort,String jxmUrl,IScheduleConfigCenterClient aScheduleCenter,IScheduleTaskDeal aQueueDealTask,IScheduleAlert aScheduleAlert) throws Exception{
		if(aQueueDealTask == null){
			throw new Exception("没有为任务：" + baseTaskType + "指定处理器 queueDealTask == null " );
		}
		this.currentSerialNumber = serialNumber();
		this.scheduleCenter = aScheduleCenter;
		this.scheduleAlert = aScheduleAlert;
    	this.taskTypeInfo = this.scheduleCenter.loadTaskTypeBaseInfo(baseTaskType);

    	if(this.taskTypeInfo.getJudgeDeadInterval() < this.taskTypeInfo.getHeartBeatRate() * 5){
    		throw new Exception("数据配置存在问题，死亡的时间间隔，至少要大于心跳线程的5倍。当前配置数据：JudgeDeadInterval = "
    				+ this.taskTypeInfo.getJudgeDeadInterval()
    				+ ",HeartBeatRate = " + this.taskTypeInfo.getHeartBeatRate());
    	}
    	this.currenScheduleServer = ScheduleServer.createScheduleServer(baseTaskType,ownSign,this.taskTypeInfo.getThreadNumber(),managerPort,jxmUrl);
    	this.taskTypeRunningInfo = this.scheduleCenter.loadTaskTypeRunningInfo(baseTaskType, ownSign, this.currenScheduleServer.getUuid());
    	this.queueDealTask = aQueueDealTask;

    	mBeanName = "pamirs:name=" + "schedule.ServerMananger." +this.currenScheduleServer.getUuid();

    	this.taskTypeInfo.setTaskQueueNumber(scheduleCenter.queryQueueCount(this.getTaskTypeRunningInfo().getTaskType()));
    	//当服务器启动的时候，向数据配置中心注册，用于调度队列的分配.
    	//发送到ConfigServer、Tair或者中心数据库
    	scheduleCenter.registerScheduleServer(this.currenScheduleServer);
    	heartBeatTimer = new Timer(this.getTaskTypeRunningInfo().getTaskType() +"-" + this.currentSerialNumber +"-HeartBeat");
    	heartBeatTimer.schedule(new com.taobao.pamirs.schedule.HeartBeatTimerTask(this),
                new java.sql.Date(ScheduleUtil.getCurrentTimeMillis() + 500),
                this.taskTypeInfo.getHeartBeatRate());

    	//只有在已经获取到任务处理队列后才开始启动任务处理器
    	new Thread(this.getTaskTypeRunningInfo().getTaskType()  +"-" + this.currentSerialNumber +"-StartProcess"){
    		@SuppressWarnings("static-access")
			public void run(){
    			try{
    			   log.info("开始获取调度任务队列...... of " + currenScheduleServer.getUuid());
    			   int count =0;
    			   lastReloadTaskQueueTime = ScheduleUtil.getCurrentTimeMillis();
    			   while(getCurrentScheduleQueueNow().size() <= 0){
    				      log.debug("尝试获取调度队列，第" + count + "次");
        			      Thread.currentThread().sleep(1000);
        			      count = count + 1;
    			   }
    			   String tmpStr ="";
    			   for(int i=0;i< currentTaskQueue.size();i++){
    				   if(i>0){
    					   tmpStr = tmpStr +",";
    				   }
    				   tmpStr = tmpStr + currentTaskQueue.get(i);
    			   }
    			   log.info("获取到任务处理队列，开始调度：" + tmpStr +"  of  "+ currenScheduleServer.getUuid());
    			   computerStart();
    			}catch(Exception e){
    				log.error(e.getMessage(),e);
    				String str = e.getMessage();
    				if(str.length() > 300){
    					str = str.substring(0,300);
    				}
    				startErrorInfo = "启动处理异常：" + str;
    			}
    		}
    	}.start();
    }
	private static synchronized int serialNumber() {
	        return nextSerialNumber++;
	}
	public static String getTaskTypeByBaseAndOwnSign(String baseType,String ownSign){
		if(ownSign.equals(OWN_SIGN_BASE) == true){
			return baseType;
		}
		return baseType+"$" + ownSign;
	}
	public int getCurrentSerialNumber(){
		return this.currentSerialNumber;
	}


	/**
	 * 定时向数据配置中心更新当前服务器的心跳信息。
	 * 如果发现本次更新的时间如果已经超过了，服务器死亡的心跳周期，则不能在向服务器更新信息。
	 * 而应该当作新的服务器，进行重新注册。
	 * @throws Exception
	 */
	public void refreshScheduleServerInfo() throws Exception {
		try{
		if(log.isDebugEnabled()){
			//log.debug("向服务器发送心跳信息：" + this.getScheduleServer().getUuid());
		}

		rewriteScheduleInfo();
        //重新分配任务
        this.assignScheduleTask();

        //判断是否需要重新加载任务队列，避免任务处理进程不必要的检查和等待
        boolean tmpBoolean = this.isNeedReLoadTaskQueue(this.taskTypeRunningInfo.getOwnSign());
        if(tmpBoolean != this.isNeedReloadQueue){
        	this.isNeedReloadQueue = tmpBoolean;
        	rewriteScheduleInfo();
        }

        if(this.isPauseSchedule  == true || processor.isSleeping() == true){
            //如果服务已经暂停了，则需要重新定时更新 cur_server 和 req_server
            //如果服务没有暂停，一定不能调用的
               this.getCurrentScheduleQueueNow();
          }


        //如果服务器超过10个心跳周期，还没有进行相关操作，则报警
        if(this.isPauseSchedule ==false && ScheduleUtil.getCurrentTimeMillis() - this.lastReloadTaskQueueTime >
             this.taskTypeInfo.getHeartBeatRate() * 10){
        	String message ="调度服务器" + this.currenScheduleServer.getUuid() +"[TASK_TYPE=" +this.getTaskTypeRunningInfo().getTaskType() + "]已经超过10个心跳周期，还 没有重新装载调度队列,请检查是否存在数据阻塞情况";
			log.warn(message);
			if(this.scheduleAlert != null){
				this.scheduleAlert.noReloadTaskQueue(this.getTaskTypeRunningInfo().getTaskType(), this.currenScheduleServer.getUuid(),message);
			}
        }
		}catch(Throwable e){
			//清除内存中所有的已经取得的数据和任务队列,避免心跳线程失败时候导致的数据重复
			this.currentTaskQueue.clear();
			if(this.processor != null){
			   this.processor.clearAllHasFetchData();
			}
			if(e instanceof Exception){
				throw (Exception)e;
			}else{
			   throw new Exception(e.getMessage(),e);
			}
		}
	}

	public boolean isNeedReLoadTaskQueue(String ownSign) throws Exception{
		List<TaskQueueInfo> queues =  this.scheduleCenter.loadAllQueue(this.taskTypeRunningInfo.getTaskType());
		List<String> tmpOldQueues = new ArrayList<String>();
		tmpOldQueues.addAll(this.currentTaskQueue);
		String tmpUUID = this.currenScheduleServer.getUuid() ;
		for(TaskQueueInfo item : queues){
			if(tmpUUID.equals(item.getCurrentScheduleServer())== true){
				if(item.getRequestScheduleServer() != null){
					return true;
				}else if(tmpOldQueues.contains(item.getTaskQueueId()) == false){
					return true;
				}else{
					tmpOldQueues.remove(item.getTaskQueueId());
				}
			}else if(tmpUUID.equals(item.getRequestScheduleServer()) == true) {
				return true;
			}
		}
		if(tmpOldQueues.size() >0){//这个判断是冗余的
		  return true;
		}
		return false;
	}

	public void rewriteScheduleInfo() throws Exception{
		//先发送心跳信息
		if(startErrorInfo == null){
			String tmpStr = this.taskTypeInfo.getProcessorType();
			tmpStr = tmpStr + ":" +	(this.isNeedReloadQueue==true?"需要重新装载调度任务":"不需要重新装载调度任务");
			this.currenScheduleServer.setDealInfoDesc(tmpStr +"::" + this.pauseMessage + ":" + this.statisticsInfo.getDealDescription());
		}else{
		    this.currenScheduleServer.setDealInfoDesc(startErrorInfo);
		}
		this.scheduleCenter.refreshScheduleServer(this.currenScheduleServer);
	}
	/**
	 * 根据当前调度服务器的信息，重新计算分配所有的调度任务
	 * 任务的分配是需要加锁，避免数据分配错误。为了避免数据锁带来的负面作用，通过版本号来达到锁的目的
	 *
	 * 1、获取任务状态的版本号
	 * 2、获取所有的服务器注册信息和任务队列信息
	 * 3、清除已经超过心跳周期的服务器注册信息
	 * 3、重新计算任务分配
	 * 4、更新任务状态的版本号【乐观锁】
	 * 5、根系任务队列的分配信息
	 * @throws Exception
	 */
	public void assignScheduleTask() throws Exception {
		int clearServerCount = scheduleCenter
				.clearExpireScheduleServer(this.taskTypeInfo,this.taskTypeRunningInfo);
		List<ScheduleServer> serverList = scheduleCenter
				.selectAllValidScheduleServer(this.getTaskTypeRunningInfo().getTaskType());
		int clearTaskQueueInfoCount = scheduleCenter.clearTaskQueueInfo(
				this.getTaskTypeRunningInfo().getTaskType(), serverList);

		boolean isNeedReAssign = false;
		if (clearServerCount > 0 || clearTaskQueueInfoCount > 0) {
			isNeedReAssign = true;
		} else  {
			for (ScheduleServer item : serverList) {
				//注意，比较时间一定要用数据库时间
				if (item.getCenterServerTime().getTime() - item.getRegisterTime().getTime()
						< taskTypeInfo.getJudgeDeadInterval() * 3 ) {
					isNeedReAssign = true;
					break;
				}
			}
		}
		if (isNeedReAssign == true) {
			scheduleCenter.assignQueue(this.getTaskTypeRunningInfo().getTaskType(),
					this.currenScheduleServer.getUuid(), serverList);
		}
		if (log.isDebugEnabled()) {
			//log.debug(message);
		}
	}

	/**
	 * 重新加载当前服务器的任务队列
	 * 1、释放当前服务器持有，但有其它服务器进行申请的任务队列
	 * 2、重新获取当前服务器的处理队列
	 *
	 * 为了避免此操作的过度，阻塞真正的数据处理能力。系统设置一个重新装载的频率。例如1分钟
	 *
	 * 特别注意：
	 *   此方法的调用必须是在当前所有任务都处理完毕后才能调用，否则是否任务队列后可能数据被重复处理
	 */
	@SuppressWarnings("static-access")
	public List<String> getCurrentScheduleQueue() {
		try{
		if (this.isNeedReloadQueue == true) {
			//特别注意：需要判断数据队列是否已经空了，否则可能在队列切换的时候导致数据重复处理
			//主要是在线程不休眠就加载数据的时候一定需要这个判断
			if (this.processor != null) {
					while (this.processor.isDealFinishAllData() == false) {
						Thread.currentThread().sleep(50);
					}
			}
			//真正开始处理数据
			this.getCurrentScheduleQueueNow();
		}
		this.lastReloadTaskQueueTime = ScheduleUtil.getCurrentTimeMillis();
		return this.currentTaskQueue;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private List<String> getCurrentScheduleQueueNow() throws Exception {
		//是否被人申请的队列
		this.scheduleCenter.releaseDealQueue(this.getTaskTypeRunningInfo().getTaskType(), this.currenScheduleServer.getUuid());
		//重新查询当前服务器能够处理的队列
		this.currentTaskQueue = this.scheduleCenter.reloadDealQueue(
				this.getTaskTypeRunningInfo().getTaskType(), this.currenScheduleServer.getUuid());

		//如果超过10个心跳周期还没有获取到调度队列，则报警
		if(this.currentTaskQueue.size() ==0 &&
				ScheduleUtil.getCurrentTimeMillis() - this.lastReloadTaskQueueTime
				> this.taskTypeInfo.getHeartBeatRate() * 10){
			String message ="调度服务器" + this.currenScheduleServer.getUuid() +"[TASK_TYPE=" + this.getTaskTypeRunningInfo().getTaskType() + "]自启动以来，超过10个心跳周期，还 没有获取到分配的任务队列";
			log.warn(message);
			if(this.scheduleAlert != null){
				this.scheduleAlert.noTaskQueue(this.getTaskTypeRunningInfo().getTaskType(), this.currenScheduleServer.getUuid(),message);
			}
		}

		if(this.currentTaskQueue.size() >0){
			 //更新时间戳
			 this.lastReloadTaskQueueTime = ScheduleUtil.getCurrentTimeMillis();
		}

		return this.currentTaskQueue;
	}


	/**
	 * 获取当前类型任务的任务队列的数量，用于数据的分片获取
	 * @return
	 */
	public int getTaskQueueCount(){
		return this.taskTypeInfo.getTaskQueueNumber();
	}

	/**
	 * 开始的时候，计算第一次执行时间
	 * @throws Exception
	 */
    public void computerStart() throws Exception{
    	//只有当存在可执行队列后再开始启动队列

    	boolean isRunNow = false;
    	if(this.taskTypeInfo.getPermitRunStartTime() == null){
    		isRunNow = true;
    	}else{
    		String tmpStr = this.taskTypeInfo.getPermitRunStartTime();
			if(tmpStr.toLowerCase().startsWith("startrun:")){
				isRunNow = true;
				tmpStr = tmpStr.substring("startrun:".length());
	    	}
			CronExpression cexpStart = new CronExpression(tmpStr);
    		Date current = new Date( ScheduleUtil.getCurrentTimeMillis());
    		Date firstStartTime = cexpStart.getNextValidTimeAfter(current);
    		this.heartBeatTimer.schedule(
    				new com.taobao.pamirs.schedule.PauseOrResumeScheduleTask(this,this.heartBeatTimer,
    						com.taobao.pamirs.schedule.PauseOrResumeScheduleTask.TYPE_RESUME,tmpStr),
    						firstStartTime);
			this.currenScheduleServer.setNextRunStartTime(ScheduleUtil.transferDataToString(firstStartTime));
			if( this.taskTypeInfo.getPermitRunEndTime() == null
    		   || this.taskTypeInfo.getPermitRunEndTime().equals("-1")){
				this.currenScheduleServer.setNextRunEndTime("当不能获取到数据的时候pause");
			}else{
				String tmpEndStr = this.taskTypeInfo.getPermitRunEndTime();
				CronExpression cexpEnd = new CronExpression(tmpEndStr);
				Date firstEndTime = cexpEnd.getNextValidTimeAfter(firstStartTime);
				Date nowEndTime = cexpEnd.getNextValidTimeAfter(current);
				if(!nowEndTime.equals(firstEndTime) && current.before(nowEndTime)){
					isRunNow = true;
					firstEndTime = nowEndTime;
				}
				this.heartBeatTimer.schedule(
	    				new com.taobao.pamirs.schedule.PauseOrResumeScheduleTask(this,this.heartBeatTimer,
	    						com.taobao.pamirs.schedule.PauseOrResumeScheduleTask.TYPE_PAUSE,tmpEndStr),
	    						firstEndTime);
				this.currenScheduleServer.setNextRunEndTime(ScheduleUtil.transferDataToString(firstEndTime));
			}
    	}
    	if(isRunNow == true){
    		this.resume("开启服务立即启动");
    	}
    	this.rewriteScheduleInfo();
    	
    }
	/**
	 * 当Process没有获取到数据的时候调用，决定是否暂时停止服务器
	 * @throws Exception
	 */
	public boolean isContinueWhenData() throws Exception{
		if(isPauseWhenNoData() == true){
			this.pause("没有数据,暂停调度");
			return false;
		}else{
			return true;
		}
	}
	public boolean isPauseWhenNoData(){
		//如果还没有分配到任务队列则不能退出
		if(this.currentTaskQueue.size() >0 && this.taskTypeInfo.getPermitRunStartTime() != null){
			if(this.taskTypeInfo.getPermitRunEndTime() == null
		       || this.taskTypeInfo.getPermitRunEndTime().equals("-1")){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}	
	/**
	 * 超过运行的运行时间，暂时停止调度
	 * @throws Exception 
	 */
	public void pause(String message) throws Exception{
		if (this.isPauseSchedule == false) {
			this.isPauseSchedule = true;
			this.pauseMessage = message;
			if (log.isDebugEnabled()) {
				log.debug("暂停调度 ：" + this.currenScheduleServer.getUuid()+":" + this.statisticsInfo.getDealDescription());
			}
			if (this.processor != null) {
				this.processor.stopSchedule();
			}
			rewriteScheduleInfo();
		}
	}
	/**
	 * 处在了可执行的时间区间，恢复运行
	 * @throws Exception 
	 */
	public void resume(String message) throws Exception{
		if (this.isPauseSchedule == true) {
			if(log.isDebugEnabled()){
				log.debug("恢复调度:" + this.currenScheduleServer.getUuid());
			}
			this.isPauseSchedule = false;
			this.pauseMessage = message;
			if (this.queueDealTask != null) {
				if (this.taskTypeInfo.getProcessorType() != null &&
					this.taskTypeInfo.getProcessorType().equalsIgnoreCase("NOTSLEEP")==true){
					this.taskTypeInfo.setProcessorType("NOTSLEEP");
					this.processor = new TBScheduleProcessorNotSleep(this,
							queueDealTask,this.statisticsInfo);
				}else{
					this.processor = new TBScheduleProcessorSleep(this,
							queueDealTask,this.statisticsInfo);
					this.taskTypeInfo.setProcessorType("SLEEP");
				}
			}
			rewriteScheduleInfo();
		}
	}	
	/**
	 * 当服务器停止的时候，调用此方法清除所有未处理任务，清除服务器的注册信息。
	 * 也可能是控制中心发起的终止指令。
	 * 需要注意的是，这个方法必须在当前任务处理完毕后才能执行
	 * @throws Exception 
	 */
	public void stopScheduleServer() throws Exception{
		if(log.isDebugEnabled()){
			log.debug("停止服务器 ：" + this.currenScheduleServer.getUuid());
		}
		this.isPauseSchedule = false;
		if (this.processor != null) {
			this.processor.stopSchedule();
		} else {
			this.unRegisterScheduleServer();
		}
	}

	protected void unRegisterScheduleServer() throws Exception{
		if(this.processor !=null){
			this.processor = null;
		}
		if(this.isPauseSchedule == true){
			//是暂停调度，不注销Manager自己
			return;
		}
		if(log.isDebugEnabled()){
			log.debug("注销服务器 ：" + this.currenScheduleServer.getUuid());
		}
		 //取消心跳TIMER
		 this.heartBeatTimer.cancel();
		 //从配置中心注销自己
		 this.scheduleCenter.unRegisterScheduleServer(this.currenScheduleServer.getUuid());
		 //从BeanManager中注销自己
		 TBScheduleManagerFactory.unregisterMBean(this.mBeanName);
	}
	public ScheduleTaskType getTaskTypeInfo() {
		return taskTypeInfo;
	}	
	
	
	public com.taobao.pamirs.schedule.StatisticsInfo getStatisticsInfo() {
		return statisticsInfo;
	}
	public ScheduleTaskTypeRunningInfo getTaskTypeRunningInfo() {
		return taskTypeRunningInfo;
	}
	/**
	 * 打印给定任务类型的任务分配情况
	 * @param taskType
	 */
	public void printScheduleServerInfo(String taskType){
		
	}
	public ScheduleServer getScheduleServer(){
		return this.currenScheduleServer;
	}
	public String getmBeanName() {
		return mBeanName;
	}
}

class HeartBeatTimerTask extends java.util.TimerTask {
	private static transient Log log = LogFactory
			.getLog(com.taobao.pamirs.schedule.HeartBeatTimerTask.class);
	com.taobao.pamirs.schedule.TBScheduleManager manager;

	public HeartBeatTimerTask(com.taobao.pamirs.schedule.TBScheduleManager aManager) {
		manager = aManager;
	}

	public void run() {
		try {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			manager.refreshScheduleServerInfo();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}

class PauseOrResumeScheduleTask extends java.util.TimerTask {
	private static transient Log log = LogFactory
			.getLog(com.taobao.pamirs.schedule.HeartBeatTimerTask.class);
	public static int TYPE_PAUSE  = 1;
	public static int TYPE_RESUME = 2;	
	com.taobao.pamirs.schedule.TBScheduleManager manager;
	Timer timer;
	int type;
	String cronTabExpress;
	public PauseOrResumeScheduleTask(com.taobao.pamirs.schedule.TBScheduleManager aManager,Timer aTimer,int aType,String aCronTabExpress) {
		this.manager = aManager;
		this.timer = aTimer;
		this.type = aType;
		this.cronTabExpress = aCronTabExpress;
	}
	public void run() {
		try {
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			this.cancel();//取消调度任务
			Date current = new Date( ScheduleUtil.getCurrentTimeMillis());
			CronExpression cexp = new CronExpression(this.cronTabExpress);
			Date nextTime = cexp.getNextValidTimeAfter(current);
			if(this.type == TYPE_PAUSE){
				manager.pause("到达终止时间,pause调度");
				this.manager.getScheduleServer().setNextRunEndTime(ScheduleUtil.transferDataToString(nextTime));
			}else{
				manager.resume("到达开始时间,resume调度");
				this.manager.getScheduleServer().setNextRunStartTime(ScheduleUtil.transferDataToString(nextTime));
			}
			this.timer.schedule(new com.taobao.pamirs.schedule.PauseOrResumeScheduleTask(this.manager,this.timer,this.type,this.cronTabExpress) , nextTime);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}

class StatisticsInfo{
	private AtomicLong fetchDataNum = new AtomicLong(0);//读取次数
	private AtomicLong fetchDataCount = new AtomicLong(0);//读取的数据量
	private AtomicLong dealDataSucess = new AtomicLong(0);//处理成功的数据量
	private AtomicLong dealDataFail = new AtomicLong(0);//处理失败的数据量
	private AtomicLong dealSpendTime = new AtomicLong(0);//处理总耗时,没有做同步，可能存在一定的误差
	private AtomicLong otherCompareCount = new AtomicLong(0);//特殊比较的次数
	
	public void addFetchDataNum(long value){
		this.fetchDataNum.addAndGet(value);
	}
	public void addFetchDataCount(long value){
		this.fetchDataCount.addAndGet(value);
	}
	public void addDealDataSucess(long value){
		this.dealDataSucess.addAndGet(value);
	}
	public void addDealDataFail(long value){
		this.dealDataFail.addAndGet(value);
	}
	public void addDealSpendTime(long value){
		this.dealSpendTime.addAndGet(value);
	}
	public void addOtherCompareCount(long value){
		this.otherCompareCount.addAndGet(value);
	}
    public String getDealDescription(){
    	return "FetchDataCount=" + this.fetchDataCount 
    	  +",FetchDataNum=" + this.fetchDataNum
    	  +",DealDataSucess=" + this.dealDataSucess
    	  +",DealDataFail=" + this.dealDataFail
    	  +",DealSpendTime=" + this.dealSpendTime
    	  +",otherCompareCount=" + this.otherCompareCount;    	  
    }

}