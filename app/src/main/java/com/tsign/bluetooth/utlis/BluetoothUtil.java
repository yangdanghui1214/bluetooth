package com.tsign.bluetooth.utlis;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tsign.bluetooth.common.ChatConstant;
import com.tsign.bluetooth.model.BluetoothMessage;
import com.tsign.bluetooth.thread.ClientThread;
import com.tsign.bluetooth.thread.ReadThread;
import com.tsign.bluetooth.thread.ServerThread;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static com.tsign.bluetooth.common.ChatConstant.ACTION_CLIENT_REPEAT_COMPLETE;
import static com.tsign.bluetooth.common.ChatConstant.ACTION_CONNECTED_SERVER;
import static com.tsign.bluetooth.common.ChatConstant.ACTION_RECEIVED_NEW_MSG;
import static com.tsign.bluetooth.common.ChatConstant.EXTRA_ERROR_MSG;
import static com.tsign.bluetooth.common.ChatConstant.EXTRA_REMOTE_ADDRESS;

/**
 * 蓝牙工具类
 *
 * @author 13001
 */
public class BluetoothUtil {

    private static BluetoothUtil instance;

    private BluetoothAdapter bluetoothAdapter;
    private Context mContext;

    /**
     * socket集合
     */
    private HashMap<String, BluetoothSocket> socketMap = new HashMap<>();
    private HashMap<String, ReadThread> readThreadMap = new HashMap<>();


    /**
     * 工具类初始化
     */
    private BluetoothUtil() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 工具类初始化
     *
     * @return
     */
    public static BluetoothUtil getInstance() {
        if (instance == null) {
            instance = new BluetoothUtil();
        }
        return instance;
    }

    /**
     * 获取 bluetoothAdapter 对象
     *
     * @return bluetoothAdapter
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * 获取 蓝牙是否开启
     *
     * @return isEnabled
     */
    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     *
     * @return isEnabled
     */
    public void getBluetoothAdapterEnable() {
        bluetoothAdapter.enable();
    }

