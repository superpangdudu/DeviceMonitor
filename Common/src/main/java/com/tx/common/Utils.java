package com.tx.common;

import java.io.*;

/**
 * Created by Administrator on 2019/9/19.
 */
public class Utils {

    public static <T extends Serializable> byte[] serialize(T object) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(object);

            byte[] data = byteArrayOutputStream.toByteArray();
            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T deserialize(byte[] data) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

            Object object = objectInputStream.readObject();

            return (T) object;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param content
     * @param pos from 0 to 7
     * @return
     */
    public static byte getBit(byte content, int pos) {
        return (byte) ((content & (1 << pos)) >> pos);
    }

    public static byte[] intToByte(int number) {
        byte[] data = new byte[4];

        data[0] = (byte) ((number >> 24) & 0xFF);
        data[1] = (byte) ((number >> 16) & 0xFF);
        data[2] = (byte) ((number >> 8) & 0xFF);
        data[3] = (byte) (number & 0xFF);

        return data;
    }

    public static short bytesToShort(byte high, byte low) {
        short value = 0;
        value |= low & 0xFF;
        value |= (high << 8) & 0xFF00;

        return value;
    }

    public static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        int value = 0;
        value = value | (b4 & 0xFF);
        value = value | ((b3 & 0xFF) << 8);
        value = value | ((b2 & 0xFF) << 16);
        value = value | ((b1 & 0xFF) << 24);

        return value;
    }

    public static int hexByteToInt(byte[] data) {
        String str = new String(data);
        return Integer.parseInt(str, 16);
    }

    public static String hexToString(byte[] data) {
        char[] hexCode = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(hexCode[(b >> 4) & 0xF]);
            sb.append(hexCode[(b & 0xF)]);
        }
        return sb.toString();
    }

    public static String hexToString(byte[] data, int offset) {
        char[] hexCode = "0123456789ABCDEF".toCharArray();

        StringBuilder sb = new StringBuilder((data.length - offset) * 2);
        for (int n = offset; n < data.length; n++) {
            byte b = data[n];

            sb.append(hexCode[(b >> 4) & 0xF]);
            sb.append(hexCode[(b & 0xF)]);
        }

        return sb.toString();
    }

    public static String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < data.length; n++) {
            sb.append("0x");

            String str = Integer.toHexString(data[n] & 0xFF);
            if (str.length() < 2)
                sb.append("0");
            sb.append(str);

            if (n < data.length - 1)
                sb.append(", ");
        }

        return sb.toString();
    }

    public static byte[] hexString2Bytes(String hexStr) {
        if (hexStr.startsWith("0x") || hexStr.startsWith("0X"))
            hexStr = hexStr.substring(2);

        if ((hexStr.length() % 2) != 0)
            hexStr = '0' + hexStr;

        int length = hexStr.length() / 2;
        byte[] value = new byte[length];

        for (int n = 0; n < length; n++) {
            value[n] = (byte) Integer.parseInt("" + hexStr.charAt(n * 2) + hexStr.charAt(n * 2 + 1), 16);
        }

        return value;
    }

    //===================================================================================
    public static void main(String[] args) {
        String hexStr = "0xF0102";
        byte[] value = hexString2Bytes(hexStr);

        hexStr = "04 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 11 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 8A 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 00 20 00 85 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FE 7F FE 7F FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 7F FE 7F FE 7F FE 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00";
        hexStr = hexStr.replaceAll(" ", "");

        value = hexString2Bytes(hexStr);

        int n = 0;
    }
}
