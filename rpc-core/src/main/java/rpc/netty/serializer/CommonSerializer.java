package rpc.netty.serializer;

/**
 * @program: xu-rpc-framework-01
 * @description: 通用的序列化反序列化接口
 * @author: XuJY
 * @create: 2022-05-12 19:39
 **/
public interface CommonSerializer {

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes,Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code){
        switch (code){
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }

    }

}
