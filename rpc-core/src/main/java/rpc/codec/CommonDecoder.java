package rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.enums.PackageType;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.serializer.CommonSerializer;

import java.util.List;

/**
 * @program: xu-rpc-framework-01
 * @description:
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
 * @create: 2022-05-12 16:39
 **/
@Slf4j
public class CommonDecoder extends ReplayingDecoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int magic = in.readInt();
        if (magic != MAGIC_NUMBER){
            log.error("无法识别的协议包：magic不匹配：{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            log.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer==null){
            log.error("不识别的反序列化器：{}",serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        Object obj = serializer.deserialize(bytes, packageClass);

        out.add(obj);//这一层的处理结果加入到集合中，传递给后面的handler


    }
}
