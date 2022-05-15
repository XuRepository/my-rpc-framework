package rpc;

import rpc.loadBanlancer.RoundRobinLoadBalancer;
import rpc.serializer.HessianSerializer;
import rpc.transport.netty.client.NettyClient;
import rpc.serializer.JsonSerializer;
import rpc.transport.RpcClient;
import rpc.transport.RpcClientProxy;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-12 21:18
 **/
public class NettyTestClient {
    public static void main(String[] args) {
        HelloObject helloObject = new HelloObject(2, "netty_rpc...");

        RpcClient client = new NettyClient(new RoundRobinLoadBalancer());
        client.setSerializer(new HessianSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        //获取到代理对象
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);

        String result = helloService.hello(helloObject);//代理方法，实际上调用的是rpcClientProxy中的invoke方法！
        System.out.println(result);


    }
}
