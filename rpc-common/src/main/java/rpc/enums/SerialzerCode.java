package rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化代码
 */
@AllArgsConstructor
@Getter
public enum SerialzerCode {

    JSON(1);

    private int code;
}
