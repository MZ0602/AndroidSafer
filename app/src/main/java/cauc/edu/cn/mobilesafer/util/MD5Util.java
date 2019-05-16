package cauc.edu.cn.mobilesafer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
* FileName: MD5Util <br>
* Description: 对字符串进行MD5加密的工具类 <br>
* Author: 沈滨伟-13042299081 <br>
* Date: 2019/4/17 11:39
*/

public class MD5Util {

    /**
     * 对指定的字符串进行MD5加密处理
     * @param password   待加密的原始密码值
     * @return MD5加盐加密后的密码值
     */
    public static String encodePassword(String password) {
        try {
            // 此处采取密码加盐加密处理,确保密码更加安全，因为MD5可将通过暴力建立表格与密文对比，查出密码
            // 加盐是将明文与随机数拼接后再加密存储，同时将该随机数保存至数据库，便于后面密码校验进行身份验证
            // 但此处的加盐并没有使用动态的随机值，而是直接粗暴地使用固定值"mobilesafer"，且未保存至数据库
            password = password + "mobilesafer";
            // 获取MessageDigest实例，并指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 将需要加密的字符串转换成byte数组后进行随机哈希过程
            byte[] byteArray = password.getBytes();
            // 信息摘要对象对字节数组进行摘要,得到摘要字节数组
            byte[] md5Byte = digest.digest(byteArray);
            StringBuffer buffer = new StringBuffer();
            // 循环遍历byte类型数组，让其生成32位字符串
            for (byte b : md5Byte) {
                int i = b & 0xff;
                String str = Integer.toHexString(i);
                if (str.length() < 2) {
                    str = "0" + str;
                }
                buffer.append(str);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
