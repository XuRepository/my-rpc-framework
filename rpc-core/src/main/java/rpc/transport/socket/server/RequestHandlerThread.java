package rpc.transport.socket.server;

import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import rpc.handler.RequestHandler;
import rpc.registry.ServiceRegistry;
import rpc.serializer.CommonSerializer;
import rpc.transport.socket.util.ObjectReader;
import rpc.transport.socket.util.ObjectWriter;


import java.io.*;
import java.net.Socket;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * WorkerThread实现了Runnable接口，用于接收RpcRequest对象，通过反射 解析并且调用，生成RpcResponse对象并传输回去。
 * @author: XuJY
 * @create: 2022-05-07 15:34
 **/
@Slf4j
public class RequestHandlerThread implements Runnable {


    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }


    @Override
    public void run() {

//        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());){
//            //接收rpc请求
//            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
//
//            //根据客户请求的接口再map中找已经注册的服务
//            String interfaceName = rpcRequest.getInterfaceName();
//            Object service = serviceProvider.getServiceProvider(interfaceName);
//
//            //使用RequestHandler解析请求的服务和方法，并执行返回！
//            Object result = requestHandler.handle(rpcRequest, service);
//
//            //封装rpcResponse  并且通过网络返回给客户端
//            objectOutputStream.writeObject(RpcResponse.success(result));
//            objectOutputStream.flush();
//
//
//        } catch (IOException | ClassNotFoundException e) {
//            log.error("连接时有错误发生：", e);
//        }
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            log.error("调用或发送时有错误发生：", e);
        }

    }
}
