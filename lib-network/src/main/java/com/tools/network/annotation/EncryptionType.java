package com.tools.network.annotation;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2017/9/28 17:55
 * <p/>
 * Description: 加密类型
 * RSA_Base64   - RSA 后 Base64
 * RSA_BCD      - RSA 后 BCD
 * MD5          - MD5（转大写）
 * MD5_2        - 2次 MD5 (不转大写)
 * Base64       - Base64
 */
public interface EncryptionType {
    int DEFAULT     = 0xEE;
    int RSA_BASE_64 = 0xE0;
    int RSA_BCD     = 0xE1;
    int MD5         = 0xE3;
    int MD5_2       = 0xE2;
    int BASE_64     = 0xE4;
}
