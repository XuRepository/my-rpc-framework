package rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化代码
 */
@AllArgsConstructor
@Getter
public enum SerialzerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private int code;
}
