package rpc.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * netty传输协议的包类型
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
