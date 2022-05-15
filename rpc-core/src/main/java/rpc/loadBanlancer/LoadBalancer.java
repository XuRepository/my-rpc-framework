package rpc.loadBanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @program: xu-rpc-framework-01
 * @description: 负载均衡，接口中的 select 方法用于从一系列 Instance 中选择一个。
 * 这里我就实现两个比较经典的算法：随机和转轮。
 * @author: XuJY
 * @create: 2022-05-15 15:22
 **/
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
