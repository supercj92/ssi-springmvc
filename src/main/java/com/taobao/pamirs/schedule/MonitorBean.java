package com.taobao.pamirs.schedule;

import com.taobao.pamirs.schedule.IScheduleConfigCenterClient;

public class MonitorBean {
	
	private static IScheduleConfigCenterClient scheduleConfigCenter;
	public void setScheduleConfigCenter(
			IScheduleConfigCenterClient aScheduleConfigCenter) {
		scheduleConfigCenter = aScheduleConfigCenter;
	}

	public static IScheduleConfigCenterClient getScheduleConfigCenterClient(){
		return scheduleConfigCenter;
	}
}
