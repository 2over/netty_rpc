package com.cover.rpcnetty.rpc.base.kryocodec;

import com.cover.rpcnetty.rpc.base.vo.MyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class KryoEncoder extends MessageToByteEncoder<MyMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyMessage message, ByteBuf out) throws Exception {
        KryoSerializer.serialize(message, out);
    }
}
