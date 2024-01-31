package serializable.protogenesis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class PerformTestUserInfo {

    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.setUserID(100).setUserName("Hello World");
        int loop = 1000000;

        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(info);
            os.flush();
            os.close();
            byte[] bytes = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The JDK serializable cost time is :" + (endTime - startTime) + "ms");
        System.out.println("---------------------------------");
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            byte[] bytes = info.codeC(buffer);
        }
        endTime = System.currentTimeMillis();

        System.out.println("The byte array serializable cost time is :" + (endTime - startTime) + "ms");
        

    }
}
