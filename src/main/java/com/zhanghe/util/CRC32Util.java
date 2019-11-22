package com.zhanghe.util;

import java.util.zip.CRC32;

public class CRC32Util {

  public static long getCrcValue(byte[] bytes){
    CRC32 crc32 = new CRC32();
    crc32.update(bytes);
    return crc32.getValue();
  }

}
