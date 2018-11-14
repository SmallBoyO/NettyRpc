package com.zhanghe.protocol.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.zhanghe.protocol.Packet;
import com.zhanghe.protocol.v1.MagicNum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Collection;
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
            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(Collection.class, new JavaSerializer());
            Output output =new Output(1024,Integer.MAX_VALUE);
            kryo.writeClassAndObject(output, packet);
            byte[] bytes = output.toBytes();
            int length = bytes.length;
            //填写二进制流的长度
            byteBuf.writeInt(length);
            byteBuf.writeBytes(bytes);
        }else{
            byteBuf.writeInt(0);
        }
    }

}
