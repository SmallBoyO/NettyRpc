package com.zhanghe.channel.hanlder.common;

import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.protocol.v1.request.HeartBeatRequest;
import com.zhanghe.protocol.v1.response.HeartBeatResponse;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.v1.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 默认解码器
 * @author zhanghe
 */
public class AbstractCommandDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        Byte version = byteBuf.readByte();
        final byte command = byteBuf.readByte();
        Byte serializerAlgorithm = byteBuf.readByte();
        int length = byteBuf.readInt();
        if(length>0){
            Serializer serializer = SerializerManager.getSerializer(serializerAlgorithm);

            if(serializer==null){
                logger.error("Serializer {} does not rigestered!");
                channelHandlerContext.channel().close();
                return;
            }
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);

            Class clazz = Command.getCommandClass(command);
            Object obj = serializer.deserialize(clazz,bytes);
            logger.debug("recive command,clazz:{},version:{},command:{},size:{},serializerAlgorithm:{},Packet:{}",clazz,version,command,length,serializerAlgorithm,obj);
            list.add(obj);
        }else{
            switch (command){
                case CommandType.HEART_BEAT_REQUEST:
                    list.add(HeartBeatRequest.INSTANCE);
                    break;
                case 2:
                    list.add(HeartBeatResponse.INSTANCE);
                    break;
                case 5:
                    list.add(GetRegisterServiceRequest.INSTANCE);
                    break;
            }
        }
    }

}
