package rpc.loadBanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @program: xu-rpc-framework-01
 * @description: 轮询
 * @author: XuJY
 * @create: 2022-05-15 15:25
 **/
public class RoundRobinLoadBalancer implements LoadBalancer{

    int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index>=instances.size()){
            index = index % instances.size();
        }
        return instances.get(index++);
    }
}
