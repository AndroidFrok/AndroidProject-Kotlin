package com.hjq.demo.util

import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncDecUtil {

    companion object {


        /** AES解密函数
         * https://www.ssleye.com/ssltool/aes_cipher.html
         */
        fun aesDecrypt(ciphertext: String): String {
            val aesKey = "08f46334bfa3f71783890006655e4dc6";
            val aesIv = "4a1586659a32ba62";

//            val cipherArray = ciphertext.toByteArray(Charsets.UTF_8);
            var decode = android.util.Base64.decode(ciphertext, 0);
            val key = aesKey.toByteArray(Charsets.UTF_8) // AES-128需要16字节密钥
            val iv = aesIv.toByteArray(Charsets.UTF_8) // AES的块大小是16字节，所以IV也是16字节
            // 创建Cipher实例，指定AES算法和CBC模式以及PKCS5填充方式
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            // 根据提供的密钥和初始化向量（IV）创建SecretKeySpec和IvParameterSpec对象
            val secretKeySpec = SecretKeySpec(key, "AES")
            val ivParameterSpec = IvParameterSpec(iv)
            // 初始化Cipher为解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
            // 检查密文长度是否是块大小的整数倍
            /*if (cipherArray.size % cipher.blockSize != 0) {
                throw IllegalArgumentException( "密文长度不是块大小的整数倍 " +cipherArray.size+".."+cipher.blockSize)
                return "";
            }*/

            // 执行解密操作并返回明文字节数组
            val decrypted = cipher.doFinal(decode);
            val decd = String(decrypted);
            Timber.d("解密后 $decd");
//            val s1 = android.util.Base64.encodeToString(decrypted, 0);
//            Timber.d("解密后 $s1");
            return decd;
        }
//
    }
}