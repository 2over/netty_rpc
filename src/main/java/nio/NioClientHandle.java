package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandle implements Runnable {
    
    private String host;
    
    private int port;
    
    private volatile boolean started;
    
    private Selector selector;
    
    private SocketChannel socketChannel;
    
    public NioClientHandle(String ip, int port) {
        this.host = ip;
        this.port = port;
        
        try {
            // 创建选择器的实例
             selector = Selector.open();
             // 创建ServerSocketChannel的实例
             socketChannel = SocketChannel.open();
             // 设置通道为非阻塞模式
            socketChannel.configureBlocking(false);
            
            started = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        started = false;
    }
    @Override
    public void run() {
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        
        // 循环遍历selector
        while (started) {
            try {
                // 无论是否有读写事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                // 获取当前有哪些事件可以使用
                Set<SelectionKey> keys = selector.selectedKeys();
                // 转换为迭代器
                Iterator<SelectionKey> it = keys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    // 我们必须受限将处理过的SelectionKey从选定的集合中删除
                    // 如果我们没有删除处理过的键，那么它仍然会在主集合中以一个激活的键
                    // 出现，这会导致我们尝试再次处理它
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        // selector关闭后会自动释放里面管理的资源
        if (selector != null) {
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 具体的事件处理方法
     * @param key
     */
    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 获得关心当前事件的channel
            SocketChannel sc = (SocketChannel)key.channel();
            // 连接事件
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else {
                    System.exit(1);
                }
            }
            
            // 有数据可读事件
            if (key.isReadable()) {
                // 创建ByteBuffer,并开启一个1M的缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                // 读取数据码流 返回读取到的字节数
                int readBytes = sc.read(buffer);
                // 读取到字节 对字节进行编解码
                if (readBytes > 0) {
                    //将缓冲区当前的limit设置为position, position = 0;
                    // 用于后续对缓冲区的读取操作
                    buffer.flip();
                    // 根据缓冲区可读字节数创建字节数组
                    byte[] bytes = new byte[buffer.remaining()];
                    // 将缓冲区可读字节数组复制到新建的数组中
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    System.out.println("客户端收到消息:" + result);
                    
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                }

            }
        }
    }
    
    
    public void sendMsg(String msg) throws IOException {
        doWrite(socketChannel, msg);
        
    }

    private void doWrite(SocketChannel socketChannel, String request) throws IOException {
        // 将消息编码为字节数组
        byte[] bytes = request.getBytes();
        // 根据数组容量创建ByteBuffer
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        // 将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        // flip操作
        writeBuffer.flip();
        // 发送缓冲区的字节数组
        // 关心事件和读写网络不冲突
        socketChannel.write(writeBuffer);
        
    }
    
    private void doConnect() throws IOException {
        // 非阻塞的连接
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }
}
