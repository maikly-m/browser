package com.molihuan.pathselector.utils;

import java.util.List;

import androidx.annotation.NonNull;

public interface OnPermissionCallback {

    /**
     * 有权限被同意授予时回调
     *
     * @param permissions           请求成功的权限组
     * @param allGranted            是否全部授予了
     */
    void onGranted(@NonNull List<String> permissions, boolean allGranted);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param permissions            请求失败的权限组
     * @param doNotAskAgain          是否勾选了不再询问选项
     */
    default void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {}
}