package rpc.transport.socket.server;

import lombok.extern.slf4j.Slf4j;
import rpc.handler.RequestHandler;
import rpc.transport.RpcServer;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.registry.NacosServiceRegistry;
import rpc.registry.ServiceRegistry;
import rpc.serializer.CommonSerializer;
import rpc.provider.ServiceProvider;
import rpc.provider.ServiceProviderImpl;


import java.io.IOException;
import java.net.InetSocketAddress;
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
public class SocketServer implements RpcServer {

    private final ExecutorService threadPool;

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private final RequestHandler requestHandler = new RequestHandler();

    private final String host;
    private final int port;
    private CommonSerializer serializer;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        //初始化线程池
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);

        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
    }

    /**
     * RpcServer对外提供接口的调用服务，添加register方法，在注册完一个服务后立刻开始监听：
     * @param
     */
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            //侦听客户端
            log.info("服务已经启动...");
            Socket socket;
            while ((socket=serverSocket.accept())!=null){
                log.info("客户端链接，ip：{}  端口：{}",socket.getInetAddress(),socket.getPort());
                //启动线程
                threadPool.execute(new RequestHandlerThread(socket,requestHandler, serviceRegistry,serializer));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            log.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
