package com.cfysu.ssi.worker.tbschedule;

import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScheduler<T> implements IScheduleTaskDealMulti<T> {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private TBScheduleManagerFactory managerFactory;
    
    protected DataSource dataSource;
    protected String taskType;
    protected String ownSign;
    protected Integer type;
    
    public void init() throws Exception {
        this.managerFactory.createTBScheduleManager(this.taskType, this.ownSign);
    }
    
//    public List<CepWorkTask> asList(Object[] taskArray) {
//        if (taskArray == null) {
//            return Collections.emptyList();
//        }
//
//        List<CepWorkTask> tasks = new ArrayList<CepWorkTask>();
//        for (Object task : taskArray) {
//            if (task != null && task instanceof CepWorkTask) {
//                tasks.add((CepWorkTask) task);
//            }
//        }
//        return tasks;
//    }
    
    /**
     * 计算本机需要取得的任务数据数量, 当只有一台机器运行的情况下取得fetchNum数量的任务,
     * 否则计算本机要取得的任务数量(此数量使命中本机的数量约为fetchNum)
     * 
     * @param queueNum 对应任务类型的总处理队列数
     * @param fetchNum  每次取任务的总数
     * @param subQueues 分配到本机的队列数(可能有多台机器运行总共queueNum数量的队列)
     * @return
     */
    public int fecthNum(int queueNum, int fetchNum, List<String> subQueues) {
        if (queueNum != subQueues.size()) {
            fetchNum = fetchNum * queueNum / subQueues.size();
        }
        return fetchNum;
    }
    
    /**
     * 用于判断由id指定的任务是否由本机上的队列处理
     * @param queueNum
     *            抓取到的队列总数
     * @param id 任务id或其它
     * @param subQueues
     *            当前实例分配到的队列,队列编号(1,2,3,4.....)
     * @return
     */
    public boolean isMyTask(long queueNum, long id, List<?> subQueues) {
        if (queueNum == subQueues.size()) {//说明只有一台机器
            return true;
        }
        
        long m = id % queueNum;//计算此task的模值,用于确定分配到哪个队列，对应PAMIRS_SCHEDULE_QUEUE表中的queue_id
        for (Object o : subQueues) {//遍历本机器上的分配的任务队列，如果计算得出的队列在本机器上，返回true
            if (m == Long.parseLong(o.toString())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 筛选出应由本机处理的任务
     * 
     * @param queueNum 对应任务类型的总处理队列数
     * @param subQueues 分配到本机的队列数(可能有多台机器运行总共queueNum数量的队列)
     * @param unhandles 未处理的任务
     * @param methodName 用于取得任务的id或者其它可以唯一指定此任务的整型值(用于计算此任务属于哪个队列)
     * @return
     */
    public List<T> assign(int queueNum, List<String> subQueues, List<T> unhandles, String methodName) {
        if (queueNum == subQueues.size()) {
            return unhandles;
        }
        
        List<T> assigns = new ArrayList<T>();
        for (T t : unhandles) {
            try {
                Method method = t.getClass().getMethod(methodName);
                Long value;
                if(methodName!=null && methodName.equals("getRefId")){
                	if(method.invoke(t) == null || "".equals(method.invoke(t))){
                		value = new Long(9999);
                	}else{
                		logger.info("cooperation_code:"+method.invoke(t));
                		value = new Long(String.valueOf(method.invoke(t).hashCode()));
                	}
                }else{
                	value = new Long(String.valueOf(method.invoke(t)));
                }
                logger.info("value"+value);
                // 防止负数的出现，特殊处理
                if(value < 0){
                	value = Math.abs(value);
                }
                if (this.isMyTask(queueNum, value, subQueues)) {
                    assigns.add(t);
                }
            } catch (Exception e) {
                this.logger.error("error!", e);
                continue;
            }
        }
        return assigns;
    }
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public void setOwnSign(String ownSign) {
        this.ownSign = ownSign;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public TBScheduleManagerFactory getManagerFactory() {
        return this.managerFactory;
    }
    
    public void setManagerFactory(TBScheduleManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }
    
}
