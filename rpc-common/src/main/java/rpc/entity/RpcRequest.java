package rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: xu-rpc-framework-01
 * @description: RPC请求体
 * @author: XuJY
 * @create: 2022-05-07 13:40
 **/
@Data
@Builder
public class RpcRequest implements Serializable {
    /**
     * 待调用接口名称
     */
    private String interfaceName;

    /**
     * 待调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;
}

