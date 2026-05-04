package com.lullaby.cinema.sys.task;

import com.lullaby.cinema.sys.entity.*;
import com.lullaby.cinema.sys.entity.FilmHall;
import com.lullaby.cinema.sys.message.Message;
import com.lullaby.cinema.sys.util.FileUtil;
import com.lullaby.cinema.sys.util.SocketUtil;

import java.io.File;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                case "login":   // 登录
                    processLogin(msg);
                    break;
                case "getPasswordBack":
                    processGetPasswordBack(msg);
                    break;
                case "unfrozenApply" :  // 处理解冻申请
                    processUnfrozenApply(msg);
                    break;
                case "addFilm":
                    processAddFilm(msg);
                    break;
                case "updateFilm":
                    processUpdateFilm(msg);
                    break;
                case "deleteFilm":
                    processDeleteFilm(msg);
                    break;
                case "getFilmList":
                    processGetFilmList(msg);
                    break;
                case "addFilmHall":
                    processAddFilmHall(msg);
                    break;
                case "updateFilmHall":
                    processUpdateFilmHall(msg);
                    break;
                case "deleteFilmHall":
                    processDeleteFilmHall(msg);
                    break;
                case "getFilmHallList":
                    processGetFilmHallList();
                    break;
                case "addFilmPlan":
                    processAddFilmPlan(msg);
                    break;
                case "updateFilmPlan":
                    processUpdateFilmPlan(msg);
                    break;
                case "deleteFilmPlan":
                    processDeleteFilmPlan(msg);
                    break;
                case "getFilmPlanList":
                    processGetFilmPlanList(msg);
                    break;
            }
        }
    }

    /**
     * 处理第一次存储
     * @return 存储的用户信息
     */
    private static List<User> processFirstStorage() {
        List<User> storageUsers = FileUtil.readData(FileUtil.USER_FILE);
        if (storageUsers.isEmpty()) {   // 如果存档信息为空，表示没有任何用户注册
            User user = new User("admin", "123456", "lullaby");
            user.setManager(true);  // 设置为管理员
            storageUsers.add(user);
            FileUtil.saveData(storageUsers, FileUtil.USER_FILE);
        }
        return storageUsers;
    }

    /**
     * 处理注册请求
     * @param msg 传输的信息
     */
    public void processRegister(Message<User> msg) {
        User registerUser = msg.getData();
        List<User> storageUsers = processFirstStorage();
        boolean exists = storageUsers.stream().anyMatch(user -> user.getUsername().equals(registerUser.getUsername()));
        if (exists) {   // 存在账号已被注册
            SocketUtil.sendBack(client, -1);
        } else {
            storageUsers.add(registerUser);
            boolean success = FileUtil.saveData(storageUsers, FileUtil.USER_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        }
    }

    /**
     * 处理登录请求
     * @param msg 传输的信息
     */
    public void processLogin(Message<User> msg) {
        User loginUser = msg.getData();
        List<User> storageUsers = processFirstStorage();
        Map<String, Object> result = new HashMap<>();
        // 查找用户名与登录用户的用户名匹配的账号
        Optional<User> optionalUser = storageUsers.stream().filter(user -> user.getUsername().equals(loginUser.getUsername())).findFirst();
        if (optionalUser.isPresent()) { // 如果有找到
            User user = optionalUser.get(); // 取出
            int state = user.getState();    // 获取账号状态
            if (state == 1) {   // 账号正常
                if (loginUser.getPassword().equals(user.getPassword())) {   // 密码匹配
                    result.put("process", 1);
                    result.put("manager", user.isManager());
                } else {    // 密码不匹配
                    result.put("process", 0);
                }
            } else {    // 账号被冻结
                result.put("process", -2);
            }
        } else {    // 账号不存在
            result.put("process", -1);
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理找回密码请求
     * @param msg 信息
     */
    public void processGetPasswordBack(Message<User> msg) {
        User getBackUser = msg.getData();
        List<User> storageUsers = processFirstStorage();
        String result = null;
        Optional<User> optionalUser = storageUsers.stream().filter(user -> user.getUsername().equals(getBackUser.getUsername())).findFirst();
        if (optionalUser.isPresent()) { // 如果Optional种有存储数据
            User user = optionalUser.get();
            if (user.getSecurityCode().equals(getBackUser.getSecurityCode())) { // 安全码匹配
                result = user.getPassword();
            }
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理解冻申请请求
     * @param msg 信息
     */
    public void processUnfrozenApply(Message<UnfrozenApply> msg) {
        UnfrozenApply unfrozenApply = msg.getData();
        List<User> storageUsers = processFirstStorage();
        Optional<User> optionalUnfrozenApply = storageUsers.stream().filter(user -> user.getUsername().equals(unfrozenApply.getUsername())).findFirst();
        int result;
        if (optionalUnfrozenApply.isPresent()) {
            User user = optionalUnfrozenApply.get();
            if (user.getState() == 1) { // 账号正常
                result = -1;
            } else {    // 账号被冻结
                List<UnfrozenApply> unfrozenApplies = FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
                unfrozenApplies.add(unfrozenApply);
                boolean success = FileUtil.saveData(unfrozenApplies, FileUtil.UNFROZEN_APPLY_FILE);
                result = success ? 1 : 0;
            }
        } else {    // 账号不存在，也就不存在解冻申请
            result = -1;
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理添加影片请求
     * @param msg 信息
     */
    public void processAddFilm(Message<FilmHall> msg) {
        FilmHall addFilm = msg.getData();
        List<FilmHall> films = FileUtil.readData(FileUtil.FILM_FILE);
        films.add(addFilm);
        boolean success = FileUtil.saveData(films, FileUtil.FILM_FILE);
        SocketUtil.sendBack(client, success ? 1 : 0);
    }

    /**
     * 处理更改影片请求
     * @param msg 信息
     */
    public void processUpdateFilm(Message<FilmHall> msg) {
        FilmHall updateFilm = msg.getData();
        List<FilmHall> films = FileUtil.readData(FileUtil.FILM_FILE);
        int index = -1;
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(updateFilm.getId())) {
                index = i;
                break;
            }
        }
        int result;
        if (index == -1) {  // 说明修改的影片信息不存在
            result = -1;
        } else {
            films.set(index, updateFilm);
            boolean success = FileUtil.saveData(films, FileUtil.FILM_FILE);
            result = success ? 1 : 0;
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理删除影片请求
     * @param msg 信息
     */
    public void processDeleteFilm(Message<String> msg) {
        String id = msg.getData();
        List<FilmHall> films = FileUtil.readData(FileUtil.FILM_FILE);
        int index = -1;
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(id)) {
                index = i;
                break;
            }
        }
        int result;
        if (index == -1) {  // 说明删除的影片信息不存在
            result = -1;
        } else {
            films.remove(index);
            boolean success = FileUtil.saveData(films, FileUtil.FILM_FILE);
            result = success ? 1 : 0;
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理查看影片请求
     * @param msg 信息
     */
    public void processGetFilmList(Message<String> msg) {
        String name = msg.getData();
        List<FilmHall> films = FileUtil.readData(FileUtil.FILM_FILE);
        List<FilmHall> result;
        if (name == null || name.isEmpty()) {
            result = films;
        } else {
            result = films.stream().filter(film -> film.getName().contains(name) || name.contains(film.getName())).collect(Collectors.toList());
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     *  处理增加影厅请求
     * @param msg 信息
     */
    public void processAddFilmHall(Message<FilmHall> msg) {
        FilmHall filmHall = msg.getData();
        List<FilmHall> filmHalls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        filmHalls.add(filmHall);
        boolean success = FileUtil.saveData(filmHalls, FileUtil.FILM_HALL_FILE);
        SocketUtil.sendBack(client, success ? 1 : 0);
    }

    /**
     * 处理修改影厅请求
     * @param msg 信息
     */
    public void processUpdateFilmHall(Message<FilmHall> msg) {
        FilmHall updateFilmHall = msg.getData();
        List<FilmHall> filmHalls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        int index = -1;
        for (int i = 0; i < filmHalls.size(); i++) {
            if (filmHalls.get(i).getId().equals(updateFilmHall.getId())) {
                index = i;
                break;
            }
        }
        int result;
        if (index == -1) {  // 说明修改的影厅信息不存在
            result = -1;
        } else {
            filmHalls.set(index, updateFilmHall);
            boolean success = FileUtil.saveData(filmHalls, FileUtil.FILM_HALL_FILE);
            result = success ? 1 : 0;
        }
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理删除影厅请求
     * @param msg 信息
     */
    public void processDeleteFilmHall(Message<String> msg) {
        String id = msg.getData();
        List<FilmHall> filmHalls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        int index = -1;
        for (int i = 0; i < filmHalls.size(); i++) {
            if (filmHalls.get(i).getId().equals(id)) {
                index = i;
                break;
            }
        }
        int result;
        if (index == -1) {  // 说明删除的影厅信息不存在
            result = -1;
        } else {
            filmHalls.remove(index);
            boolean success = FileUtil.saveData(filmHalls, FileUtil.FILM_HALL_FILE);
            result = success ? 1 : 0;
        }
        SocketUtil.sendBack(client, result);

    }

    /**
     * 处理查看影厅请求
     */
    public void processGetFilmHallList() {
        List<FilmHall> filmHalls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        SocketUtil.sendBack(client, filmHalls);
    }

    /**
     * 添加播放计划
     * @param msg 信息
     */
    public void processAddFilmPlan(Message<FilmPlan> msg) {

    }

    /**
     * 删除播放计划
     * @param msg 信息
     */
    public void processDeleteFilmPlan(Message<FilmPlan> msg) {

    }

    /**
     * 修改播放计划
     * @param msg 信息
     */
    public void processUpdateFilmPlan(Message<FilmPlan> msg) {

    }

    /**
     * 查看播放计划
     * @param msg 信息
     */
    public void processGetFilmPlanList(Message<FilmPlan> msg) {

    }
}
