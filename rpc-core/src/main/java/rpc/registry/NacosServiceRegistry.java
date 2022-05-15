package rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @program: xu-rpc-framework-01
 * @description: Nacos服务注册中心
 * @author: XuJY
 * @create: 2022-05-13 12:13
 **/
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry{

//    private static final String SERVICE_ADDR = "127.0.0.1:8848";//nacos服务器的地址，nacos服务部署在本地
//    private static final NamingService NAMING_SERVICE;
//
//    static {
//        /*
//        通过 NamingFactory 创建 NamingService 连接 Nacos，
//        连接的过程写在了静态代码块中，在类加载时自动连接。namingService 提供了两个很方便的接口，
//        registerInstance 和 getAllInstances 方法，前者可以直接向 Nacos 注册服务，后者可以获得提供某个服务的所有提供者的列表。
//        所以接口的这两个方法只需要包装一下就好了。
//         */
//        //            NAMING_SERVICE = NamingFactory.createNamingService(SERVICE_ADDR);
//        NAMING_SERVICE = NacosUtil.getNacosNamingService();
//    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {

        try {
            NacosUtil.registerService(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

//    @Override
//    public InetSocketAddress lookupService(String serviceName) {
//        try {
//            List<Instance> instances = NAMING_SERVICE.getAllInstances(serviceName);
//            //每个serviceName服务会有 多个 提供者提供！！！
//            //在 lookupService 方法中，通过 getAllInstance 获取到某个服务的所有提供者列表后，需要选择一个，
//            // 这里就涉及了负载均衡策略，这里我们先选择第 0 个，后面某节会详细讲解负载均衡。
//            Instance instance = instances.get(0);
//            return new InetSocketAddress(instance.getIp(),instance.getPort());
//        } catch (NacosException e) {
//            log.error("注册服务时有错误发生:", e);
//            throw new RpcException(RpcError.FAILED_TO_GET_INSTANCE);
//        }
//    }
}
