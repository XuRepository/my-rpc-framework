package rpc.netty.client;

import com.sun.media.jfxmedia.logging.Logger;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcResponse;

/**
 * @program: xu-rpc-framework-01
 * @description: NettyClienthandler位于inbound责任链的最后 用于接收解码之后的 RpcResponse。
 * @author: XuJY
 * @create: 2022-05-12 16:41
 **/
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    //这里只需要处理收到的消息，即 RpcResponse 对象，由于前面已经有解码器解码了，这里就直接将返回的结果放入 ctx 中即可。
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            log.info("客户端收到消息：{}",msg);

            //Channel上的AttributeMap就是大家共享的，每一个ChannelHandler都能获取到
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }
}
