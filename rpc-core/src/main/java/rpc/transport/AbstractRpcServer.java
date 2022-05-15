package rpc.transport;

import lombok.extern.slf4j.Slf4j;
import rpc.annotation.Service;
import rpc.annotation.ServiceScan;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.provider.ServiceProvider;
import rpc.registry.ServiceRegistry;
import rpc.serializer.CommonSerializer;
import rpc.util.ReflectUtil;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @program: xu-rpc-framework-01
 * @description: 抽象的rpcServer，内部实现rpc的自动扫描服务功能：scanService
 * @author: XuJY
 * @create: 2022-05-15 16:01
 **/
@Slf4j
public abstract class AbstractRpcServer implements RpcServer{

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanService(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                log.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

    @Override
    public <T> void publishService(Object service, String serviceName) {

        //注册的时候首先把服务加入到本地的serviceProvider
        serviceProvider.addServiceProvider(service);
        //
        serviceRegistry.register(serviceName,new InetSocketAddress(host,port));
    }
}
