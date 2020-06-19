package com.interfacetest.util;

import java.util.ArrayList;
import java.util.List;
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
    protected static String getRandomUUID() {
        UUID uuid= UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 随机生成一个字符串
     * @return a-z
     */
    protected static String getRandomStr() {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<10;i++){
            int number=random.nextInt(62);
            sb.append(randomBase.charAt(number));
        }
        return sb.toString();
    }

    protected static List<String> getRandomStrList() {
        List<String> list = new ArrayList<>();

        for (int j=0;j<5;j++){
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<10;i++){
                int number=random.nextInt(62);
                sb.append(randomBase.charAt(number));
            }
            list.add(sb.toString());
        }
        return list;
    }

    protected static String getRandomChar() {
        StringBuilder sb=new StringBuilder();
        int number=random.nextInt(62);
        sb.append(randomBase.charAt(number));
        return sb.toString();
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
        StringBuilder sBuffer = new StringBuilder();
        for (int i = 0; i < arrLength; i++) {
            sBuffer.append("\"").append(paramStr).append("\"").append(",");
        }
        int leng = sBuffer.toString().length();
        return sBuffer.toString().substring(0, leng-1);
    }

    public static int getRandomInt() {
        return random.nextInt(10000000);
    }

    public static byte getRandomByte(){
        byte[] arr=new byte[2];
        random.nextBytes(arr);
        return arr[0];
    }

    public static short getRandomShort() {
        int n=random.nextInt(200000);
        return (short) ((short) (new Random().nextInt(n)) & 0x7FFF);
    }

    public static long getRandomLong() {
        return random.nextLong();
    }

    public static Float getRandomFloat() {
        return random.nextFloat();
    }

    public static Double getRandomDouble() {
        return random.nextDouble();
    }

    public static void main(String[] args) {
        System.out.println(RandomUtil.getRandomStrList());
    }
}

