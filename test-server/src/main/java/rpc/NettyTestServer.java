package rpc;

import rpc.netty.serializer.KryoSerializer;
import rpc.netty.server.NettyServer;
import rpc.provider.ServiceProviderImpl;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-12 21:18
 **/
public class NettyTestServer {
    public static void main(String[] args) {

        //创建服务端的服务实例，并且注册到注册中心去！
        HelloServiceImpl helloService = new HelloServiceImpl();
        //rpc netty server
        NettyServer nettyServer = new NettyServer("127.0.0.1",9001);
        nettyServer.setSerializer(new KryoSerializer());
        nettyServer.publishService(helloService,HelloService.class);

    }
}
