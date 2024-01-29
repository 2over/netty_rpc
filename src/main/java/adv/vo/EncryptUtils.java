package adv.vo;

import adv.kryocodec.KryoSerializer;

import java.security.MessageDigest;

public class EncryptUtils {
    
    private static String EncryptStr(String strSrc, String encName) {
        MessageDigest md  = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest());
        } catch (Exception e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        
        return strDes;
    }

    /**
     * MD5 摘要
     * @param str 需要被摘要的字符串
     * @return 对字符串str进行MD5摘要后,将摘要字符串返回
     */
    public static String EncryptByMD5(String str) {
        return EncryptStr(str, "MD5");
    }

    /**
     * SHA1摘要
     * @param str 需要被摘要的字符串
     * @return 对字符串str进行SHA-1摘要后，将摘要字符串返回
     */
    public static String EncryptBySHA1(String str) {
        return EncryptStr(str, "SHA-1");
    }
    /**
     * SHA256摘要
     * @param str 需要被摘要的字符串
     * @return 对字符串str进行SHA-256摘要后，将摘要字符串返回
     */
    public static String EncryptBySHA256(String str) {
        return EncryptStr(str, "SHA-256");
    }

    /**
     * 字节转十六进制，结果以字符串形式呈现
     */

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;

        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
            
        }
        
        return des;
    }
    
    /**
     * 对字符串进行MD5加盐摘要 先将str进行一次MD5摘要，摘要后再取摘要后的字符串的
     * 1,3,5个字符追加到摘要串，再拿这个摘要串再次进行摘要
     */
    private static String encrypt(String str) {
        String encryptStr = EncryptByMD5(str);
        if (encryptStr != null) {
            encryptStr = encryptStr + encryptStr.charAt(0) + encryptStr.charAt(2) + encryptStr.charAt(4);
            encryptStr = EncryptByMD5(encryptStr);
        }
        return encryptStr;
    }
    
    public static String encryptObj(Object o) {
        return encrypt(bytes2Hex(KryoSerializer.obj2Bytes(o)));
    }
    
}
