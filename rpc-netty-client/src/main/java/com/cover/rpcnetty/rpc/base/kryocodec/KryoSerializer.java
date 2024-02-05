package com.cover.rpcnetty.rpc.base.kryocodec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.ByteArrayOutputStream;

public class KryoSerializer {
    
    private static Kryo kryo = KryoFactory.createKryo();
    
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
        } catch (Exception e ){
            e.printStackTrace();
        }

        out.writeBytes(b);
    }
    
    public static Object deserialize(ByteBuf out) {
        if (out == null) {
            return null;
        }

        Input input = new Input(new ByteBufInputStream(out));
        return kryo.readClassAndObject(input);
    }
}
