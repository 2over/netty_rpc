package adv;

import adv.business.User;
import serializable.msgpack.UserContact;

import java.util.Scanner;

public class BusiClient {

    public static void main(String[] args) throws InterruptedException, IllegalAccessException {
        NettyClient nettyClient = new NettyClient();
        new Thread(nettyClient).start();
        while (!nettyClient.isConnected()) {
            synchronized (nettyClient) {
                nettyClient.wait();
            }
        }

        System.out.println("网络通信已准备好，可以进行业务操作了。。。。。");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            if (msg == null) {
                break;
            } else if ("q".equals(msg.toLowerCase())) {
                nettyClient.close();
                scanner.close();
                while (nettyClient.isConnected()) {
                    synchronized (nettyClient) {
                        System.out.println("等待网络关闭完成....");
                        nettyClient.wait();
                    }
                }
                System.exit(1);
            } else if ("v".equals(msg.toLowerCase())) {
                User user = new User();
                user.setAge(19);
                String userName = "cover";
                user.setUserName(userName);
                user.setId("No:1");
                user.setUserContact(
                        new UserContact(userName + "@gmail.com", "133"));
                
                nettyClient.sendOneWay(user);
                
            } else {
                nettyClient.send(msg);
            }
        }
    }
}
