package com.lullaby.cinema.sys.task;

import com.lullaby.cinema.sys.entity.User;
import com.lullaby.cinema.sys.message.Message;
import com.lullaby.cinema.sys.util.FileUtil;
import com.lullaby.cinema.sys.util.SocketUtil;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理任务
 */
public class MessageProcessTask implements Runnable{

    private Socket client;

    public MessageProcessTask(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        Message msg = SocketUtil.receiveMessage(client);
        System.out.println(msg);
        if (msg != null) {
            switch (msg.getAction()) {
                case "register":    // 注册
                    processRegister(msg);
                    break;
            }
        }
    }

    /**
     * 处理注册请求
     * @param msg 传输的信息
     */
    public void processRegister(Message<User> msg) {
        User registerUser = msg.getData();
        List<User> storageUsers = FileUtil.readData(FileUtil.USER_FILE);
        if (storageUsers.isEmpty()) {   // 如果存档信息为空，表示没有任何用户注册
            User user = new User("admin", "123456", "lullaby");
            user.setManager(true);  // 设置为管理员
            storageUsers.add(user);
        }
        boolean exists = storageUsers.stream().anyMatch(user -> user.getUsername().equals(registerUser.getUsername()));
        if (exists) {   // 存在账号已被注册
            SocketUtil.sendBack(client, -1);
        } else {
            storageUsers.add(registerUser);
            boolean success = FileUtil.saveData(storageUsers, FileUtil.USER_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        }
    }
}
