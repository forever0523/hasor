/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;

/**
 *
 * @version : 2014年11月17日
 * @author 赵永春 (zyc@hasor.net)
 */
public class NetworkUtils {
    protected static Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    private static void isPortAvailable(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**测试端口是否被占用*/
    public static boolean isPortAvailable(int port) {
        try {
            isPortAvailable("0.0.0.0", port);
            isPortAvailable(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**根据IP字节数据转换为int.*/
    public static byte[] ipStrToBytes(String ipData) {
        byte[] ipParts = new byte[4];
        String[] splitArrays = ipData.split("\\.");
        for (int i = 0; i < splitArrays.length; i++) {
            ipParts[i] = (byte) Integer.parseInt(splitArrays[i]);
        }
        return ipParts;
    }

    /**根据名字获取地址，local代表本机（如果本机有多网卡那么请明确指定ip）*/
    public static InetAddress finalBindAddress(String hostString) throws UnknownHostException {
        if ("local".equalsIgnoreCase(hostString)) {
            List<String> localAddr = localIpAddr();
            if (localAddr.isEmpty()) {
                return InetAddress.getLocalHost();
            } else {
                return InetAddress.getByName(localAddr.get(0));
            }
        } else {
            return InetAddress.getByName(hostString);
        }
    }

    /**根据掩码长度获取掩码字符串形式.*/
    public static String maskToStringByPrefixLength(int length) {
        return ipDataToString(ipDataByInt(maskByPrefixLength(length)));
    }

    /**根据掩码长度获取子网掩码值.*/
    public static int maskByPrefixLength(int length) {
        if (length > 32) {
            throw new IndexOutOfBoundsException("mask length max is 32.");
        }
        return -1 << (32 - length);
    }

    /**根据IP值分解IP为字节数组.*/
    public static byte[] ipDataByInt(int ipData) {
        byte ipParts[] = new byte[4];
        for (int i = 0; i < ipParts.length; i++) {
            int pos = ipParts.length - 1 - i;
            ipParts[pos] = (byte) (ipData >> (i * 8));
        }
        return ipParts;
    }

    /**根据IP字节数据转换为int.*/
    public static int ipDataByBytes(byte[] ipData) {
        int ipParts[] = new int[4];
        ipParts[0] = 0xFFFFFFFF & (ipData[0] << 24);
        ipParts[1] = 0x00FFFFFF & (ipData[1] << 16);
        ipParts[2] = 0x0000FFFF & (ipData[2] << 8);
        ipParts[3] = 0x000000FF & (ipData[3] << 0);
        int intIP = 0;
        for (int i = 0; i < ipParts.length; i++) {
            intIP = intIP | ipParts[i];
        }
        return intIP;
    }

    /**将分解的IP数据转换为字符串*/
    public static String ipDataToString(int ipData) {
        return ipDataToString(ipDataByInt(ipData));
    }

    /**将分解的IP数据转换为字符串*/
    public static String ipDataToString(byte[] ipData) {
        String result = "";
        result = result + tostr(ipData[0]);
        for (int i = 1; i < ipData.length; i++) {
            result = result + "." + tostr(ipData[i]);
        }
        return result;
    }

    private static String tostr(byte byteData) {
        return "" + ((byteData < 0) ? 256 + byteData : byteData);
    }

    /** 获取本机地址 */
    public static List<String> localIpAddr() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
                Enumeration ipAddrEnum = ni.getInetAddresses();
                while (ipAddrEnum.hasMoreElements()) {
                    InetAddress addr = (InetAddress) ipAddrEnum.nextElement();
                    if (addr.isLoopbackAddress()) {
                        continue;
                    }
                    String ip = addr.getHostAddress();
                    if (ip.contains(":")) {
                        //skip the IPv6 addr
                        continue;
                    }
                    logger.debug("Interface: " + ni.getName() + ", IP: " + ip);
                    ipList.add(ip);
                }
            }
            Collections.sort(ipList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to get local ip list. " + e.getMessage());
            throw new RuntimeException("Failed to get local ip list");
        }
        return ipList;
    }

    /** 获取本机IP */
    public static void localIpAddr(Set<String> set) {
        List<String> addrList = localIpAddr();
        set.clear();
        set.addAll(addrList);
    }
}