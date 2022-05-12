package rpc.exception;

import rpc.enums.RpcError;

/**
 * @program: xu-rpc-framework-01
 * @description:
 * @author: XuJY
 * @create: 2022-05-07 20:51
 **/
public class SerialzeException extends RuntimeException{
    public SerialzeException(String e) {
        super(e);
    }

    }
