package adv.vo;

import java.util.HashMap;
import java.util.Map;

// 消息头
public class MsgHeader {
    
    // 消息体的MD5摘要
    private String md5;
    
    // 消息ID，因为是同步处理模式，不考虑应答消息需要填入请求消息ID
    private long msgId;
    
    // 消息类型
    private byte type;
    
    // 消息优先级
    private byte priority;
    
    private Map<String, Object> attachment = new HashMap<>();


    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "MsgHeader{" +
                "md5='" + md5 + '\'' +
                ", msgId=" + msgId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
