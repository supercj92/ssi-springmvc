package com.cfysu.ssi.tbschedule;

import java.util.Comparator;
import java.util.List;

public class DemoTask extends AbstractScheduler{
    public boolean execute(Object[] tasks, String ownSign) throws Exception {
        return false;
    }

    public List selectTasks(String ownSign, int taskQueueNum, List taskQueueList, int eachFetchDataNum) throws Exception {
        System.out.println("select task...");
        return null;
    }

    public Comparator getComparator() {
        return null;
    }
}
