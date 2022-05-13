package rpc.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import rpc.enums.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: xu-rpc-framework-01
 * @description: PRC调用返回值
 * @author: XuJY
 * @create: 2022-05-07 13:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 请求号
     */
    private String requestId;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 响应状态补充信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        //设置响应的请求的id，便于校验
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }


    public static <T> RpcResponse<T> fail(ResponseCode code){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
