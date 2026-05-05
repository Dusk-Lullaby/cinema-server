package com.lullaby.cinema.sys.entity;

import com.lullaby.cinema.sys.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单
 */
public class Order implements Serializable {
    /**
     * 编号
     */
    private String id;
    /**
     * 影片名称
     */
    private String fileName;
    /**
     * 开始时间
     */
    private Date begin;
    /**
     * 结束时间
     */
    private Date end;
    /**
     * 座位信息
     */
    private String seatInfo;
    /**
     * 状态 0-退订中 1-正常 2-已经退订
     */
    private int state;
    /**
     * 拥有者
     */
    private String owner;

    public Order(String id, String fileName, Date begin, Date end, String seatInfo, int state, String owner) {
        this.id = id;
        this.fileName = fileName;
        this.begin = begin;
        this.end = end;
        this.seatInfo = seatInfo;
        this.state = state;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getSeatInfo() {
        return seatInfo;
    }

    public void setSeatInfo(String seatInfo) {
        this.seatInfo = seatInfo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return id + "\t" + fileName + "\t" + DateUtil.date2str(begin) + "\t" + DateUtil.date2str(end) + "\t" +
                seatInfo + "\t" + owner;
    }
}
