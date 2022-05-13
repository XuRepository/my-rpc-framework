package rpc.registry;

import java.net.InetSocketAddress;

/**
 * @program: xu-rpc-framework-01
 * @description: 服务注册中心通用接口
 * @author: XuJY
 * @create: 2022-05-13 12:08
 **/
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表,register 方法将服务的名称和地址注册进服务注册中心
     *
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据服务名称查找服务实体,lookupService 方法则是根据服务名称从注册中心获取到一个服务提供者的地址。
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);

}
