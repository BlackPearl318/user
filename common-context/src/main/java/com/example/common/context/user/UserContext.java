package com.example.common.context.user;

public final class UserContext {

    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static String requireUserId() {
        String userId = USER_ID.get();
        if (userId == null) {
            throw new IllegalStateException("用户未登录");
        }
        return userId;
    }

    public static void clear() {
        USER_ID.remove();
    }

    private UserContext() {}
}

