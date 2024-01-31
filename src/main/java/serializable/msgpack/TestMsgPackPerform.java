package serializable.msgpack;

import org.msgpack.MessagePack;
import serializable.protogenesis.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestMsgPackPerform {

    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.setUserID(100).setUserName("Hello World");
        long start = System.currentTimeMillis();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();

        byte[] b = bos.toByteArray();
        long end = System.currentTimeMillis();
        System.out.println("The JDK serializable length is :" + b.length + ", time is :" + (end - start));

        
        MessagePack messagePack = new MessagePack();
        byte[] bytes = messagePack.write(info);
        System.out.println("The MsgPack serializable length is :" + bytes.length);
        UserInfo read = messagePack.read(bytes, UserInfo.class);
    
        System.out.println("read = " + read);
    }
}
