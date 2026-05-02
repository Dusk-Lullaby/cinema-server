package com.lullaby.cinema.sys.starter;

import com.lullaby.cinema.sys.task.MessageProcessTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 影院服务器
 */
public class CinemaServer {

    private ServerSocket serverSocket;

    public CinemaServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    /**
     * 服务器启动
     */
    public void start() {
        while (true) {
            try {
                // 等待客户端连接
                Socket client = serverSocket.accept();
                new Thread(new MessageProcessTask(client)).start();
                // 信息处理
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            CinemaServer server = new CinemaServer(8888);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
