package com.goldgrother.repeatmana.Other;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v on 2016/1/4.
 */
public class Worker {
    private String WorkerNo;
    private List<ProblemRecord> Items;

    public String getWorkerNo() {
        return WorkerNo;
    }

    public void setWorkerNo(String workerNo) {
        WorkerNo = workerNo;
    }

    public List<ProblemRecord> getItems() {
        return Items;
    }

    public void setItems(List<ProblemRecord> Items) {
        this.Items = Items;
    }

}
