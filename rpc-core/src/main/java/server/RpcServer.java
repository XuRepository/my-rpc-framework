package server;

import entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * 服务端的实现，使用一个ServerSocket监听某个端口，循环接收连接请求，+
 * 如果发来了请求就创建一个线程，在新线程中处理调用。这里创建线程采用线程池
 * @author: XuJY
 * @create: 2022-05-07 15:20
 **/
@Slf4j
public class RpcServer {

    private final ExecutorService threadPool;

    public RpcServer() {
        //初始化线程池
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,
                keepAliveTime,TimeUnit.SECONDS,workingQueue,threadFactory);
    }

    /**
     * RpcServer暂时只能注册一个接口，即对外提供一个接口的调用服务，添加register方法，在注册完一个服务后立刻开始监听：
     * @param service 客户端远程调用的服务，属于是Api的实现
     * @param port
     */
    public void register(Object service,int port){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            //侦听客户端
            log.info("服务已经启动...");
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                log.info("客户端链接，ip为："+socket.getInetAddress());
                //启动线程
                threadPool.execute(new WorkerThread(socket,service));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }
}
