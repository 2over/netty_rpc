package adv.kryocodec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import serializable.protogenesis.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class TestPerform {

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
//        ByteBuf sendBuf = Unpooled.buffer();
        ByteBuf sendBuf = Unpooled.buffer();
        long start1 = System.currentTimeMillis();
        KryoSerializer.serialize(info, sendBuf);
        long end1 = System.currentTimeMillis();
        System.out.println("time is : " + (end1 - start1));
        UserInfo deserialize = (UserInfo)KryoSerializer.deserialize(sendBuf);
        System.out.println("deserialize = " + deserialize.toString());
    }
}
