package rpc.exception;

import rpc.enums.RpcError;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 20:51
 **/
public class RpcException extends RuntimeException{
    public RpcException(RpcError error) {
        super(error.getMessage());
    }

    public RpcException(RpcError error,String detail) {
        super(error.getMessage()+":"+detail);
    }

    public RpcException(RpcError error,Throwable cause) {
        super(error.getMessage(),cause);
    }
}
