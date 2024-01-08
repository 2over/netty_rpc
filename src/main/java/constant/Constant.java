package constant;

import java.util.Date;

public class Constant {
    
    public static final Integer DEFUALT_PORT = 12345;
    
    public static final String DEFAULT_SERVER_IP= "127.0.0.1";
    
    // 根据输入信息拼接出一个应答信息
    public static String response(String msg) {
        return "Hello, " + msg + ", Now is" + new Date(System.currentTimeMillis()).toString(); 
    }
}
