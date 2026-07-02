package com.hjq.demo.other // 包声明：定义当前文件所属的包路径

import java.io.File // 导入文件类，用于文件操作
import java.io.FileInputStream // 导入文件输入流类，用于读取文件
import java.io.InputStream // 导入输入流接口，用于数据输入
import java.security.MessageDigest // 导入消息摘要类，用于MD5加密
import kotlin.also // 导入also扩展函数，用于链式调用
import kotlin.io.use // 导入use扩展函数，用于自动关闭资源
import kotlin.text.equals // 导入字符串比较函数
import kotlin.text.toByteArray // 导入字符串转字节数组函数
import kotlin.text.uppercase // 导入字符串转大写函数

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2026/01/XX
 *    desc   : MD5 加密工具类
 */
object Md5Util { // 定义MD5工具类对象（单例模式）

    /**
     * 获取字符串的 MD5 值
     *
     * @param text 待加密的字符串
     * @return MD5 值（32位小写）
     */
    fun md5(text: String): String { // 定义获取字符串MD5值的函数
        return md5(text.toByteArray()) // 将字符串转换为字节数组后调用MD5加密
    }

    /**
     * 获取字符串的 MD5 值（大写）
     *
     * @param text 待加密的字符串
     * @return MD5 值（32位大写）
     */
    fun md5UpperCase(text: String): String { // 定义获取字符串MD5值（大写）的函数
        return md5(text).uppercase() // 获取MD5值后转换为大写
    }

    /**
     * 获取字节数组的 MD5 值
     *
     * @param bytes 待加密的字节数组
     * @return MD5 值（32位小写）
     */
    fun md5(bytes: ByteArray): String { // 定义获取字节数组MD5值的函数
        return try { // 尝试执行MD5加密操作
            val digest = MessageDigest.getInstance("MD5") // 获取MD5消息摘要实例
            val result = digest.digest(bytes) // 对字节数组进行MD5加密，得到加密结果
            bytesToHex(result) // 将加密结果转换为十六进制字符串
        } catch (e: Exception) { // 捕获异常
            e.printStackTrace() // 打印异常堆栈信息
            "" // 返回空字符串
        }
    }

    /**
     * 获取文件的 MD5 值
     *
     * @param file 待加密的文件
     * @return MD5 值（32位小写），如果文件不存在或读取失败则返回空字符串
     */
    fun md5(file: File): String { // 定义获取文件MD5值的函数
        if (!file.exists() || !file.isFile) { // 判断文件是否存在且是文件（非目录）
            return "" // 如果文件不存在或不是文件，返回空字符串
        }
        return try { // 尝试读取文件并计算MD5值
            FileInputStream(file).use { inputStream -> // 创建文件输入流，使用use函数自动关闭
                md5(inputStream) // 调用输入流MD5计算方法
            }
        } catch (e: Exception) { // 捕获异常
            e.printStackTrace() // 打印异常堆栈信息
            "" // 返回空字符串
        }
    }

    /**
     * 获取输入流的 MD5 值
     *
     * @param inputStream 输入流
     * @return MD5 值（32位小写）
     */
    fun md5(inputStream: InputStream): String { // 定义获取输入流MD5值的函数
        return try { // 尝试从输入流读取数据并计算MD5值
            val digest = MessageDigest.getInstance("MD5") // 获取MD5消息摘要实例
            val buffer = ByteArray(8192) // 创建8KB的缓冲区用于读取数据
            var len: Int // 声明变量用于存储读取的字节数
            while (inputStream.read(buffer).also { len = it } != -1) { // 循环读取输入流数据，直到读取完毕（返回-1）
                digest.update(buffer, 0, len) // 更新消息摘要，将读取的数据添加到MD5计算中
            }
            bytesToHex(digest.digest()) // 完成MD5计算并转换为十六进制字符串
        } catch (e: Exception) { // 捕获异常
            e.printStackTrace() // 打印异常堆栈信息
            "" // 返回空字符串
        } finally { // 无论是否发生异常都执行
            try { // 尝试关闭输入流
                inputStream.close() // 关闭输入流
            } catch (e: Exception) { // 捕获关闭流时的异常
                e.printStackTrace() // 打印异常堆栈信息
            }
        }
    }

    /**
     * 验证字符串的 MD5 值
     *
     * @param text 原始字符串
     * @param md5Value 期望的 MD5 值
     * @return 是否匹配
     */
    fun verify(text: String, md5Value: String): Boolean { // 定义验证字符串MD5值的函数
        return md5(text).equals(md5Value, ignoreCase = true) // 计算字符串的MD5值并与期望值比较（忽略大小写）
    }

    /**
     * 验证文件的 MD5 值
     *
     * @param file 文件
     * @param md5Value 期望的 MD5 值
     * @return 是否匹配
     */
    fun verify(file: File, md5Value: String): Boolean { // 定义验证文件MD5值的函数
        return md5(file).equals(md5Value, ignoreCase = true) // 计算文件的MD5值并与期望值比较（忽略大小写）
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    private fun bytesToHex(bytes: ByteArray): String { // 定义私有函数：将字节数组转换为十六进制字符串
        val hexString = kotlin.text.StringBuilder() // 创建字符串构建器用于拼接十六进制字符串
        for (byte in bytes) { // 遍历字节数组中的每个字节
            val hex = Integer.toHexString(0xff and byte.toInt()) // 将字节转换为十六进制字符串（0xff用于确保无符号转换）
            if (hex.length == 1) { // 如果十六进制字符串只有一位
                hexString.append('0') // 在前面补0，保证两位十六进制数
            }
            hexString.append(hex) // 将十六进制字符串追加到结果中
        }
        return hexString.toString() // 返回完整的十六进制字符串
    }
}
