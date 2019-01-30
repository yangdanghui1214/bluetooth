package com.tsign.bluetooth.model;


/**
 * 蓝牙数据库实体类
 *
 * @author 13001
 */
public class BluetoothModel {
    public final static String TYPE = "type";

    private String mac;
    private String name;
    private String type;

    public BluetoothModel(String mac, String name) {
        this.mac = mac;
        this.name = name;
    }

    public BluetoothModel(String mac, String name, String type) {
        this.mac = mac;
        this.name = name;
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
