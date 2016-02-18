package com.goldgrother.repeatmana.Other;

/**
 * Created by v on 2016/1/1.
 */
public class UserAccount {
    private static UserAccount user = new UserAccount();
    private String UserID;
    private String UserPWD;
    private String DormID;
    private String Photo;


    private UserAccount() {
    }

    public static UserAccount getUserAccount() {
        return user;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserPWD() {
        return UserPWD;
    }

    public void setUserPWD(String userPWD) {
        UserPWD = userPWD;
    }

    public String getDormID() {
        return DormID;
    }

    public void setDormID(String dormID) {
        DormID = (dormID != null && !dormID.isEmpty() && !dormID.equals("null")) ? dormID : "5203";
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = (photo != null && !photo.isEmpty() && !photo.equals("null")) ? photo : "";
    }
}
