package rpc.transport.netty.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import rpc.loadBanlancer.LoadBalancer;
import rpc.loadBanlancer.RandomLoadBalancer;
import rpc.registry.NacosServiceDiscovery;
import rpc.registry.ServiceDiscovery;
import rpc.transport.RpcClient;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.registry.NacosServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.serializer.CommonSerializer;
import rpc.transport.netty.server.ChannelProvider;
import rpc.util.RpcMessageChecker;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: xu-rpc-framework-01
 * @description: 负责初始化客户端的netty服务，将rpcRequest发送到server！
 * @author: XuJY
 * @create: 2022-05-07 23:46
 **/
@Slf4j
public class NettyClient implements RpcClient {

//    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
    }

    public NettyClient() {
        this.serviceDiscovery = new NacosServiceDiscovery(new RandomLoadBalancer());
    }


//    static {
//        bootstrap = new Bootstrap();
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE,true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline()
////                                .addLast(new CommonEncoder(new JsonSerializer()))
//                                .addLast(new CommonEncoder(new KryoSerializer()))
//                                .addLast(new CommonDecoder())
//                                .addLast(new NettyClientHandler());
//                    }
//                });
//    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {

        if (serializer==null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        //线程安全的请求结果，用于存放resopnse的结果
        AtomicReference<Object> result = new AtomicReference<>(null);

        try {
            //从nacos注册中心获取目标服务端地址
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

//            ChannelFuture future = bootstrap.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort()).sync();//同步阻塞 等待连接建立完成！
//            log.info("客户端连接到服务器 {}:{}", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
//            Channel channel = future.channel();
            if (channel.isActive()){
                channel.writeAndFlush(rpcRequest).addListener(new GenericFutureListener<Future<? super Void>>() {
                    //addListener,异步操作，另外的线程通知writeAndFlush发送操作是否成功！
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()){
                            log.info("客户端成功发送消息：{}",rpcRequest.toString());
                        }else {
                            log.error("客户端发送消息时有错误发生：",future.cause());
                        }
                    }
                });
                channel.closeFuture().sync();//同步等待channel.close关闭操作！

                /*
                Channel上的AttributeMap就是大家共享的，每一个ChannelHandler都能获取到

                Channel类本身继承了AttributeMap，而AttributeMap它持有多个Attribute，这些attribute可以通过attributekey来访问。
                channel.attr(key).set(value)将属性值设置到channel中。

                channel 将 RpcRequest 对象写出，并且等待服务端返回的结果。注意这里的发送是非阻塞的，
                所以发送后会立刻返回，而无法得到结果。这里通过 AttributeKey 的方式阻塞获得返回结果：
                 */

                //这里获取的是NettyClientHandler最终处理结束的RpcResopnse，它被放进了ctx.channel.attributeMap中
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();//阻塞

                //检查request和response，进行校验
                RpcMessageChecker.check(rpcRequest,rpcResponse);
                log.debug("PpcRequest和RpcResponse校验成功");

                result.set(rpcResponse.getData());
            }
        } catch (InterruptedException e) {
            log.error("发送消息发生异常：",e);
        }

        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