    /**
     * 开始搜索
     *
     * @return isEnabled
     */
    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 返回绑定(成对)到本地适配器的BluetoothDevice对象集。
     *
     * @return
     */
    public ArrayList<BluetoothDevice> getAvailableDevices() {
        Set<BluetoothDevice> availableDevices = bluetoothAdapter.getBondedDevices();
        ArrayList availableList = new ArrayList();
        for (Iterator<BluetoothDevice> iterator = availableDevices.iterator(); iterator.hasNext(); ) {
            availableList.add(iterator.next());
        }
        return availableList;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * ==============================================================================================
     * ================================       线程连接读取操作    ===================================
     * ==============================================================================================
     */

    private ClientThread clientConnectThread;
    private ServerThread startServerThread;

    private String deviceMac = "";
    private String deviceAddress = "";


    /**
     * 启动服务线程
     */
    public void initServer() {
        startServerThread = new ServerThread();
        startServerThread.start();
    }

    /**
     * 连接蓝牙
     *
     * @param device
     */
    public void connect(BluetoothDevice device) {
        if (device == null || TextUtils.isEmpty(device.getAddress())) {
            Log.e("zxy", "connect: address is null !");
            return;
        }
        if (!socketMap.containsKey(device.getAddress())) {
            if (clientConnectThread != null && clientConnectThread.isAlive()) {
                Log.e("zxy", "connect: 正在连接");
                return;
            }
            if (getSocketMap().size() > 0) {
                shutdownClient();
            }
            clientConnectThread = new ClientThread(device);
            clientConnectThread.start();
        } else {
            Log.e("zxy", "connect: 已连接");
        }
    }

    /**
     * 发送数据
     *
     * @param msg
     * @param remoteDeviceAddress
     */
    public void sendMessageHandle(BluetoothMessage msg, String remoteDeviceAddress) {
        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
        if (socket == null) {
//            Toast.makeText(mContext, "连接断开，正在尝试重新连接", Toast.LENGTH_SHORT).show();
//            connect(bluetoothAdapter.getRemoteDevice(remoteDeviceAddress));
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(msg);

            msg.setReceiver(remoteDeviceAddress);
        } catch (IOException e) {
            closeConnection(remoteDeviceAddress);
            e.printStackTrace();
        }
    }

    /**
     * 读取失败
     *
     * @param remoteDeviceAddress
     */
    public void closeConnection(String remoteDeviceAddress) {
        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ReadThread readThread = readThreadMap.get(remoteDeviceAddress);
        if (readThread != null) {
            readThread.readClose();
        }
        socketMap.remove(remoteDeviceAddress);
        readThreadMap.remove(remoteDeviceAddress);
        if (socketMap.size() < 1) {
            shutdownClient();
        }
    }

    /**
     * 添加 BluetoothSocket连接
     *
     * @param address
     * @param socket
     */
    public void putSocketMap(String address, BluetoothSocket socket) {
        socketMap.put(address, socket);
    }

    /**
     * 移除 BluetoothSocket 连接
     *
     * @param address
     */
    public void removeSocketMap(String address) {
        socketMap.remove(address);
    }

    /**
     * 获取 socketMap
     *
     * @return socketMap
     */
    public HashMap<String, BluetoothSocket> getSocketMap() {
        return socketMap;
    }

    /**
     * 连接取消
     *
     * @param socket
     * @param address
     */
    public void socketClear(final BluetoothSocket socket, String address) {
        BluetoothMessage bluetoothMessage = new BluetoothMessage();
        bluetoothMessage.setType(-1);
        try {
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(bluetoothMessage);
            bluetoothMessage.setReceiver(address);
            Log.e("zxy", "connect: 发送成功");
        } catch (IOException e) {
            closeConnection(address);
            Log.e("zxy", "connect: 发送失败");
            e.printStackTrace();
        }
        getLinkDetectedHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    /**
     * 添加 读取线程
     *
     * @param address
     * @param readThread
     */
    public void putReadThreadMap(String address, ReadThread readThread) {
        readThreadMap.put(address, readThread);
    }

    /**
     * 获取 读取线程列表
     *
     * @return readThreadMap
     */
    public HashMap<String, ReadThread> getReadThreadMap() {
        return readThreadMap;
    }

    /**
     * 获取最后一次的设备连接
     *
     * @return
     */
    public boolean isDeviceAddress() {
        return getSocketMap().containsKey(getDeviceAddress());
    }

    /**
     * 获取是否有设备连接
     *
     * @param address
     * @return
     */
    public boolean getDeviceMac(String address) {
        return TextUtils.isEmpty(deviceMac) || address.equals(deviceMac);
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    /**
     * 获取已连接的Mac地址
     *
     * @return
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * 设置已连接的Mac地址
     *
     * @param deviceAddress
     */
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public Handler getLinkDetectedHandler() {
        return linkDetectedHandler;
    }

    @SuppressLint("HandlerLeak")
    private Handler linkDetectedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == ChatConstant.CONNECT_CLIENT_REPEAT_ERROR) {
                Intent intent = new Intent();
                intent.setAction(ACTION_CLIENT_REPEAT_COMPLETE);
                mContext.sendOrderedBroadcast(intent, null);
                Log.e("zxy", "有其他设备连接，请检查设备");
            } else if (msg.obj instanceof BluetoothMessage) {
                BluetoothMessage message = (BluetoothMessage) msg.obj;
                Intent intent = new Intent();
                intent.setAction(ACTION_RECEIVED_NEW_MSG);
                intent.putExtra("msg", message);
                mContext.sendOrderedBroadcast(intent, null);

                Log.e("zxy", "handleMessage: 接收到数据");
            } else {
                Intent intent = new Intent();
                switch (msg.what) {
                    case ChatConstant.WAITING_FOR_CLIENT:
                        //初始化服务器完成
                        intent.setAction(ChatConstant.ACTION_INIT_COMPLETE);
                        break;
                    case ChatConstant.IS_CONNECTING_SERVER:
                        //正在连接服务器

                        break;
                    case ChatConstant.CONNECTED_CLIENT:
                        //有客户端连接到自己

                        break;
                    case ChatConstant.CONNECT_CLIENT_ERROR:
                        //连接客户端出错
                        setDeviceMac("");
                        break;
                    case ChatConstant.CONNECT_SERVER_ERROR:
                        setDeviceMac("");
                        //连接服务器出错
                        intent.putExtra(EXTRA_ERROR_MSG, (String) msg.obj);
                        intent.setAction(ChatConstant.ACTION_CONNECT_ERROR);
                        break;
                    case ChatConstant.CONNECTED_SERVER:
                        //连接到服务器
                        intent.putExtra(EXTRA_REMOTE_ADDRESS, (String) msg.obj);
                        intent.setAction(ACTION_CONNECTED_SERVER);
                        break;
                    default:
                        break;
                }
                mContext.sendBroadcast(intent);
            }

        }
    };


    /**
     * 停止蓝牙
     */
    public void onDestroy() {
        shutdownClient();
        shutdownServer();
    }

    /**
     * 停止服务器
     */
    private void shutdownServer() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (startServerThread != null) {
                        startServerThread.close();
                        startServerThread.interrupt();
                        startServerThread = null;
                    }
                    Set<String> keySet = socketMap.keySet();
                    for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                        String remoteDeviceAddress = iterator.next();
                        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
                        if (socket != null) {
                            socket.close();
                            socketMap.remove(remoteDeviceAddress);
                        }

                        ReadThread mreadThread = readThreadMap.get(remoteDeviceAddress);
                        if (mreadThread != null) {
                            mreadThread.interrupt();
                            readThreadMap.remove(remoteDeviceAddress);
                        }
                    }
                } catch (IOException e) {
                    Log.e("server", "mServerSocket.close()", e);
                }
            }
        }.start();
    }


    /**
     * 停止客户端连接
     */
    private void shutdownClient() {
        setDeviceMac("");
        setDeviceAddress("");
        new Thread() {
            @Override
            public void run() {
                try {
                    if (clientConnectThread != null) {
                        clientConnectThread.close();
                        clientConnectThread.interrupt();
                        clientConnectThread = null;
                    }
                    Set<String> keySet = socketMap.keySet();
                    for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext(); ) {
                        String remoteDeviceAddress = iterator.next();
                        BluetoothSocket socket = socketMap.get(remoteDeviceAddress);
                        if (socket != null) {
                            socket.close();
                            socketMap.remove(remoteDeviceAddress);
                        }
                        ReadThread mreadThread = readThreadMap.get(remoteDeviceAddress);
                        if (mreadThread != null) {
                            mreadThread.interrupt();
                            readThreadMap.remove(remoteDeviceAddress);
                        }

                    }
                } catch (Exception e) {
                    Log.d("shutdownCLient", e.getMessage());
                }
            }
        }.start();
    }

}
