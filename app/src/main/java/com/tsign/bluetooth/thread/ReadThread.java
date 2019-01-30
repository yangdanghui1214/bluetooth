package com.tsign.bluetooth.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Message;

import com.tsign.bluetooth.common.ChatConstant;
import com.tsign.bluetooth.model.BluetoothMessage;
import com.tsign.bluetooth.utlis.BluetoothUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * 读取线程
 *
 * @author 13001
 */
public class ReadThread extends Thread {

    private String remoteDeviceAddress;
    InputStream mmInStream = null;

    public ReadThread(String remoteDeviceAddress) {
        this.remoteDeviceAddress = remoteDeviceAddress;
    }

    @Override
    public void run() {

        BluetoothSocket socket = BluetoothUtil.getInstance().getSocketMap().get(remoteDeviceAddress);
        try {
            mmInStream = socket.getInputStream();
        } catch (IOException e1) {
            BluetoothUtil.getInstance().closeConnection(remoteDeviceAddress);
            e1.printStackTrace();
        }
        while (true) {
            try {

                ObjectInputStream ois = new ObjectInputStream(mmInStream);
                BluetoothMessage message = (BluetoothMessage) ois.readObject();
                message.setSender(remoteDeviceAddress);
                message.setIsMe(0);

                Message msg = new Message();
                msg.obj = message;
                msg.what = message.getType() == -1 ? ChatConstant.CONNECT_CLIENT_REPEAT_ERROR : 1;
                BluetoothUtil.getInstance().getLinkDetectedHandler().sendMessage(msg);

            } catch (Exception e) {
                BluetoothUtil.getInstance().closeConnection(remoteDeviceAddress);
                break;
            }
        }
    }

    /**
     * 销毁连接
     */
    public void readClose() {
        if (mmInStream == null) {
            return;
        }
        try {
            mmInStream.close();
            mmInStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}