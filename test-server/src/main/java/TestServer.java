import register.DefaultServiceRegistry;
import register.ServiceRegistry;
import server.RpcServer;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 15:47
 **/
public class TestServer {
    public static void main(String[] args) {

        //服务端实例，等待客户端调用
        HelloServiceImpl helloService = new HelloServiceImpl();

        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();

        //向registry注册helloService
        serviceRegistry.register(helloService);

        //rpcServer
        RpcServer rpcServer = new RpcServer(serviceRegistry);

        rpcServer.start(9000);

    }
}
