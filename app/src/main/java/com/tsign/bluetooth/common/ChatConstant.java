package com.tsign.bluetooth.common;

import java.util.UUID;

public class ChatConstant {

    /*KEY*/
    public static final String NAME_SECURE = "BluetoothChatSecure";
    public static final String NAME_INSECURE = "BluetoothChatInsecure";

    /**
     * UUID
     */
    public static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    //会出现连接失败的情况
    public static final UUID UUID_STRING = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    /**
     * 服务器别名
     */
    public static final String PROTOCOL_SCHEME_RFCOMM = "server_name";
    public static final String EXTRA_REMOTE_ADDRESS = "remoteAddress";
    public static final String EXTRA_ERROR_MSG = "error_msg";

    /**
     * 已连接到服务器
     */
    public static final int CONNECTED_SERVER = 1;
    /**
     * 连接服务器出错
     */
    public static final int CONNECT_SERVER_ERROR = CONNECTED_SERVER + 1;
    /**
     * 正在连接服务器
     */
    public static final int IS_CONNECTING_SERVER = CONNECT_SERVER_ERROR + 1;
    /**
     * 等待客户端连接
     */
    public static final int WAITING_FOR_CLIENT = IS_CONNECTING_SERVER + 1;
    /**
     * 已连接客户端
     */
    public static final int CONNECTED_CLIENT = WAITING_FOR_CLIENT + 1;
    /**
     * 连接客户端出错
     */
    public static final int CONNECT_CLIENT_ERROR = CONNECTED_CLIENT + 1;
    /**
     * 客户端重复
     */
    public static final int CONNECT_CLIENT_REPEAT_ERROR = CONNECT_CLIENT_ERROR + 1;


    /**
     * 发送消息
     */
    public static final String ACTION_RECEIVED_NEW_MSG = "com.tsign.bluetooth.action.received.new.msg";
    /**
     * 连接成功
     */
    public static final String ACTION_CONNECTED_SERVER = "com.tsign.bluetooth.action.connected_server";
    /**
     * 连接失败
     */
    public static final String ACTION_CONNECT_ERROR = "com.tsign.bluetooth.action.connect_error";
    /**
     * 服务器初始化失败
     */
    public static final String ACTION_INIT_COMPLETE = "com.tsign.bluetooth.action.server_init";
    /**
     * 客户端重复
     */
    public static final String ACTION_CLIENT_REPEAT_COMPLETE = "com.tsign.bluetooth.action.client_repeat";
}
