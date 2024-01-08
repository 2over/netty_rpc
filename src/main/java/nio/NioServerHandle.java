package nio;

import constant.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServerHandle implements Runnable {
    
    private volatile boolean started;
    
    private ServerSocketChannel serverSocketChannel;
    
    private Selector selector;

    /**
     * 构造方法
     * @param port 指定要监听的端口号
     */
    public NioServerHandle(int port) {
        try {
            // 创建选择器的实例
            selector = Selector.open();
            // 创建ServerSocketChannel的实例
            serverSocketChannel = ServerSocketChannel.open();
            // 设置通道未非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 注册事件，表示关心客户端连接
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            started = true;
            System.out.println("服务器已启动, 端口号:" + port);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (started) {
            try {
                // 获取当前有哪些事件
                selector.select(1000);
                
                // 获取事件的集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 我们必须首先将处理过的SelectionKey从选定的键集合中删除
                    // 如果没有删除处理过的键，那么它仍然会在主机和中以一个激活的键
                    //出现，这会导致我们尝试再次处理它
                    iterator.remove();
                    handleInput(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理事件的发生
     * @param key
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 处理新接入的客户端的请求
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            // 接受连接
            SocketChannel sc = ssc.accept();
            System.out.println("=========建立连接=========");
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        }
        
        // 处理对端的发送的数据
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            // 创建ByteBuffer,开辟一个缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 从通道里面读取数据,然后写入buffer
            int readBytes = sc.read(buffer);
            if (readBytes > 0) {
                // 将缓冲区当前的limit设置为position, position =0
                // 用于后续对缓冲区的读取操作
                buffer.flip();
                // 将缓冲区的可读字节数创建字节数组
                byte[] bytes = new byte[buffer.remaining()];
                // 将缓冲区可读字节复制到新建的数组中
                buffer.get(bytes);;
                String message = new String(bytes, "UTF-8");
                System.out.println("服务器收到消息:" + message);

                String result = Constant.response(message);
                //发送应答消息
                doWrite(sc,result);
            } else if (readBytes < 0) {
                // 取消特定的注册关系
                key.cancel();
                // 关闭通道
                sc.close();
            }
        }
    }
    
    private void doWrite(SocketChannel sc, String response) throws IOException {
        byte[] bytes = response.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        sc.write(buffer);
    }
}
