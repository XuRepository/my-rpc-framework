package rpc.hook;

import lombok.extern.slf4j.Slf4j;
import rpc.util.NacosUtil;
import rpc.util.ThreadPoolFactory;

/**
 * @program: xu-rpc-framework-01
 * @description: 钩子，关闭服务端时候实现自动向nacos注销服务
 * @author: XuJY
 * @create: 2022-05-15 14:05
 **/
@Slf4j
public class ShutdownHook {

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    private ShutdownHook(){
    }

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        log.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }
}
