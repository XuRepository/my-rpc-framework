package rpc.provider;

/**
 * @program: xu-rpc-framework-01
 * @description: 注册中心
 * @author: XuJY
 * @create: 2022-05-07 19:54
 **/
public interface ServiceProvider {

    /**
     * 将一个服务注册进注册表
     * @param service 待注册的服务实体
     * @param <T> 服务实体类
     */
    <T> void addServiceProvider(T service);


    /**
     * 根据服务名称获取服务实体
     * @param serviceName 服务名称
     * @return 服务实体
     */
    Object getServiceProvider(String serviceName);
}
