package serializable.protogenesis;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;

@Message
public class UserInfo implements Serializable {
    /**
     * 默认序列号
     */
    private static final long serialVersionUID = 7627113094707995002L;
    
    private String userName;
    
    private int userID;

    public String getUserName() {
        return userName;
    }

    public UserInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public int getUserID() {
        return userID;
    }

    public UserInfo setUserID(int userID) {
        this.userID = userID;
        return this;
    }
    
    // 自行序列化
    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // userName转换为字节数组value
        byte[] value = this.userName.getBytes();
        // 写入字节数组value的长度
        buffer.putInt(value.length);
        // 写入字节数组value的值
        buffer.put(value);
        // 写入userID的值
        buffer.putInt(this.userID);
        // 准备读取buffer中的数据
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        // buffer中的数据写入字节数组并作为结果返回
        buffer.get(result);
        return result;
        
    }
    
    // 自行序列化方法2
    public byte[] codeC(ByteBuffer buffer) {
        buffer.clear();
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userID);
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userID=" + userID +
                '}';
    }
}
