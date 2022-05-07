package server;

import entity.RpcRequest;
import entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;

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
public class WorkerThread implements Runnable {

    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }


    @Override
    public void run() {

        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());){
            //接收rpc请求
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();

            //解析请求，找到需要调用的服务端方法本身
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

            //在服务端调用方法本身
            Object result = method.invoke(service, rpcRequest.getParameters());

            //封装rpcResponse  并且通过网络返回给客户端
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();


        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("连接时有错误发生：", e);

        }

    }
}
