package rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import rpc.RpcClient;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.netty.codec.CommonDecoder;
import rpc.netty.codec.CommonEncoder;
import rpc.netty.serializer.JsonSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description: 负责初始化客户端的netty服务，将rpcRequest发送到server！
 * @author: XuJY
 * @create: 2022-05-07 23:46
 **/
@Slf4j
public class NettyClient implements RpcClient {

    private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new CommonDecoder())
                                .addLast(new NettyClientHandler());


                    }
                });
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();//同步阻塞 等待连接简历完成！
            log.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = future.channel();
            if (channel!=null){
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
                return rpcResponse;
            }
        } catch (InterruptedException e) {
            log.error("发送消息发生异常：",e);
        }

        return null;
    }
}
