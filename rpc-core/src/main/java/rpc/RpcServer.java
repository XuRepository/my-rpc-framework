package rpc;

import rpc.netty.serializer.CommonSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 22:20
 **/
public interface RpcServer {
    void start() throws InterruptedException;

    //用于向 Nacos 注册服务：
    <T> void publishService(Object service, Class<T> serviceClass);

    void setSerializer(CommonSerializer serializer);



}
