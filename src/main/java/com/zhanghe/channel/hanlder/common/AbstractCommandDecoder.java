package com.zhanghe.channel.hanlder.common;

import com.zhanghe.protocol.request.HeartBeatRequest;
import com.zhanghe.protocol.response.HeartBeatResponse;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.serializer.impl.JsonSerializer;
import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class AbstractCommandDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic_num = byteBuf.readInt();
        Byte version = byteBuf.readByte();
        Byte command = byteBuf.readByte();


        logger.debug("recive command:{}",command);
        int length = byteBuf.readInt();
        if(length>0){
            Byte serializerAlgorithm = byteBuf.readByte();
            Serializer serializer = SerializerManager.getSerializer(serializerAlgorithm);

            if(serializer==null){
                logger.error("Serializer {} does not rigestered!");
                channelHandlerContext.channel().close();
                return;
            }

            byte[] bytes = new byte[length - 1 ];
            byteBuf.readBytes(bytes);

            Object obj = serializer.deserialize(bytes);
            logger.debug("recive packet:{}",obj.getClass());
            list.add(obj);
        }else{
            if(command==1){
                list.add(HeartBeatRequest.INSTANCE);
            }else if(command==2){
                list.add(HeartBeatResponse.INSTANCE);
            }
        }
    }

}
