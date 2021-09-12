package com.lazydog.chatchacha.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {
    public void startServer() throws IOException {
        //1 创建selector
        Selector selector = Selector.open();
        // 2 创建channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // channel 板顶通道
        serverSocketChannel.bind(new InetSocketAddress(8000));
        serverSocketChannel.configureBlocking(false);
        //channel 注册到SELECTOR
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器已经启动");
        //循环，等连接
        for (;;){
            //获取channel数量
            int readChannel = selector.select();

            if (readChannel==0){
                continue;
            }
            //获取可用的channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey next = iterator.next();
                iterator.remove();
                //就绪状态，调用对应的方法
                //如果accept
                if (next.isAcceptable()){
                    acceptOperator(serverSocketChannel,selector);
                }
                //如果可读
                if (next.isReadable()){
                    readOperator(selector,next);
                }


            }
        }
    }

    private void readOperator(Selector selector, SelectionKey next) throws IOException {
        //get already channel from SelectionKey
       SocketChannel channel = (SocketChannel) next.channel();
        //create buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //loop get client msg
        int length = channel.read(byteBuffer);
        String msg="";
        if(length>0){
            byteBuffer.flip();
            msg +=Charset.forName("utf-8").decode(byteBuffer);
        }
        //channel registry selector
        channel.register(selector,SelectionKey.OP_READ);
        //broadcast to other clients
        if (msg.length()>0){
            System.out.println(msg);
//            castOtherClient(msg,selector,channel);

        }
    }

    private void castOtherClient(String msg, Selector selector, SocketChannel channel) throws IOException {
        //获取所有已经接入客户端
        Set<SelectionKey> keys = selector.keys();
        //循环所有的channel广播消息
        for (SelectionKey key : keys) {
            Channel tarChannel = key.channel();
            if (tarChannel instanceof SocketChannel&&tarChannel!=channel){
                ((SocketChannel) tarChannel).write(Charset.forName("utf-8").encode(msg));


            }
        }
    }

    private void acceptOperator(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //创建socketchannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //socketchannel=>false block
        socketChannel.configureBlocking(false);
        //channel register selector
        socketChannel.register(selector,SelectionKey.OP_READ);

        //reply client
        socketChannel.write(Charset.forName("utf-8").encode("link start 进入聊天室"));

    }


    public static void main(String[] args) {
        try {
            new ChatServer().startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
