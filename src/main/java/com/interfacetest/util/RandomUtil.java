package com.interfacetest.util;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {
    //定义字母和数字
    public static String randomBase = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomNumberBase = "0123456789";
    public static Random random = new Random();

    /**
     * 随机生成一个UUID
     * @return UUID
     */
    protected static String getRandomHanZi() {
        UUID uuid= UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 随机生成一个字母
     * @return a-z
     */
    protected static char getRandomStr() {
        Random ran = new Random();
        return (char) randomBase.charAt(ran.nextInt(62));
    }

    public static String getRandom(int length, boolean onlyNumber) {
        String base;
        if (onlyNumber) {
            base = randomNumberBase;
        } else {
            base = randomBase;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char chr;
            do {
                int number = random.nextInt(base.length());
                chr = base.charAt(number);
            } while (i==0&&chr=='0') ;//第一个字符不能为0,

            sb.append(chr);
        }
        return sb.toString();
    }


    public static String getRandomArr(int arrLength, int length, boolean flag) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < arrLength; i++) {
            sBuffer.append(getRandom(length, flag)).append(",");
        }
        int leng = sBuffer.toString().length();
        return sBuffer.toString().substring(0, leng-1);
    }

    /**
     * 生成定长的字符串数组
     * @param arrLength  数组长度
     * @param paramStr
     * @return
     */
    public static String generateStrArr(int arrLength, String paramStr) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < arrLength; i++) {
            sBuffer.append("\"").append(paramStr).append("\"").append(",");
        }
        int leng = sBuffer.toString().length();
        return sBuffer.toString().substring(0, leng-1);
    }

}

