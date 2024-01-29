package adv.vo;

public class MyMessage {
    
    private MsgHeader msgHeader;
    
    private Object body;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }


    @Override
    public String toString() {
        return "MyMessage{" +
                "msgHeader=" + msgHeader +
                ", body=" + body +
                '}';
    }
}
