package com.lullaby.cinema.sys.entity;

import java.io.Serializable;
import java.util.concurrent.LinkedTransferQueue;

/**
 * 影厅
 */
public class FilmHall implements Serializable {
    /**
     * 编号
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 总行数
     */
    private int totalRow;
    /**
     * 总列数
     */
    private int totalCol;
    /**
     * 座位
     */
    private Seat[][] seats;

    public FilmHall(String id, String name, int totalRow, int totalCol) {
        this.id = id;
        this.name = name;
        this.totalRow = totalRow;
        this.totalCol = totalCol;
        this.seats = new Seat[totalRow][totalCol];
        for (int i = 0; i < totalRow; i++) {
            for (int j = 0; j < totalCol; j++) {
                this.seats[i][j] = new Seat(i, j);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalCol() {
        return totalCol;
    }

    public void setTotalCol(int totalCol) {
        this.totalCol = totalCol;
    }

    /**
     * 展示座位信息
     */
    public void showSeats() {
        System.out.print(" ");
        for (int i = 0; i < totalCol; i++) {
            System.out.print(" " + i + " ");
        }
        System.out.println();
        for (int i = 0; i < totalRow; i++) {
            System.out.print(i);
            for (int j = 0; j < totalCol; j++) {
                Seat seat = seats[i][j];
                if (seat.getOwner() == null) {
                    System.out.print(seats[i][j]);
                } else {
                    System.err.print(seats[i][j]);
                }
                try {
                    Thread.sleep(20L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println();
        }
    }

    /**
     * 获取余票
     * @return 余票数量
     */
    public int getRestTicket() {
        int total = 0;
        for (Seat[] seatArr: seats) {
            for (Seat seat : seatArr) {
                if (seat.getOwner() == null)
                    total++;
            }
        }
        return total;
    }

    /**
     * 给定排号和列号的座位是否已经售卖
     * @param row 行号
     * @param col 列号
     * @return 已经售卖返回true，未售卖返回false
     */
    public boolean hasOwner(int row, int col) {
        return seats[row][col] != null;
    }

    /**
     * 给定排号和列号的座位设定用户
     * @param row 排号
     * @param col 列号
     * @param username 用户
     */
    public void setOwners(int row, int col, String username) {
        seats[row][col].setOwner(username);
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t\t" + totalRow  * totalCol;
    }
}
