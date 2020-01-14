package com.zhanghe.util;

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressUtil {

  static final Pattern P = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");

  static final String PREFIX = "/";

  public static InetSocketAddress getSocketAddress(String address) {
    if(address.startsWith(PREFIX)){
      address = address.substring(1,address.length()-1);
    }
    Matcher m = P.matcher(address);
    if (m.matches()) {
      String host = m.group(1);
      int port = Integer.parseInt(m.group(2));
      return new InetSocketAddress(host,port);
    }else{
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    getSocketAddress("/127.0.0.1:7777");
  }
}
