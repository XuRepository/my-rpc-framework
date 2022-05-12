package rpc;

import lombok.extern.slf4j.Slf4j;
import rpc.HelloObject;
import rpc.HelloService;

/**
 * @program: xu-rpc-framework-01
 * @description: 服务端对接口的具体实现
 * @author: XuJY
 * @create: 2022-05-07 13:34
 **/
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(HelloObject object) {
        log.info("接收到客户端数据："+object.getMessage());
        return "这个是使用client发送的对象返回的值，id="+object.getId();

    }
}
