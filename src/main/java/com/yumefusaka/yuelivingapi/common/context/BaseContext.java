package com.yumefusaka.yuelivingapi.common.context;

public class BaseContext {

    public static ThreadLocal<String> currentId = new ThreadLocal<>();
    public static ThreadLocal<String> currentRoleId = new ThreadLocal<>();

    public static void setCurrentId(String id) {
        currentId.set(id);
    }

    public static String getCurrentId() {
        return currentId.get();
    }

    public static void removeCurrentId() {
        currentId.remove();
    }

    public static void setCurrentRoleId(String roleId) {
        currentRoleId.set(roleId);
    }

    public static String getCurrentRoleId() {
        return currentRoleId.get();
    }

    public static void removeCurrentRoleId() {
        currentRoleId.remove();
    }

    public static void clear() {
        removeCurrentId();
        removeCurrentRoleId();
    }
}