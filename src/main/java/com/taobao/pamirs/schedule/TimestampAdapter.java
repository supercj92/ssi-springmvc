/**
 * @(#) TimestampAdapter.java Created on 2010-12-27 下午05:50:29
 * Copyright (c) 2010 by Taobao.com.
 */
package com.taobao.pamirs.schedule;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * 类名称：TimestampAdapter
 * 类描述：Timestamp适配器,用于解决web service传输异常问题
 * 创建人：jishao
 * 创建时间：2010-12-27 下午05:50:29
 * 
 */
public class TimestampAdapter extends XmlAdapter<Date, Timestamp> {   
	  
	   public Date marshal(Timestamp t) {   
	     return new Date(t.getTime());   
	   }   
	  
	   public Timestamp unmarshal(Date d) {   
	     return new Timestamp (d.getTime());   
	   }   
	  
	} 
