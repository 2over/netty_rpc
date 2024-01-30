package serializable.protogenesis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class TestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.setUserID(100).setUserName("Hello World");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();

        byte[] b = bos.toByteArray();
        System.out.println("The JDK serializable length is :" + b.length);
        bos.close();
        System.out.println("-------------------------------------------");
        System.out.println("the byte array serializable length is :" + info.codeC().length);

    }
}
