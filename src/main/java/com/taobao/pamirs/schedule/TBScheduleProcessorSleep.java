package com.taobao.pamirs.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.taobao.pamirs.schedule.IScheduleProcessor;
import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.TBScheduleManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 任务调度器，在TBScheduleManager的管理下实现多线程数据处理
 * @author xuannan
 *
 * @param <T>
 */
public class TBScheduleProcessorSleep<T> implements IScheduleProcessor,Runnable {
	
	private static transient Log logger = LogFactory.getLog(com.taobao.pamirs.schedule.TBScheduleProcessorSleep.class);
	final com.taobao.pamirs.schedule.LockObject m_lockObject = new com.taobao.pamirs.schedule.LockObject();
	List<Thread> threadList =  Collections.synchronizedList(new ArrayList<Thread>());
	/**
	 * 任务管理器
	 */
	protected TBScheduleManager scheduleManager;
	/**
	 * 任务类型
	 */
	ScheduleTaskType taskTypeInfo;

	/**
	 * 任务处理的接口类
	 */
	protected IScheduleTaskDeal<T> taskDealProcessor;

	/**
	 * 当前任务队列的版本号
	 */
	protected long taskListVersion = 0;
	final Object lockVersionObject = new Object();
	final Object lockRunningList = new Object();

	protected List<T> taskList = Collections.synchronizedList(new ArrayList<T>());

	/**
	 * 是否可以批处理
	 */
	boolean isMutilTask = false;

	/**
	 * 是否已经获得终止调度信号
	 */
	boolean isStopSchedule = false;// 用户停止队列调度
	boolean isSleeping = false;

	com.taobao.pamirs.schedule.StatisticsInfo statisticsInfo;
	/**
	 * 创建一个调度处理器
	 * @param scheduleCenter
	 * @param taskType
	 * @param aQueueTask
	 * @throws Exception
	 */
	public TBScheduleProcessorSleep(TBScheduleManager aManager,
			IScheduleTaskDeal<T> aQueueTask,	com.taobao.pamirs.schedule.StatisticsInfo aStatisticsInfo) throws Exception {
		this.scheduleManager = aManager;
		this.statisticsInfo = aStatisticsInfo;
		this.taskTypeInfo = this.scheduleManager.getTaskTypeInfo();
		this.taskDealProcessor = aQueueTask;
		if (this.taskDealProcessor instanceof IScheduleTaskDealSingle<?>) {
			if (taskTypeInfo.getExecuteNumber() > 1) {
				taskTypeInfo.setExecuteNumber(1);
			}
			isMutilTask = false;
		} else {
			isMutilTask = true;
		}
		if (taskTypeInfo.getFetchDataNumber() < taskTypeInfo.getThreadNumber() * 10) {
			logger.warn("参数设置不合理，系统性能不佳。【每次从数据库获取的数量fetchnum】 >= 【线程数量threadnum】 *【最少循环次数10】 ");
		}
		for (int i = 0; i < taskTypeInfo.getThreadNumber(); i++) {
			this.startThread(i);
		}
	}

	/**
	 * 需要注意的是，调度服务器从配置中心注销的工作，必须在所有线程退出的情况下才能做
	 * @throws Exception
	 */
	public void stopSchedule() throws Exception {
		// 设置停止调度的标志,调度线程发现这个标志，执行完当前任务后，就退出调度
		this.isStopSchedule = true;
		//清除所有未处理任务,但已经进入处理队列的，需要处理完毕
		this.taskList.clear();
	}

	private void startThread(int index) {
		Thread thread = new Thread(this);
		threadList.add(thread);
		String threadName = this.scheduleManager.getTaskTypeRunningInfo().getTaskType()+"-"
				+ this.scheduleManager.getCurrentSerialNumber() + "-exe"
				+ index;
		thread.setName(threadName);
		thread.start();
	}

	   public synchronized Object getScheduleTaskId() {
		     if (this.taskList.size() > 0)
		    	 return this.taskList.remove(0);  // 按正序处理
		     return null;
		   }

		   public synchronized Object[] getScheduleTaskIdMulti() {
		       if (this.taskList.size() == 0){
		         return null;
		       }
		       int size = taskList.size() > taskTypeInfo.getExecuteNumber() ? taskTypeInfo.getExecuteNumber()
						: taskList.size();
		       Object[] result = new Object[size];
		       for(int i=0;i<size;i++){
		      	 result[i] = this.taskList.remove(0);  // 按正序处理
		       }
		       return result;
		   }

	public void clearAllHasFetchData() {
		this.taskList.clear();
	}
	public boolean isDealFinishAllData() {
		return this.taskList.size() == 0 ;
	}

