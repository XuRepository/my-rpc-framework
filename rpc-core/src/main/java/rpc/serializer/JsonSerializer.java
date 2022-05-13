package rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcRequest;
import rpc.enums.SerialzerCode;

import java.io.IOException;

/**
 * @program: xu-rpc-framework-01
 * @description: 使用JSON格式的序列化器
 * @author: XuJY
 * @create: 2022-05-12 19:43
 **/
@Slf4j
public class JsonSerializer implements CommonSerializer {

    //jackson序列化器
    private ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("jackson序列化时发生错误",e);
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);

            //如果反序列化的是rpcRequest，因为内不含有Object数组，Object 就是一个十分模糊的类型，会出现反序列化失败的现象，所以需要手动处理！
            if (obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;

        } catch (IOException e) {
            log.error("jackson反序列化时发生错误",e);
            e.printStackTrace();
            return null;
        }

    }

    /*
     在 RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，在反序列化时序列化器会根据字段类型进行反序列化，
     而 Object 就是一个十分模糊的类型，会出现反序列化失败的现象，
     这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，辅助反序列化，这就是 handleRequest() 方法的作用。
  */
    private Object handleRequest(Object obj) throws IOException {

        RpcRequest rpcRequest = (RpcRequest) obj;

        //处理object数组
        for(int i = 0; i < rpcRequest.getParamTypes().length; i ++) {

            Class<?> clazz = rpcRequest.getParamTypes()[i];

            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                //把反序列化失败的object对象进行重新序列化，然后按照参数类型重新反序列化！
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);

            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerialzerCode.JSON.getCode();
    }
}
