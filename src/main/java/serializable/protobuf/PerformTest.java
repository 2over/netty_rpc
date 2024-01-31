package serializable.protobuf;


import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PerformTest {

    public static void main(String[] args) throws IOException {
        long start1 = System.currentTimeMillis();
        PersonOuterClass.Person person = PersonOuterClass.Person.newBuilder()
                .setId(1).setName("Cover").setEmail("xxxxx@gmail.com").build();

        byte[] result = person.toByteArray();
        long end1 = System.currentTimeMillis();
        System.out.println("Proto serialize length:" + result.length + ", time :" + (end1 - start1));
        
        // FAST JSON
        long start3 = System.currentTimeMillis();
        Person person2 = new Person();
        person2.id = 2;
        person2.name = "Cover";
        person2.email = "xxxxx@gmail.com";
        byte[] bytes = JSON.toJSONString(person2).getBytes();
        long end3 = System.currentTimeMillis();
        System.out.println("FASTJSON serialize length: " + bytes.length + ", time :" + (end3 - start3));
        
        // JDK
        long start2 = System.currentTimeMillis();
        Person person1 = new Person();
        person1.id = 2;
        person1.name = "Cover";
        person1.email = "xxxxx@gmail.com";

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(arrayOutputStream);
        out.writeObject(person1);
        long end2 = System.currentTimeMillis();
        System.out.println("JDK serialize length:" + arrayOutputStream.toByteArray().length + ", time :" + (end2 - start2));

    }
}
