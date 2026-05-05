package com.lullaby.cinema.sys.task;

import com.lullaby.cinema.sys.entity.*;
import com.lullaby.cinema.sys.entity.FilmHall;
import com.lullaby.cinema.sys.message.Message;
import com.lullaby.cinema.sys.util.DateUtil;
import com.lullaby.cinema.sys.util.FileUtil;
import com.lullaby.cinema.sys.util.IdGenerator;
import com.lullaby.cinema.sys.util.SocketUtil;

import java.net.Socket;
import java.util.*;
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
                case "getFilmPlan":
                    processGetFilmPlanList(msg);
                    break;
                case "getUserList": // 查看用户
                    processGetUserList();
                    break;
                case "frozenUser":  // 冻结用户
                    processFrozenUser(msg);
                    break;
                case "unfrozenUser":    // 解冻用户
                    processUnfrozenUser(msg);
                    break;
                case "getUnfrozenApplyList":    // 查看用户解冻申请
                    processGetUnfrozenApplyList();
                    break;
                case "getOrderList":    // 查看订单
                    processGetOrderList(msg);
                    break;
                case "getUserOrderList":    // 查看用户订单
                    processGetUserOrderList(msg);
                    break;
                case "updateOrder":    // 修改订单
                    processUpdateOrder(msg);
                    break;
                case "cancelOrder":    // 取消订单
                    processCancelOrder(msg);
                    break;
                case "auditOrder":    // 审核订单
                    processAuditOrder(msg);
                    break;
                case "orderSeatOnline": // 在线订座
                    processOrderSeatOnline(msg);
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
                    result.put("user", user);
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
        List<Film> films = FileUtil.readData(FileUtil.FILM_FILE);
        List<Film> result;
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
     * 处理添加播放计划请求
     * @param msg 信息
     */
    public void processAddFilmPlan(Message<FilmPlan> msg) {
        FilmPlan filmPlan = msg.getData();
        List<FilmPlan> filmPlans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        boolean conflict = filmPlans.stream().anyMatch(plan -> DateUtil.isConflictPlan(plan, filmPlan));
        if (conflict) { // 播放时间冲突
            SocketUtil.sendBack(client, -1);
        } else {
            filmPlans.add(filmPlan);
            boolean success = FileUtil.saveData(filmPlans, FileUtil.FILM_PLAN_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        }
    }

    /**
     * 处理删除播放计划请求
     * @param msg 信息
     */
    public void processDeleteFilmPlan(Message<String> msg) {
        String planId = msg.getData();
        List<FilmPlan> filmPlans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        int index = -1;
        for (int i = 0; i < filmPlans.size(); i++) {
            if (filmPlans.get(i).getId().equals(planId)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            SocketUtil.sendBack(client, -1);
        } else {
            filmPlans.remove(index);
            boolean success = FileUtil.saveData(filmPlans, FileUtil.FILM_PLAN_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        }
    }

    /**
     * 处理修改播放计划请求
     * @param msg 信息
     */
    public void processUpdateFilmPlan(Message<FilmPlan> msg) {
        FilmPlan filmPlan = msg.getData();
        List<FilmPlan> filmPlans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        int index = -1;
        for (int i = 0; i < filmPlans.size(); i++) {
            if (filmPlans.get(i).getId().equals(filmPlan.getId())) {
                index = i;
                break;
            }
        }
        if (index == -1) {  // 播放计划不存在
            SocketUtil.sendBack(client, -2);
        } else {
            FilmPlan removeFilmPlan = filmPlans.remove(index);    // 先将原来的播放计划移除
            filmPlan.setFilm(removeFilmPlan.getFilm());
            filmPlan.setFilmHall(removeFilmPlan.getFilmHall());
            // 然后再检测是否存在时间冲突
            boolean conflict = filmPlans.stream().anyMatch(plan -> DateUtil.isConflictPlan(plan, filmPlan));
            if (conflict) {
                SocketUtil.sendBack(client, -1);
            } else {
                filmPlans.add(filmPlan);
                boolean success = FileUtil.saveData(filmPlans, FileUtil.FILM_PLAN_FILE);
                SocketUtil.sendBack(client, success ? 1 : 0);
            }
        }
    }

    /**
     * 处理查看播放计划请求
     * @param msg 信息
     */
    public void processGetFilmPlanList(Message<String> msg) {
        String fileName = msg.getData();
        List<FilmPlan> filmPlans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        if (fileName == null || fileName.isEmpty()) {
            SocketUtil.sendBack(client, filmPlans);
        } else {
            List<FilmPlan> result = filmPlans.stream().filter(filePlan -> filePlan.getFilm().getName().contains(fileName) || fileName.contains(filePlan.getFilm().getName())).toList();
            SocketUtil.sendBack(client, result);
        }
    }

    /**
     * 处理查看用户请求
     */
    public void processGetUserList() {
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        SocketUtil.sendBack(client, users);
    }

    /**
     * 处理查看用户解冻申请请求
     */
    public void processGetUnfrozenApplyList() {
        List<UnfrozenApply> applies = FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
        SocketUtil.sendBack(client, applies);
    }

    /**
     * 处理冻结用户请求
     * @param msg 信息
     */
    public void processFrozenUser(Message<String> msg) {
        String username = msg.getData();
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                index = i;
                break;
            }
        }
        if (index == -1) {  // 账号不存在
            SocketUtil.sendBack(client, -1);
        } else {
            User user = users.get(index);
            if (user.getState() == 1) {
                user.setState(0);   // 设置账号为冻结状态
                boolean success = FileUtil.saveData(users, FileUtil.USER_FILE);
                SocketUtil.sendBack(client, success ? 1 : 0);
            } else {    // 已经被冻结
                SocketUtil.sendBack(client, -2);
            }
        }
    }

    /**
     * 处理解冻用户请求
     * @param msg 信息
     */
    public void processUnfrozenUser(Message<Map<String, Object>> msg) {
        Map<String, Object> data = msg.getData();
        List<UnfrozenApply> unfrozenApplies = FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
        int index = -1;
        for (int i = 0; i < unfrozenApplies.size(); i++) {
            if (unfrozenApplies.get(i).getId().equals(data.get("id"))) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            UnfrozenApply unfrozenApply = unfrozenApplies.get(index);
            int state = unfrozenApply.getState();
            if (state == 0) {   // 待处理
                unfrozenApply.setState((Integer) data.get("number"));
                List<User> users = FileUtil.readData(FileUtil.USER_FILE);
                for (User user : users) {
                    if (user.getUsername().equals(unfrozenApply.getUsername())) {
                        user.setState((int)data.get("number") == 1 ? 1 : 0);
                        break;
                    }
                }
                FileUtil.saveData(users, FileUtil.USER_FILE);
                boolean success = FileUtil.saveData(unfrozenApplies, FileUtil.UNFROZEN_APPLY_FILE);
                SocketUtil.sendBack(client, success ? 1 : 0);
            } else {
                SocketUtil.sendBack(client, -1);
            }
        } else {    // 解冻申请编号不存在
            SocketUtil.sendBack(client, -2);
        }
    }

    /**
     * 处理在线订座请求
     * @param msg 信息
     */
    public void processOrderSeatOnline(Message<Map<String, Object>> msg) {
        Map<String, Object> data = msg.getData();
        String planId = (String) data.get("planId");
        int row = (int) data.get("row");
        int col = (int) data.get("col");
        String username = (String) data.get("username");
        List<FilmPlan> filmPlans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        Optional<FilmPlan> optionalFilmPlan = filmPlans.stream().filter(filmPlan -> filmPlan.getId().equals(planId)).findFirst();
        if (optionalFilmPlan.isPresent()) {
            FilmPlan filmPlan = optionalFilmPlan.get();
            FilmHall filmHall = filmPlan.getFilmHall();
            filmHall.setOwners(row, col, username);
            List<FilmHall> filmHalls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
            Optional<FilmHall> optionalFilmHall = filmHalls.stream().filter(hall -> hall.getId().equals(filmHall.getId())).findFirst();
            if (optionalFilmHall.isPresent()) {
                FilmHall hall = optionalFilmHall.get();
                hall.setOwners(row, col, username);
                FileUtil.saveData(filmHalls, FileUtil.FILM_HALL_FILE);
            } else {
                SocketUtil.sendBack(client, -1);
                return;
            }
            boolean success = FileUtil.saveData(filmPlans, FileUtil.FILM_PLAN_FILE);
            String info = filmHall.getName() + " 第" + row + "排第" + col + "列";
            Order order = new Order(IdGenerator.generateId(10), filmPlan.getFilm().getName(), filmPlan.getBegin(), filmPlan.getEnd(), info, 1, username);
            List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
            orders.add(order);
            FileUtil.saveData(orders, FileUtil.ORDER_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        } else {
            SocketUtil.sendBack(client, -1);
        }
    }

    /**
     * 处理查看订单请求
     * @param msg 信息
     */
    public void processGetOrderList(Message msg) {
        Object data = msg.getData();
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        if (data == null) {
            SocketUtil.sendBack(client, orders);
        } else {
            int state = (int) data;
            List<Order> orderList = orders.stream().filter(order -> order.getState() == state).toList();
            SocketUtil.sendBack(client, orderList);
        }
    }

    /**
     * 处理查看用户订单请求
     * @param msg 信息
     */
    public void processGetUserOrderList(Message<String> msg) {
        String username = msg.getData();
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        List<Order> result = orders.stream().filter(order -> order.getOwner().equals(username)).toList();
        SocketUtil.sendBack(client, result);
    }

    /**
     * 处理更新订单请求
     * @param msg 信息
     */
    public void processUpdateOrder(Message msg) {

    }

    /**
     * 处理取消订单请求
     * @param msg 信息
     */
    public void processCancelOrder(Message<String> msg) {
        String orderId = msg.getData();
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        Optional<Order> optionalOrder = orders.stream().filter(order -> order.getId().equals(orderId)).findFirst();
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (order.getState() == 1) {
                order.setState(0);  // 更改订单状态为取消中
                boolean success = FileUtil.saveData(orders, FileUtil.ORDER_FILE);
                SocketUtil.sendBack(client, success ? 1 : 0);
            } else {    // 订单处于取消中，或已退订
                SocketUtil.sendBack(client, -2);
            }
        } else {
            SocketUtil.sendBack(client, -1);
        }
    }

    /**
     * 处理审核订单请求
     * @param msg 信息
     */
    public void processAuditOrder(Message<String> msg) {
        String orderId = msg.getData();
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        Optional<Order> optionalOrder = orders.stream().filter(order -> order.getId().equals(orderId)).findFirst();
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setState(2);
            boolean success = FileUtil.saveData(orders, FileUtil.ORDER_FILE);
            SocketUtil.sendBack(client, success ? 1 : 0);
        } else {
            SocketUtil.sendBack(client, -1);
        }
    }
}
