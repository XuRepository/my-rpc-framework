package rpc;

import rpc.netty.serializer.KryoSerializer;
import rpc.provider.ServiceProviderImpl;
import rpc.provider.ServiceProvider;
import rpc.socket.server.SocketServer;


/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 15:47
 **/
public class SocketTestServer {
    public static void main(String[] args) {
//
//        //服务端实例，等待客户端调用
//        HelloServiceImpl helloService = new HelloServiceImpl();
//
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
//
//        //向registry注册helloService
//        serviceProvider.addServiceProvider(helloService);
//
//        //rpcServer
//        SocketServer rpcServer = new SocketServer("127.0.0.1",9000);
//
//        rpcServer.start();

        HelloService helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9100);
        socketServer.setSerializer(new KryoSerializer());
        socketServer.publishService(helloService, HelloService.class);

    }
}
