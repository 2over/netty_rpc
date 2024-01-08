package nio;

import constant.Constant;

public class NioServer {

    private static NioServerHandle nioServerHandle;
    public static void main(String[] args) {
        nioServerHandle = new NioServerHandle(Constant.DEFUALT_PORT);
        new Thread(nioServerHandle, "Server").start();
    }
}
