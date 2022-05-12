package rpc.netty.server;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import rpc.RequestHandler;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.registry.DefaultServiceRegistry;
import rpc.registry.ServiceRegistry;

/**
 * @program: xu-rpc-framework-01
 * @description: NettyServerhandler位于inbound责任链的最后 用于接收解码之后的 RpcRequest，并且执行调用，将调用结果返回封装成 RpcResponse 发送出去。
 * @author: XuJY
 * @create: 2022-05-12 16:39
 **/
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static ServiceRegistry serviceRegistry;
    private static RequestHandler requestHandler;

    //static代码块初始化static属性
    static {
        serviceRegistry = new DefaultServiceRegistry();
        requestHandler=new RequestHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        try {
            String interfaceName = msg.getInterfaceName();

            //在服务端注册中心中查找要调用的服务的实例。
            Object service = serviceRegistry.getService(interfaceName);

            //通过requestHandler，通过反射+本地服务实例+方法名+方法参数+参数类型---》调用方法并且返回结果！
            Object result = requestHandler.handle(msg, service);

            //把得到的结果封装为一个 RpcResponse实例，并且返回给客户端
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);//释放责任链中的buffer
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("进行服务端过程调用的过程中发生异常：");
        cause.printStackTrace();
        ctx.close();
    }
}
