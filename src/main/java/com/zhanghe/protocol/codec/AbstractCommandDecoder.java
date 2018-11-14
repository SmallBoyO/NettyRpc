package com.zhanghe.protocol.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Collection;
import java.util.List;

@ChannelHandler.Sharable
public class AbstractCommandDecoder extends ByteToMessageDecoder {

    public static final AbstractCommandDecoder INSTANCE = new AbstractCommandDecoder();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic_num = byteBuf.readInt();
        Byte version = byteBuf.readByte();
        Byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        if(length>0){
            byte[] bytes = new byte[length];
            byteBuf.readBytes(bytes);
            Input input = new Input(bytes);
            Kryo kryo = new Kryo();
            kryo.addDefaultSerializer(Collection.class, new JavaSerializer());
            Object obj = kryo.readClassAndObject(input);
            list.add(obj);
        }
    }

}
