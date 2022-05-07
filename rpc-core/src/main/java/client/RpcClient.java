package client;

import entity.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @program: xu-rpc-framework-01
 * @description: PRC客户都安，负责发送rpc对象
 * @author: XuJY
 * @create: 2022-05-07 14:18
 **/
@Slf4j
public class RpcClient {
    public Object sendRequest(RpcRequest request,String host,int port){

        try(Socket socket = new Socket(host, port)){

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            return objectInputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            log.info("调用时发生错误：",e);
            return null;
        }
    }


}
