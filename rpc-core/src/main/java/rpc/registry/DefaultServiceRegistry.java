package rpc.registry;

import rpc.enums.RpcError;
import rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 19:58
 **/
@Slf4j
public class DefaultServiceRegistry implements ServiceRegistry {

    //存放本地服务所实现的所有接口列表
    private final static Map<String,Object> serviceMap = new ConcurrentHashMap<String,Object>();

    //存放本地服务的service类名
    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void register(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) return;

        registeredService.add(serviceName);

        //看是否有接口，没有接口无法代理和RPC调用
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length==0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }

        //把服务的所有接口，都加入到serviceMap中去
        for (Class<?> i : interfaces){
            serviceMap.put(i.getCanonicalName(),service);
        }
        log.info("向接口： {}  注册服务：{}",interfaces,service);

    }

    /**
     *
     * @param serviceName 服务名称,这里应该是接口名称，就是客户端所调用的服务接口名
     * @return
     */
    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
