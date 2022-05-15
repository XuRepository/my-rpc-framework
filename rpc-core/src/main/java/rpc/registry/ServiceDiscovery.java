package rpc.registry;

import java.net.InetSocketAddress;

/**
 * @program: xu-rpc-framework-01
 * @description: 服务发现借口
 * @author: XuJY
 * @create: 2022-05-15 14:59
 **/
public interface ServiceDiscovery {
    /**
     * 根据服务名称查找服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
