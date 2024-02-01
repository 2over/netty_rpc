package remote.vo;

import java.io.Serializable;

// 注册中心注册服务的实体类
public class RegisterServiceVo implements Serializable {
    private static final long serialVersionUID = -1297236403535813628L;
    
    // 服务提供者的ip地址
    private String host;
    
    // 服务提供者的端口
    private int port;
    
    public RegisterServiceVo(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
}
