package com.cfysu.ssi.worker.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cfysu.ssi.worker.SpringTimer;

@Component("springTimer")
public class SpringTimerImpl implements SpringTimer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringTimerImpl.class);
	@Scheduled(cron ="*/10 * * * * ?")
	public void print() {
		LOGGER.info("-------------timer is running--------------");
	}

}
