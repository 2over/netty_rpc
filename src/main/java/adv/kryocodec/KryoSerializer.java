package adv.kryocodec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer {
    
    private static Kryo kryo = KryoFactory.createKryo();
    
    // 序列化
    public static void serialize(Object object, ByteBuf out) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        
        output.flush();
        output.close();

        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        out.writeBytes(b);
    }
    
    // 序列化为一个字节数组，主要用在消息摘要上
    public static byte[] obj2Bytes(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output, object);
        output.flush();
        output.close();

        byte[] b = baos.toByteArray();
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return b;
    }
    
    
    public static Object deserialize(ByteBuf out) {
        if (out == null) {
            return null;
        }
        
        Input input = new Input(new ByteBufInputStream(out));
        return kryo.readClassAndObject(input);
    }
}
