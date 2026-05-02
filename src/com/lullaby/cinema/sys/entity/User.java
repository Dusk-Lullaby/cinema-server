package com.lullaby.cinema.sys.entity;
// 实体包的命名：entity 实体 model 数据模型
// bo(business object)业务对象 vo （view object）视图对象 dto（data transfer object） 数据传输对象
// pojo（plain ordinary java object）简单java对象 domain 领域模型

import java.io.Serializable;

/**
 * 用户类
 */
public class User implements Serializable {
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 安全码
     */
    private String securityCode;
    /**
     * 是否是管理员
     */
    private boolean manager;
    /**
     * 状态：1-正常 0-冻结
     */
    private int state = 1;

    public User(String username, String password, String securityCode) {
        this.username = username;
        this.password = password;
        this.securityCode = securityCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        String identity = manager ? "管理员" : "普通用户";
        String s = state == 1 ? "正常" : "冻结";
        return username + "\t" + identity + "\t" + s;
    }
}
