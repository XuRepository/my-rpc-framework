package rpc;

import lombok.extern.slf4j.Slf4j;
import rpc.annotation.Service;

/**
 * @program: xu-rpc-framework-01
 * @description: 服务端服务端实现
 * @author: XuJY
 * @create: 2022-05-15 16:35
 **/
@Slf4j
@Service
public class ByeServiceImpl implements ByeService{
    @Override
    public String bye(String name) {
        log.info("接收到客户端数据："+name);
        return "[ByeService]这个是使用client发送的对象返回的值，name="+name;
    }
}
