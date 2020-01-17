package com.zhanghe.protocol.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.SerializerAlgorithm;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class KryoSerializer implements Serializer {

    private static Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 8) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            return kryo;
        }
    };

    public static final KryoSerializer INSTANCE = new KryoSerializer();

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.KYRO;
    }

    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = KRYO_POOL.obtain();
        try {
            Output output =new Output(1024,Integer.MAX_VALUE);
            kryo.writeObject(output,object);
            return output.toBytes();
        }finally {
            KRYO_POOL.free(kryo);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Input input = new Input(bytes);
        Kryo kryo = KRYO_POOL.obtain();
        try {
            return kryo.readObject(input,clazz);
        }finally {
            KRYO_POOL.free(kryo);
        }
    }

    @Override
    public Object deserialize( byte[] bytes) {
        Input input = new Input(bytes);
        Kryo kryo = KRYO_POOL.obtain();
        try {
            return kryo.readClassAndObject(input);
        }finally {
            KRYO_POOL.free(kryo);
        }
    }

}
