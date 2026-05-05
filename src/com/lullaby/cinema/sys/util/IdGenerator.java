package com.lullaby.cinema.sys.util;    //util包通常是工具类包

import java.util.Random;

/**
 * ID生成器
 */
public class IdGenerator {

    private static final char[] characters = {
            'A','B','C','D','E','F','G','H','I','J','K','L',
            'M','N','O','P','Q','R','S','T','U','V','W','X',
            'Y','Z','0','1','2','3','4','5','6','7','8','9'
    };

    private static Random RANDOM = new Random();

    /**
     * 根据给定的字符串长度生成ID
     * @param length 长度
     * @return ID
     */
    public static String generateId(int length) {
        StringBuilder sb = new StringBuilder("Lullaby_");
        for (int i = 0; i < length; i++) {
            sb.append(characters[RANDOM.nextInt(characters.length)]);
        }

        return sb.toString();
    }
}
