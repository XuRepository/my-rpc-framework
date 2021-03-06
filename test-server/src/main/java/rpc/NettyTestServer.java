package rpc;

import rpc.annotation.ServiceScan;
import rpc.serializer.HessianSerializer;
import rpc.serializer.JsonSerializer;
import rpc.transport.netty.server.NettyServer;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-12 21:18
 **/
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {

//        //创建服务端的服务实例，并且注册到注册中心去！
//        HelloServiceImpl helloService = new HelloServiceImpl();
//        //rpc netty server
//        NettyServer nettyServer = new NettyServer("127.0.0.1",9001);
//        nettyServer.setSerializer(new HessianSerializer());
////        nettyServer.publishService(helloService,HelloService.class);
        NettyServer server = new NettyServer("127.0.0.1", Integer.parseInt(args[0]));
        server.start();

    }
}
