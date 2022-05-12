package rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import rpc.RpcServer;
import rpc.netty.codec.CommonDecoder;
import rpc.netty.codec.CommonEncoder;
import rpc.netty.serializer.JsonSerializer;
import rpc.netty.serializer.KryoSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 23:47
 **/
@Slf4j
public class NettyServer implements RpcServer {
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //ChannelOption.SO_BACKLOG对应的是tcp/ip协议, listen函数 中的 backlog 参数，用来初始化服务端可连接队列。
                    .option(ChannelOption.SO_BACKLOG,256)
                    //该参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。
                    // 当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关。Nagle算法是将小的数据包组装为更大的帧然后进行发送，
                    // 而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效负载，但是却造成了延时，而
                    // 该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输，于TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送数据，适
                    // 用于文件传输
                    .childOption(ChannelOption.TCP_NODELAY,true)

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new CommonEncoder(new KryoSerializer()));
                            ch.pipeline().addLast(new CommonDecoder());
                            ch.pipeline().addLast(new NettyServerHandler());
                            //NettyServerHandler 和 NettyClientHandler 都分别位于服务器端和客户端责任链的尾部，
                            // 直接和 RpcServer 对象或 RpcClient 对象打交道，而无需关心字节序列的情况。
                            //NettyServerhandler 用于接收解码之后的 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
                        }
                    });
            //sync是为了阻塞住线程，等待连接建立完毕，建立连接是异步操作，不阻塞继续运行可能获得到空的channel后面
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //sync等待发生channel.close()方法，关闭channel结束，close是异步操作，所以这里需要阻塞住，不让线程往下运行，等待彻底结束之后优雅释放资源！
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器错误：",e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
