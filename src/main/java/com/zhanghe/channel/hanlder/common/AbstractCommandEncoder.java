package com.zhanghe.channel.hanlder.common;

import com.zhanghe.attribute.Attributes;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.v1.BasePacket;
import com.zhanghe.protocol.v1.MagicNum;
import com.zhanghe.util.Crc32Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 默认编码器
 * @author zhanghe
 */
@ChannelHandler.Sharable
public class AbstractCommandEncoder extends MessageToByteEncoder<BasePacket> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCommandEncoder.class);

    public static final AbstractCommandEncoder INSTANCE = new AbstractCommandEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BasePacket packet, ByteBuf byteBuf) throws Exception {
        logger.debug("send packet:"+packet);
        //填写魔数
        byteBuf.writeInt(MagicNum.MAGIC_NUM);
        //填写版本
        byteBuf.writeByte(packet.getVersion());
        //填写发送的命令
        byteBuf.writeByte(packet.getCommand());
        Serializer serializer = channelHandlerContext.channel().attr(Attributes.SERIALIZER_ATTRIBUTE_KEY).get();
        if( serializer == null ){
            serializer = SerializerManager.getDefault();
        }
        //填写使用哪个序列化器
        byteBuf.writeByte(serializer.getSerializerAlgorithm());
        //如果需要序列化 则将对象序列化发送过去
        if(packet.needSerilize()){
            byte[] bytes = serializer.serialize(packet);
            int length = bytes.length;
            //填写二进制流的长度
            byteBuf.writeInt( length + 8 );
            byteBuf.writeBytes(bytes);
            long crcValue = Crc32Util.getCrcValue(bytes);
            byteBuf.writeLong(crcValue);
            logger.debug("send packet,serializer:{},length:{}",serializer,length);
        }else{
            byteBuf.writeInt(0);
        }
    }

}
