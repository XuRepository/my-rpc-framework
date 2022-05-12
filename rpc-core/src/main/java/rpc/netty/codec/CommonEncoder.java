package rpc.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcRequest;
import rpc.enums.PackageType;
import rpc.enums.SerialzerCode;
import rpc.netty.serializer.CommonSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description: 自定义协议
 *
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 *
 * @author: XuJY
 * @create: 2022-05-12 16:38
 **/
@Slf4j
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

        //1，魔数
        out.writeInt(MAGIC_NUMBER);

        //2，包类型
        if (msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }

        //3,序列化类型
        out.writeInt(serializer.getCode());

        //4，数据长度
        byte[] bytes = serializer.serialize(msg);//序列化待编码的消息
        int length = bytes.length;
        out.writeInt(length);

        //5 消息本身！
        out.writeBytes(bytes);


    }
}
