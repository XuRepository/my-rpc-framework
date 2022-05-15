package rpc.loadBanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @program: xu-rpc-framework-01
 * @description: 随机
 * @author: XuJY
 * @create: 2022-05-15 15:24
 **/
public class RandomLoadBalancer implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
