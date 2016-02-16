package com.goldgrother.repeatmana.Other;

/**
 * Created by hao_jun on 2016/2/15.
 */
public class ProblemResponse {
    private int PRSNo;
    private String ResponseContent;
    private String ResponseDate;

    private String ResponseID;
    private String ResponseRole;

    public int getPRSNo() {
        return PRSNo;
    }

    public void setPRSNo(int PRSNo) {
        this.PRSNo = PRSNo;
    }

    public String getResponseContent() {
        return ResponseContent;
    }

    public void setResponseContent(String responseContent) {
        ResponseContent = responseContent;
    }

    public String getResponseDate() {
        return ResponseDate;
    }

    public void setResponseDate(String responseDate) {
        ResponseDate = responseDate;
    }

    public String getResponseID() {
        return ResponseID;
    }

    public void setResponseID(String responseID) {
        ResponseID = responseID;
    }

    public String getResponseRole() {
        return ResponseRole;
    }

    public void setResponseRole(String responseRole) {
        ResponseRole = responseRole;
    }
}
