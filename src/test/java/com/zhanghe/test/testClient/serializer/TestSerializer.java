package com.zhanghe.test.testClient.serializer;

import com.zhanghe.protocol.serializer.Serializer;
import com.zhanghe.protocol.serializer.impl.KryoSerializer;

public class TestSerializer extends KryoSerializer implements Serializer {

  @Override
  public byte getSerializerAlgorithm() {
    return 10;
  }
}
