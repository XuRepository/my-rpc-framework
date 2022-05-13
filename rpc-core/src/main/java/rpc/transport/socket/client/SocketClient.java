package rpc.transport.socket.client;

import lombok.Data;
import rpc.transport.RpcClient;
import rpc.entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcResponse;
import rpc.enums.ResponseCode;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.registry.NacosServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.serializer.CommonSerializer;

import rpc.transport.socket.util.ObjectReader;
import rpc.transport.socket.util.ObjectWriter;
import rpc.util.RpcMessageChecker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @program: xu-rpc-framework-01
 * @description: PRC客户都安，负责发送rpc对象
 * @author: XuJY
 * @create: 2022-05-07 14:18
 **/
@Slf4j
@Data
public class SocketClient implements RpcClient {

    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public SocketClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest){

        if (serializer==null){
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //首先向nacos获取服务地址！
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());

        try(Socket socket = new Socket()){
            //建立和服务端的连接
            socket.connect(inetSocketAddress);
            log.info("和服务端建立连接：{}",inetSocketAddress);

//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//            objectOutputStream.writeObject(request);
//            objectOutputStream.flush();

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);

            RpcResponse rpcResponse = (RpcResponse) obj;

            if (rpcResponse == null) {
                log.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                log.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();

        }catch (IOException e){
            log.info("调用时发生错误：",e);
            return null;
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

}
