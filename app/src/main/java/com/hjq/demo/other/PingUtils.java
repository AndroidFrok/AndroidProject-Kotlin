package com.hjq.demo.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

public class PingUtils {

    static public String ping(String host) {
        StringBuilder result = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "4", host);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            result.append("Error: ").append(e.getMessage());
        }
        String res = result.toString();
        Timber.d("ping结果 " + res);
        return res;
    }
//-----------------------------------
//        ©著作权归作者所有：来自51CTO博客作者mob64ca12f7e7cf的原创作品，请联系作者获取转载授权，否则将追究法律责任
//            Android执行ping
//    https://blog.51cto.com/u_16213463/11616675

    /**
     * 计算CRC16校验码
     *
     * @param bytes 需要计算的字节数组
     */
    /*public static String getCRC(byte[] bytes) {

        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {

            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {

                if ((CRC & 0x00000001) != 0) {

                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {

                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }*/

}
