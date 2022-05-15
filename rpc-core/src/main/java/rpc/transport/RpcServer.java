package rpc.transport;

import rpc.annotation.Service;
import rpc.enums.SerialzerCode;
import rpc.serializer.CommonSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 22:20
 **/
public interface RpcServer {

    int DEFAULT_SERIALIZER = SerialzerCode.KRYO.getCode();//默认的序列化 KRYO

    void start() throws InterruptedException;

    //用于向 Nacos 注册服务：
    <T> void publishService(Object service, String serviceName);

    void setSerializer(CommonSerializer serializer);



}
