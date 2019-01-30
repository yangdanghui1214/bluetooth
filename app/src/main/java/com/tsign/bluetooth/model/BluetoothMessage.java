package com.tsign.bluetooth.model;

import java.io.Serializable;

/**
 *  传输实体类
 *
 * @author lenew
 * @date 2016/7/7 0007
 */
public class BluetoothMessage implements Serializable {


    public interface MessageType{
        int TYPE_IMAGE = 1;
        int TYPE_VIBRATOR = 2;
        int TYPE_TEXT = 3;
    }

    private int id;
    //发送者 以address标记
    private String sender;
    private String senderNick;
    /**
     * 对比的图片
     */
    private byte[] senderIcon;
    /**
     * 被对比的图片
     */
    private byte[] contrastIcon;
    private int isMe;
    private String content;
    private String name;
    private int type = MessageType.TYPE_TEXT;
    private String dateTime;
     private String receiver;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public byte[] getSenderIcon() {
        return senderIcon;
    }

    public void setSenderIcon(byte[] senderIcon) {
        this.senderIcon = senderIcon;
    }

    public byte[] getContrastIcon() {
        return contrastIcon;
    }

    public void setContrastIcon(byte[] contrastIcon) {
        this.contrastIcon = contrastIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsMe() {
        return isMe;
    }

    public void setIsMe(int isMe) {
        this.isMe = isMe;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
