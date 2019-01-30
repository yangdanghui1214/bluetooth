package com.tsign.bluetooth.thread;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.tsign.bluetooth.common.ChatConstant;
import com.tsign.bluetooth.utlis.BluetoothUtil;

import java.io.IOException;

/**
 * 服务器
 *
 * @author 13001
 */
public class ServerThread extends Thread {

    private BluetoothServerSocket mServerSocket;

    @Override
    public void run() {

        try {
            /* 创建一个蓝牙服务器
             * 参数分别：服务器名称、UUID   */
            mServerSocket = BluetoothUtil.getInstance().getBluetoothAdapter().listenUsingRfcommWithServiceRecord(
                    ChatConstant.PROTOCOL_SCHEME_RFCOMM, ChatConstant.UUID_INSECURE);


            while (true) {

                Message msg = new Message();
                msg.obj = "请稍候，正在等待客户端的连接...";
                msg.what = ChatConstant.WAITING_FOR_CLIENT;
                BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg);

                Log.e("zxy", "wait cilent connect...");
                /* 接受客户端的连接请求 */
                BluetoothSocket socket = mServerSocket.accept();

                if (BluetoothUtil.getInstance().getDeviceMac(socket.getRemoteDevice().getAddress())) {
                    BluetoothUtil.getInstance().putSocketMap(socket.getRemoteDevice().getAddress(), socket);
                    BluetoothUtil.getInstance().setDeviceMac(socket.getRemoteDevice().getAddress());
                    BluetoothUtil.getInstance().setDeviceAddress(socket.getRemoteDevice().getAddress());
                    Log.e("zxy", "accept success !");
                    Message msg2 = new Message();
                    msg2.obj = "客户端已经连接上！可以发送信息。";
                    msg.what = ChatConstant.CONNECTED_CLIENT;
                    BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg2);

                    //启动接受数据
                    ReadThread mreadThread = new ReadThread(socket.getRemoteDevice().getAddress());
                    BluetoothUtil.getInstance().putReadThreadMap(socket.getRemoteDevice().getAddress(), mreadThread);
                    mreadThread.start();
                }else {
                    Message msg3 = new Message();
                    msg3.what = ChatConstant.CONNECT_CLIENT_REPEAT_ERROR;
                    BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg3);
                    BluetoothUtil.getInstance().socketClear(socket,socket.getRemoteDevice().getAddress());
                }
            }

        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
    }

    /**
     * 销毁连接
     */
    public void close() {
        if (mServerSocket == null) {
            return;
        }
        try {
            mServerSocket.close();
            mServerSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}