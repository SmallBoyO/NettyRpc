package com.zhanghe.channel.hanlder.common;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.request.GetRegisterServiceRequest;
import com.zhanghe.protocol.v1.request.HeartBeatRequest;
import com.zhanghe.protocol.v1.response.HeartBeatResponse;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.v1.Command;
import com.zhanghe.util.CRC32Util;
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
            Serializer serializer = channelHandlerContext.channel().attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).get();
            if( serializer == null ){
                serializer = SerializerManager.getDefault();
            }
            if(  !serializerAlgorithm.equals(serializer.getSerializerAlgorithm()) ){
                logger.error("serializerAlgorithm:[{}] not support!",serializerAlgorithm);
                channelHandlerContext.channel().close();
            }
            byte[] bytes = new byte[length-8];
            byteBuf.readBytes(bytes);
            long crcValue = CRC32Util.getCrcValue(bytes);
            long clientCrcValue = byteBuf.readLong();
            if(crcValue!=clientCrcValue){
                logger.error("CRC check failed!");
                channelHandlerContext.channel().close();
            }
            Class clazz = Command.getCommandClass(command);
            Object obj = serializer.deserialize(clazz,bytes);
            logger.debug("receive command,clazz:{},version:{},command:{},size:{},serializerAlgorithm:{},Packet:{}",clazz,version,command,length,serializerAlgorithm,obj);
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
                default:
                    break;
            }
        }
    }

}
