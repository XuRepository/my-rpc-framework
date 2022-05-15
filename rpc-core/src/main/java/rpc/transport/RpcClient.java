package rpc.transport;

import rpc.entity.RpcRequest;
import rpc.serializer.CommonSerializer;

/**
 * @program: xu-rpc-framework-01
 * @description: RpcClient通用接口
 * @author: XuJY
 * @create: 2022-05-07 22:12
 **/
public interface RpcClient {



    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);


}
