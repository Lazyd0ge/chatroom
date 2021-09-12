package com.lazydog.chatchacha.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ChatClient {
    public void startClient() throws IOException {
        //连接服务端
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
        //向服务端发送信息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String msg=scanner.nextLine();
            if (msg.length()>0){
                socketChannel.write(Charset.forName("utf-8").encode(msg));
            }
        }

    }

    public static void main(String[] args) {
        try {
            new ChatClient().startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
