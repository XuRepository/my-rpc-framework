package rpc.handler;

import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import rpc.provider.ServiceProvider;
import rpc.provider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @program: xu-rpc-framework-01
 * @description: 进行本地的服务方法的调用，通过反射拿到本地所需要执行的method，执行并且返回
 * @author: XuJY
 * @create: 2022-05-07 21:19
 **/
@Slf4j
public class RequestHandler {

    /*
    本地服务所实现的所有接口列表,是static修饰，为类所有，所有对象共享
    private final static Map<String,Object> serviceMap = new ConcurrentHashMap<String,Object>();
    private final static Set<String> registeredService = ConcurrentHashMap.newKeySet();
     */
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest) {

        Object result = null;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        //执行本地方法并且返回结果
        try {
            result = invokeTargetMethod(rpcRequest, service);
            log.info("服务：{}  成功调用方法：{},  调用方法返回的结果：{}", rpcRequest.getInterfaceName(),rpcRequest.getMethodName(),result);

        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("调用或发送时有错误发生：", e);

        }

        return result;

    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method = null;
        try {
            //解析请求，找到需要调用的服务端方法本身
            //通过requestHandler，通过反射+本地服务实例+方法名+方法参数+参数类型---》调用方法并且返回结果！
            //因为object对象的service实例无法调用相应方法，所以采取反射调用！
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND,rpcRequest.getRequestId());
        }

        return method.invoke(service,rpcRequest.getParameters());
    }
}
