package rpc.socket.server;

import lombok.extern.slf4j.Slf4j;
import rpc.RequestHandler;
import rpc.registry.ServiceRegistry;


import java.io.IOException;
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
public class SocketServer {

    private final ExecutorService threadPool;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;

    public SocketServer(ServiceRegistry serviceRegistry) {
        //初始化线程池
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);

    }

    /**
     * RpcServer暂时只能注册一个接口，即对外提供一个接口的调用服务，添加register方法，在注册完一个服务后立刻开始监听：
     * @param port
     */
    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            //侦听客户端
            log.info("服务已经启动...");
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                log.info("客户端链接，ip：{}  端口：{}",socket.getInetAddress(),socket.getPort());
                //启动线程
                threadPool.execute(new RequestHandlerThread(socket,requestHandler,serviceRegistry));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }
}