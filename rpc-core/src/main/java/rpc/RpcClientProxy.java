package rpc;


import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @program: xu-rpc-framework-01
 * @description: 客户动态代理,代理客户都安，当客户端调用方法的时候，通过代理类，实现将客户的调用请求发送到服务端，在服务端方法调用完毕之后再返回。
 * 这样客户端看起来+好像再自己调用本地方法一样。
 * @author: XuJY
 * @create: 2022-05-07 14:09
 **/

public class RpcClientProxy implements InvocationHandler {

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //建造者模式，lombok
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();

        return client.sendRequest(rpcRequest);
    }
}
