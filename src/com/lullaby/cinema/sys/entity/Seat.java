package com.lullaby.cinema.sys.entity;

import java.io.Serializable;

/**
 * 座位
 */
public class Seat implements Serializable {
    /**
     * 排号
     */
    private int row;
    /**
     * 列号
     */
    private int col;
    /**
     * 所属者
     */
    private String owner;

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return " # ";
    }
}
