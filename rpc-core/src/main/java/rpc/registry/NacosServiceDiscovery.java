package rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import rpc.enums.RpcError;
import rpc.exception.RpcException;
import rpc.loadBanlancer.LoadBalancer;
import rpc.loadBanlancer.RandomLoadBalancer;
import rpc.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @program: xu-rpc-framework-01
 * @description: 服务发现实现,选择负载均衡
 * @author: XuJY
 * @create: 2022-05-15 15:00
 **/
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery{
    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer==null) loadBalancer = new RandomLoadBalancer();
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            //每个serviceName服务会有 多个 提供者提供！！！
            //在 lookupService 方法中，通过 getAllInstance 获取到某个服务的所有提供者列表后，需要选择一个，
            // 这里就涉及了负载均衡策略，这里我们先选择第 0 个，后面某节会详细讲解负载均衡。
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.FAILED_TO_GET_INSTANCE);
        }
    }
}
