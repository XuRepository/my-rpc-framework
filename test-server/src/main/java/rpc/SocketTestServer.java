package rpc;

import rpc.registry.DefaultServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.socket.server.SocketServer;


/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 15:47
 **/
public class SocketTestServer {
    public static void main(String[] args) {

        //服务端实例，等待客户端调用
        HelloServiceImpl helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();

        //向registry注册helloService
        serviceRegistry.register(helloService);

        //rpcServer
        SocketServer rpcServer = new SocketServer(serviceRegistry);

        rpcServer.start(9000);

    }
}
