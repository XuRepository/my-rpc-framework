package rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import rpc.entity.RpcRequest;
import rpc.entity.RpcResponse;
import rpc.enums.SerialzerCode;
import rpc.exception.SerialzeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * @program: xu-rpc-framework-01
 * @description: Kryo序列化器
 * @author: XuJY
 * @create: 2022-05-12 23:18
 **/
@Slf4j
public class KryoSerializer implements CommonSerializer{

    private static final ThreadLocal<Kryo> kryoThreadLocal =
            ThreadLocal.withInitial(new Supplier<Kryo>() {
                @Override
                public Kryo get() {
                    Kryo kryo = new Kryo();
                    kryo.register(RpcRequest.class);
                    kryo.register(RpcResponse.class);
                    kryo.setReferences(true);
                    kryo.setRegistrationRequired(false);
                    return kryo;
                }
            });

    @Override
    public byte[] serialize(Object obj) {

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)) {

            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();

            return output.toBytes();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("kyro序列化发生错误：{}",e);
            throw new SerialzeException("kyro序列化发生错误");
        }

    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {

            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();

            return o;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("反序列化时有错误发生:", e);
            throw new SerialzeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerialzerCode.KRYO.getCode();
    }
}
