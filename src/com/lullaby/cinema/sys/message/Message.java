package com.lullaby.cinema.sys.message;

import java.io.Serializable;

/**
 * 信息
 * @param <T>
 */
public class Message<T> implements Serializable {
    /**
     * 动作
     */
    private String action;
    /**
     * 携带数据
     */
    private T data;

    public Message(String action, T data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return action + "=>" + data;
    }
}
