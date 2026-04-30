package com.lullaby.cinema.sys.util;

import com.lullaby.cinema.sys.message.Message;

import java.io.*;
import java.net.Socket;

/**
 * 套接字工具类
 */
public class SocketUtil {

    /**
     * 读取客户端发送的消息
     * @param client 客户端套接字
     * @return 消息
     * @param <T> 因为不知道客户端发送的信息携带的是什么数据类型，因此使用泛型
     */
    public static <T> Message<T> receiveMessage(Socket client) {
        try {
            InputStream inputStream = client.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Message<T> message = (Message<T>) objectInputStream.readObject();
            client.shutdownInput();
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向客户端返回处理结果
     * @param client 客户端套接字
     * @param data 处理结果
     * @param <V> 因为不知道处理结果是什么数据类型，因此使用泛型
     */
    public static <V> void sendBack(Socket client, V data) {
        try {
            OutputStream outputStream = client.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            client.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
