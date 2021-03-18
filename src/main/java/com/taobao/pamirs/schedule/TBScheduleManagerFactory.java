package com.taobao.pamirs.schedule;

import com.taobao.pamirs.schedule.IScheduleAlert;
import com.taobao.pamirs.schedule.IScheduleConfigCenterClient;
import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.MBeanManagerFactory;
import com.taobao.pamirs.schedule.ScheduleTaskType;
import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.TBScheduleManager;
import com.taobao.pamirs.schedule.TBScheduleManagerFactoryMBean;
import com.taobao.pamirs.schedule.TBScheduleManagerMBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 调度服务器构造器
 * 
 * @author xuannan
 * 
 */
public class TBScheduleManagerFactory implements ApplicationContextAware {
	protected static transient Log logger = LogFactory.getLog(com.taobao.pamirs.schedule.TBScheduleManagerFactory.class);
	/**
	 * 调度配置中心客服端
	 */
	private IScheduleConfigCenterClient scheduleConfigCenter;
	/**
	 * 告警接口
	 */
	private IScheduleAlert scheduleAlert;

	private ApplicationContext applicationcontext;

	private String						dealBeanName;

	private static String mbeanServerAgentId;

	public TBScheduleManagerFactory() {

	}

	public void init() {
		try {
			String name = "pamirs:name=schedule.ScheduleManangerFactory." + this.hashCode();
			if (MBeanManagerFactory.isRegistered(name) == false) {
				MBeanManagerFactory.registerMBean(
						new TBScheduleManagerFactoryMBean(this), name);
			}else{
				logger.warn("重复注册com.taobao.pamirs.schedule.TBScheduleManagerFactory");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TBScheduleManager createTBScheduleManager(String taskType,
			IScheduleTaskDeal<?> aQueueTask) throws Exception {
         return createTBScheduleManager(taskType,TBScheduleManager.OWN_SIGN_BASE);
	}

	/**
	 * 创建调度服务器
	 *
	 * @param taskType
	 *            任务类型
	 * @param aQueueTask
	 *            任务处理器
	 * @return
	 * @throws Exception
	 */
	public TBScheduleManager createTBScheduleManager(String taskType,String ownSign)
			throws Exception {
		if (scheduleConfigCenter == null) {
			throw new Exception("没有设置配置中心客户端的接口实现，请在Spring中配置，或者通过程序设置");
		}
		int managerPort = MBeanManagerFactory.getHtmlAdaptorPort();
		String jmxUrl = MBeanManagerFactory.getManangerUrl();
        //清除已经过期1天的TASK,OWN_SIGN的组合。超过一天没有活动server的视为过期
		ScheduleTaskType baseTaskTypeInfo = scheduleConfigCenter.loadTaskTypeBaseInfo(taskType);

		scheduleConfigCenter.clearExpireTaskTypeRunningInfo(taskType,ScheduleUtil.getLocalIP() + "清除过期OWN_SIGN信息",baseTaskTypeInfo.getExpireOwnSignInterval());
		Object dealBean = dealBeanName != null ? applicationcontext.getBean(dealBeanName) : applicationcontext.getBean(baseTaskTypeInfo.getDealBeanName());
		if (dealBean == null) {
			throw new Exception( "SpringBean " + dealBeanName + " 不存在");
		}
		if (dealBean instanceof IScheduleTaskDeal == false) {
			throw new Exception( "SpringBean " + baseTaskTypeInfo.getDealBeanName() + " 没有实现 IScheduleTaskDeal接口");
		}
		TBScheduleManager result = new TBScheduleManager(taskType,ownSign,managerPort, jmxUrl, scheduleConfigCenter,
				(IScheduleTaskDeal<?>)dealBean, scheduleAlert);
		MBeanManagerFactory.registerMBean(new TBScheduleManagerMBean(result), result.getmBeanName());
		return result;
	}

	public TBScheduleManager createTBScheduleManager(String taskType,String dealBeanName,String ownSign) throws Exception {
		this.dealBeanName = dealBeanName;
		return createTBScheduleManager(taskType, ownSign);
	}

	public static void unregisterMBean(String aMBeanName) throws Exception {
		MBeanManagerFactory.unregisterMBean(aMBeanName);
	}

	public String[] getScheduleTaskDealList() {
		return applicationcontext.getBeanNamesForType(IScheduleTaskDeal.class);

	}

	public IScheduleConfigCenterClient getScheduleConfigCenter() {
		return scheduleConfigCenter;
	}

	public void setScheduleConfigCenter(IScheduleConfigCenterClient aScheduleConfigCenter) {
		scheduleConfigCenter = aScheduleConfigCenter;
	}

	public void setApplicationContext(ApplicationContext aApplicationcontext) throws BeansException {
		applicationcontext = aApplicationcontext;
	}

	public Object getBean(String beanName) {
		return applicationcontext.getBean(beanName);
	}

	public void setScheduleAlert(IScheduleAlert aScheduleAlert) {
		scheduleAlert = aScheduleAlert;
	}

	public void setMbeanServerAgentId(String aMbeanServerAgentId) {
		mbeanServerAgentId = aMbeanServerAgentId;
	}
	public static String getMbeanServerAgentId() {
		return mbeanServerAgentId;
	}

}