	public boolean isSleeping(){
    	return this.isSleeping;
    }
	protected int loadScheduleData() {
		try {
           //在每次数据处理完毕后休眠固定的时间
			if (this.taskTypeInfo.getSleepTimeInterval() > 0) {
				if(logger.isTraceEnabled()){
					logger.trace("处理完一批数据后休眠：" + this.taskTypeInfo.getSleepTimeInterval());
				}
				this.isSleeping = true;
			    Thread.sleep(taskTypeInfo.getSleepTimeInterval());
			    this.isSleeping = false;

				if(logger.isTraceEnabled()){
					logger.trace("处理完一批数据后休眠后恢复");
				}
			}

			List<String> taskQueues = this.scheduleManager.getCurrentScheduleQueue();
			// 根据队列信息查询需要调度的数据，然后增加到任务列表中
			if (taskQueues.size() > 0) {
				List<T> tmpList = this.taskDealProcessor.selectTasks(
						scheduleManager.getTaskTypeRunningInfo().getOwnSign(),
						this.taskTypeInfo.getTaskQueueNumber(), taskQueues,
						taskTypeInfo.getFetchDataNumber());
				if(tmpList != null){
				   this.taskList.addAll(tmpList);
				}
			} else {
				if(logger.isTraceEnabled()){
					   logger.trace("没有获取到需要处理的数据队列");
				}
			}
			addFetchNum(taskList.size(),"TBScheduleProcessor.loadScheduleData");
			return this.taskList.size();
		} catch (Throwable ex) {
			logger.error("Get tasks error.", ex);
		}
		return 0;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public void run(){
	      try {
	        long startTime =0;
	        while(true){
	          this.m_lockObject.addThread();
	          Object executeTask;
	          while (true) {
	            if(this.isStopSchedule == true){//停止队列调度
	              this.m_lockObject.realseThread();
	              this.m_lockObject.notifyOtherThread();//通知所有的休眠线程
				  this.threadList.remove(Thread.currentThread());
				  if(this.threadList.size()==0){
						this.scheduleManager.unRegisterScheduleServer();
				  }
				  return;
	            }

	            //加载调度任务
	            if(this.isMutilTask == false){
	              executeTask = this.getScheduleTaskId();
	            }else{
	              executeTask = this.getScheduleTaskIdMulti();
	            }

	            if(executeTask == null){
	              break;
	            }

	            try {//运行相关的程序
	              startTime =ScheduleUtil.getCurrentTimeMillis();
	              if (this.isMutilTask == false) {
						if (((IScheduleTaskDealSingle) this.taskDealProcessor).execute(executeTask,scheduleManager.getTaskTypeRunningInfo().getOwnSign()) == true) {
							addSuccessNum(1, ScheduleUtil.getCurrentTimeMillis()
									- startTime,
									"com.taobao.pamirs.schedule.TBScheduleProcessorSleep.run");
						} else {
							addFailNum(1, ScheduleUtil.getCurrentTimeMillis()
									- startTime,
									"com.taobao.pamirs.schedule.TBScheduleProcessorSleep.run");
						}
					} else {
						if (((IScheduleTaskDealMulti) this.taskDealProcessor)
								.execute((Object[]) executeTask,scheduleManager.getTaskTypeRunningInfo().getOwnSign()) == true) {
							addSuccessNum(((Object[]) executeTask).length, ScheduleUtil
									.getCurrentTimeMillis()
									- startTime,
									"com.taobao.pamirs.schedule.TBScheduleProcessorSleep.run");
						} else {
							addFailNum(((Object[]) executeTask).length, ScheduleUtil
									.getCurrentTimeMillis()
									- startTime,
									"com.taobao.pamirs.schedule.TBScheduleProcessorSleep.run");
						}
					}
	            }catch (Throwable ex) {
					if (this.isMutilTask == false) {
						addFailNum(1, ScheduleUtil.getCurrentTimeMillis() - startTime,
								"TBScheduleProcessor.run");
					} else {
						addFailNum(((Object[]) executeTask).length, ScheduleUtil
								.getCurrentTimeMillis()
								- startTime,
								"TBScheduleProcessor.run");
					}
					logger.warn("Task :" + executeTask + " 处理失败", ex);				
	            }
	          }
	          //当前队列中所有的任务都已经完成了。
	            if(logger.isTraceEnabled()){
				   logger.trace(Thread.currentThread().getName() +"：当前运行线程数量:" +this.m_lockObject.count());
			    }
				if (this.m_lockObject.realseThreadButNotLast() == false) {
					int size = 0;
					Thread.currentThread().sleep(100);
					startTime = ScheduleUtil.getCurrentTimeMillis();
					// 装载数据
					size = this.loadScheduleData();
					if (size > 0) {
						this.m_lockObject.notifyOtherThread();
					} else {
						//判断当没有数据的是否，是否需要退出调度
						if (this.isStopSchedule == false && this.scheduleManager.isContinueWhenData()== true ){						 
							if(logger.isTraceEnabled()){
								   logger.trace("没有装载到数据，start sleep");
							}
							this.isSleeping = true;
						    Thread.currentThread().sleep(this.scheduleManager.getTaskTypeInfo().getSleepTimeNoData());
						    this.isSleeping = false;
						    
						    if(logger.isTraceEnabled()){
								   logger.trace("Sleep end");
							}
						}else{
							//没有数据，退出调度，唤醒所有沉睡线程
							this.m_lockObject.notifyOtherThread();
						}
					}
					this.m_lockObject.realseThread();
				} else {// 将当前线程放置到等待队列中。直到有线程装载到了新的任务数据
					if(logger.isTraceEnabled()){
						   logger.trace("不是最后一个线程，sleep");
					}
					this.m_lockObject.waitCurrentThread();
				}
	        }
	      }
	      catch (Throwable e) {
	    	  logger.error(e.getMessage(), e);
	      }
	    }

	public void addFetchNum(long num, String addr) {
		
        this.statisticsInfo.addFetchDataCount(1);
        this.statisticsInfo.addFetchDataNum(num);
	}

	public void addSuccessNum(long num, long spendTime, String addr) {
        this.statisticsInfo.addDealDataSucess(num);
        this.statisticsInfo.addDealSpendTime(spendTime);
	}

	public void addFailNum(long num, long spendTime, String addr) {
      this.statisticsInfo.addDealDataFail(num);
      this.statisticsInfo.addDealSpendTime(spendTime);
	}
}
