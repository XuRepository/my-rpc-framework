package rpc;

import rpc.socket.client.SocketClient;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 15:48
 **/
public class SocketTestClient{

    //客户端调用远程方法的时候，需要通过代理类来调用
    public static void main(String[] args) {
        RpcClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        //客户端调用的方法在这个对象内！
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        HelloObject obj = new HelloObject(11, "rpc");
        String res = helloService.hello(obj);
        System.out.println(res);


    }
}
