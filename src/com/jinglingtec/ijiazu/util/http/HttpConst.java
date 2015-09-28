package com.jinglingtec.ijiazu.util.http;

public interface HttpConst
{
    // http response code, success
    int ERR_SUCCESS = 9;

    // constants for HTTP interaction with server, begin
    // HTTP request timeout, 60 seconds
    int REQUEST_TIMEOUT = 60 * 1000;
    // domain name of fodrive API
    String FODRIVE_API_DOMAIN = "http://192.168.1.105:18701";
    //    String FODRIVE_API_DOMAIN = "http://min.ijiazu.com";  // internal API testing URL
    //    String FODRIVE_API_DOMAIN = "http://api.ijiazu.com";   // public internet UTL

    String REQUEST_BODY = "RequestBody";
    String REQUEST_SIGN = "RequestSign";

    //    String API_MANAGE_ACCOUNT = "/mapi/manageaccount/";
    //    String API_ACCOUNT_LOGIN = "/mapi/alogin/";

    String API_ACCOUNT_CREATE = "/mapi/account/create/";
    String API_ACCOUNT_MANAGE = "/mapi/account/manager/";
    String API_ACCOUNT_LOGIN = "/mapi/account/login/";
    String API_USER_FEEDBACK = "/mapi/feedback/";

    String API_CHECK_LATEST_INFO = "/mapi/checklatestinfo/";
    // constants for HTTP interaction with server, end

}

