package com.goldgrother.repeatmana.Other;

/**
 * Created by v on 2016/1/4.
 */
public class WorkerProblem {
    private int PRSNo;
    private String CustomerNo;
    private String FLaborNo;
    private String ProblemDescription;
    private String CreateProblemDate;
    private String ResponseResult;
    private String ResponseDate;
    private String ResponseID;
    private String SatisfactionDegree;
    private String ProblemStatus;

    public int getPRSNo() {
        return PRSNo;
    }

    public void setPRSNo(int PRSNo) {
        this.PRSNo = PRSNo;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getFLaborNo() {
        return FLaborNo;
    }

    public void setFLaborNo(String FLaborNo) {
        this.FLaborNo = FLaborNo;
    }

    public String getProblemDescription() {
        return ProblemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        ProblemDescription = problemDescription;
    }

    public String getCreateProblemDate() {
        return CreateProblemDate;
    }

    public void setCreateProblemDate(String createProblemDate) {
        CreateProblemDate = createProblemDate;
    }

    public String getResponseResult() {
        return ResponseResult;
    }

    public void setResponseResult(String responseResult) {
        if (responseResult.length() == 4) {
            if (responseResult.equals("null") || responseResult.equals("NULL"))
                responseResult = "";
        }
        ResponseResult = responseResult;
    }

    public String getResponseDate() {
        return ResponseDate;
    }

    public void setResponseDate(String responseDate) {
        if (responseDate.length() == 4) {
            if (responseDate.equals("null") || responseDate.equals("NULL"))
                responseDate = "";
        }
        ResponseDate = responseDate;
    }

    public String getResponseID() {
        return ResponseID;
    }

    public void setResponseID(String responseID) {
        if (responseID.length() == 4) {
            if (responseID.equals("null") || responseID.equals("NULL"))
                responseID = "";
        }
        ResponseID = responseID;
    }

    public String getSatisfactionDegree() {
        return SatisfactionDegree;
    }

    public void setSatisfactionDegree(String satisfactionDegree) {
        SatisfactionDegree = satisfactionDegree;
    }

    public String getProblemStatus() {
        return ProblemStatus;
    }

    public void setProblemStatus(String problemStatus) {
        ProblemStatus = problemStatus;
    }
}
