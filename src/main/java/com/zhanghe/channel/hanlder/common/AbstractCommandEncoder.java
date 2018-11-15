package com.zhanghe.channel.hanlder.common;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerManager;
import com.zhanghe.protocol.serializer.impl.JsonSerializer;
import com.zhanghe.protocol.v1.Packet;
import com.zhanghe.protocol.serializer.impl.KyroSerializer;
import com.zhanghe.protocol.v1.MagicNum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class AbstractCommandEncoder extends MessageToByteEncoder<Packet> {

    public static final AbstractCommandEncoder INSTANCE = new AbstractCommandEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        //填写魔数
        byteBuf.writeInt(MagicNum.MAGIC_NUM);
        //填写版本
        byteBuf.writeByte(packet.getVersion());
        //填写发送的命令
        byteBuf.writeByte(packet.getCommand());

        //如果需要序列化 则将对象序列化发送过去
        if(packet.needSerilize()){
            Serializer serializer = SerializerManager.getDefault();

            byte[] bytes = serializer.serialize(packet);
            int length = bytes.length;
            //填写二进制流的长度(+1因为要标示序列化器)
            byteBuf.writeInt( length + 1 );


            //填写使用哪个序列化器
            byteBuf.writeByte(serializer.getSerializerAlgorithm());
            byteBuf.writeBytes(bytes);
        }else{
            byteBuf.writeInt(0);
        }
    }

}
