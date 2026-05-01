package com.lullaby.cinema.sys.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtil {
    /**
     * 用户存档文件
     */
    public static final String USER_FILE = "data/user.obj";
    /**
     * 影片存档文件
     */
    public static final String FILM_FILE = "data/film.obj";
    /**
     * 播放计划存档文件
     */
    public static final String FILM_PLAN_FILE = "data/filPlan.obj";
    /**
     * 影厅存档文件
     */
    public static final String FILM_HALL_FILE = "data/filmHall.obj";
    /**
     * 订单存档文件
     */
    public static final String ORDER_FILE = "data.order.obj";
    /**
     * 解冻申请存档文件
     */
    public static final String UNFROZEN_APPLY_FILE = "data/unfrozenApply.obj";

    /**
     * 保存给定的列表数据
     * @param dataList 列表数据
     * @param path 路径
     * @return 是否保存成功
     * @param <T> 因为不清楚列表的具体数据类型，因此使用泛型
     */
    public static <T> boolean saveData(List<T> dataList, String path) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (!parent.exists())
            parent.mkdirs();
        try (OutputStream outputStream = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)){
            objectOutputStream.writeObject(dataList);
            objectOutputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从给定路径的文件中读取数据
     * @param path 路径
     * @return 文件数据
     * @param <T> 因为不清楚文件中存储的数据类型，因此使用泛型
     */
    public static <T> List<T> readData(String path) {
        try (InputStream inputStream = new FileInputStream(path);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)){
            return (List<T>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
