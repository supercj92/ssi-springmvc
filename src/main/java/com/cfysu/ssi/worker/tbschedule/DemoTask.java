package com.cfysu.ssi.worker.tbschedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DemoTask extends AbstractScheduler<String>{
    public boolean execute(Object[] tasks, String ownSign) throws Exception {
        System.out.println(Thread.currentThread().getName() + ":size=" + tasks.length);
        Thread.sleep(500);
        return false;
    }

    public List<String> selectTasks(String ownSign, int taskQueueNum, List taskQueueList, int eachFetchDataNum) throws Exception {
        System.out.println("select task...");
        List<String> dataList = new ArrayList<String>();
        for(int i = 0;i < 100;i++){
            dataList.add(String.valueOf(i));
        }
        return dataList;
    }

    public Comparator getComparator() {
        return null;
    }
}
