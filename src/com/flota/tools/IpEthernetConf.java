package com.flota.tools;

import com.pos.device.net.eth.EthernetInfo;
import com.pos.device.net.eth.EthernetManager;

public class IpEthernetConf {

    private static IpEthernetConf ethernetConf;

    private IpEthernetConf() {

    }

    public static IpEthernetConf getInstance() {
        if (ethernetConf == null) {
            ethernetConf = new IpEthernetConf();
        }
        return ethernetConf;
    }

    public static void setConnectionIP(EthernetInfo.NetType netType, String ip, String prefix, String dns, String gateway) {
        EthernetInfo info = new EthernetInfo();
        info.setConnectionType(netType);
        if (netType.equals(EthernetInfo.NetType.STATIC_IP)) {
            // Instancio la IP
            EthernetInfo.StaticIP infoIP = new EthernetInfo.StaticIP(ip, dns, gateway, prefix);
            //La seteo en INFOF
            info.setStaticIpConfigs(infoIP);
            //La seteo en manager
        }
        EthernetManager manager = EthernetManager.getInstance();
        manager.setEtherentConfigs(info);
    }
}
