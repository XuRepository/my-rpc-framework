package server;

import entity.RpcRequest;
import entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import register.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public void run() {

        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());){
            //接收rpc请求
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

            //根据客户请求的接口再map中找已经注册的服务
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);

            //使用RequestHandler解析请求的服务和方法，并执行返回！
            Object result = requestHandler.handle(rpcRequest, service);

            //封装rpcResponse  并且通过网络返回给客户端
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();


        } catch (IOException | ClassNotFoundException e) {
            log.error("连接时有错误发生：", e);
        }

    }
}
