package com.goldgrother.repeatmana.Other;

/**
 * Created by v on 2016/1/1.
 */
public class URLs {
    private static final String serverip = "http://140.131.115.42:8080/gbDormitory/AppProcess/";

    public static final String url_login = serverip + "mana_login";
    /**
     *   LoadProblem
     */
    public static final String url_allproblem = serverip + "pb_allproblem";
    /**
     *   Response
     */
    public static final String url_response = serverip + "pb_response";
    public static final String url_allresponse = serverip + "pb_allresponse";
    /**
     *   AddResponse
     */
    public static final String url_addresponse = serverip + "pb_addresponse";

    public static final String url_repeatproblem = serverip + "mana_repeatproblem";
    /**
     *   Image
     */
    public static final String url_loadimage = serverip + "pb_loadimage";
    public static final String url_uploadimage = serverip + "pb_uploadImage";
}
