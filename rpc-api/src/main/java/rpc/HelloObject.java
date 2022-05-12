package rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: xu-rpc-framework-01
 * @description:接口发送的实体类对象
 * @author: XuJY
 * @create: 2022-05-07 13:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}

