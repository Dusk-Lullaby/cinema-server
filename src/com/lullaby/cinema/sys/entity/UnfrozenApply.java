package com.lullaby.cinema.sys.entity;

import java.io.Serializable;

/**
 * 解冻申请
 */
public class UnfrozenApply implements Serializable {
    /**
     * 编号
     */
    private String id;
    /**
     * 账号
     */
    private String username;
    /**
     * 原因
     */
    private String reason;
    /**
     * 处理状态 0-待处理 1-已通过 2-已驳回
     */
    private int state;

    public UnfrozenApply(String id, String username, String reason) {
        this.id = id;
        this.username = username;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        String stateStr = state == 0 ? "待处理" : state == 1 ? "已处理" : "已驳回";
        return id + "\t" + username + "\t" + reason + "\t" + stateStr;
    }
}
