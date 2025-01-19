package com.task.backend.utils;

public class GlobalVars {
    public static String OK = "000";
    public static String OK_INF = "010"; // means the response is OK but a condition was not met, and we need to inform, the user of it
    public static String SERVER_ERROR = "005";
    public static String NOT_FOUND = "004";
    public static String UNAUTHORIZED = "003";
    public static String ALREADY_EXISTS = "002";
    public static String INVALID_ACTION = "001";
    public static String LIMIT_EXCEEDED = "006";
    public static String BLOCKED = "008";
    public static String EXPIRED = "007";
    public static String FAILED = "111";

    public static String SERVER_ERROR_MESSAGE = "Operation failed, please contact your support !";
    public static String OK_MESSAGE = "Success";
    public static String INVALID_ACTION_MESSAGE = "Invalid action requested !";
}
