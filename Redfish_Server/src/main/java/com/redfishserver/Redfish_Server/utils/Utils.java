package com.redfishserver.Redfish_Server.utils;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Utils {

    public static final String API_REQUEST_TYPE_GET = "GET";
    public static final String API_REQUEST_TYPE_HEAD = "HEAD";
    public static final String API_REQUEST_TYPE_PATCH = "PATCH";
    public static final String API_REQUEST_TYPE_PUT = "PUT";
    public static final String API_REQUEST_TYPE_DELETE = "DELETE";
    public static final String API_REQUEST_TYPE_POST = "POST";

    public static final String API_CHARACTER_SET_NAME = "utf-8";

    public static final String SSE_CONNECTION_NAME = "SSE";

    public static Long BYTES_IN_MB = Long.valueOf(1000000);

    public static boolean isSizeILimit(JSONObject jsonObject, long sizeLimit) {
        String strObj = jsonObject.toString();
        long numberOfBytes = strObj.length();
        return numberOfBytes <= sizeLimit;
    }
}
