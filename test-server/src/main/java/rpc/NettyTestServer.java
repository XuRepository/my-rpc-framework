package rpc;

import rpc.netty.server.NettyServer;
import rpc.registry.DefaultServiceRegistry;

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
        DefaultServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);

        //rpc netty server
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(9000);

    }
}
