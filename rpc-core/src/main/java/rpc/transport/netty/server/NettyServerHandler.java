package rpc.transport.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import rpc.handler.RequestHandler;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.util.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * @program: xu-rpc-framework-01
 * @description: NettyServerhandler位于inbound责任链的最后 用于接收解码之后的 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 * @author: XuJY
 * @create: 2022-05-12 16:39
 **/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

//    private static ServiceProvider serviceProvider;
    private static final RequestHandler requestHandler;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    //static代码块初始化static属性
    static {
        requestHandler=new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(THREAD_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(()->{
            try {
                log.info("服务器接收到请求: {}", msg);
                //通过requestHandler，通过反射+本地服务实例+方法名+方法参数+参数类型---》调用方法并且返回结果！
                Object result = requestHandler.handle(msg);

                //把得到的结果封装为一个 RpcResponse实例，并且返回给客户端
                ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result,msg.getRequestId()));
                log.info("服务端---->客户端: {}", msg);
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(msg);//释放责任链中的buffer
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("进行服务端过程调用的过程中发生异常：");
        cause.printStackTrace();
        ctx.close();
    }
}
