package adv.kryocodec;

import adv.vo.EncryptUtils;
import adv.vo.MakeMsgId;
import adv.vo.MsgHeader;
import adv.vo.MyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

public class TestKryoCodeC {
    
    
    public MyMessage getMessage(int j) {
//        String content = "abcdefg-----------AAAAAAAAAA" + j;
        String content =  "abcdefg-----------AAAAAA" + j;
        MyMessage myMessage = new MyMessage();
        MsgHeader msgHeader = new MsgHeader();
        
        msgHeader.setMsgId(MakeMsgId.getID());
        msgHeader.setType((byte) 1);
        msgHeader.setPriority((byte) 7);
        msgHeader.setMd5(EncryptUtils.encryptObj(content));
        Map<String, Object> attachment = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            attachment.put("city --> " + i, "cover " + i);
        }
        
        msgHeader.setAttachment(attachment);
        myMessage.setMsgHeader(msgHeader);
        myMessage.setBody(content);
        
        return myMessage;
    }

    public static void main(String[] args) {
        TestKryoCodeC testC = new TestKryoCodeC();

        for (int i = 0; i < 5; i++) {
            ByteBuf sendBuf = Unpooled.buffer();
            MyMessage message = testC.getMessage(i);
            System.out.println("Encode:" + message);
            KryoSerializer.serialize(message, sendBuf);
            MyMessage decodeMsg = (MyMessage)KryoSerializer.deserialize(sendBuf);
            System.out.println("Decode:" + decodeMsg);
            System.out.println("-----------------------------------------------");
        }
    }
}
