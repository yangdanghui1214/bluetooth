package com.tsign.bluetooth.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.tsign.bluetooth.common.ChatConstant;
import com.tsign.bluetooth.utlis.BluetoothUtil;

import java.io.IOException;

/**
 * 客户端线程
 *
 * @author 13001
 */
public class ClientThread extends Thread {
    private BluetoothSocket socket;
    private BluetoothDevice device;

    public ClientThread(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public void run() {
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(ChatConstant.UUID_INSECURE);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            close();
            Message msg = new Message();
            msg.obj = "连接服务端异常！断开连接重试。";
            msg.what = ChatConstant.CONNECT_SERVER_ERROR;
            BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg);
            return;
        }

        if (socket.isConnected()) {
            BluetoothUtil.getInstance().putSocketMap(device.getAddress(), socket);
            BluetoothUtil.getInstance().setDeviceMac(socket.getRemoteDevice().getAddress());
            BluetoothUtil.getInstance().setDeviceAddress(socket.getRemoteDevice().getAddress());
            Log.e("zxy", "蓝牙连接成功");

            Intent intent = new Intent();
            intent.putExtra(ChatConstant.EXTRA_REMOTE_ADDRESS, device.getAddress());
            intent.setAction(ChatConstant.ACTION_CONNECTED_SERVER);
            BluetoothUtil.getInstance().getContext().sendBroadcast(intent);

            //启动接受数据
            ReadThread mreadThread = new ReadThread(device.getAddress());
            BluetoothUtil.getInstance().putReadThreadMap(device.getAddress(), mreadThread);
            mreadThread.start();
        } else {
            Log.e("zxy", "蓝牙连接失败");
            close();

            Message msg = new Message();
            msg.obj = "连接服务端异常！断开连接重试。";
            msg.what = ChatConstant.CONNECT_SERVER_ERROR;
            BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg);
        }
    }

    /**
     * 销毁连接
     */
    public void close() {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
            socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}