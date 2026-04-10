package com.zhanghe.test.testClient;

import com.zhanghe.protocol.v1.response.RpcResponse;
import com.zhanghe.rpc.core.client.RpcRequestCallBack;
import com.zhanghe.rpc.core.client.RpcRequestCallBackholder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

public class RpcRequestCallBackTest {

  @Test
  public void startShouldUseTenSecondsAsDefaultTimeout() throws TimeoutException {
    RecordTimeoutRpcRequestCallBack callBack = new RecordTimeoutRpcRequestCallBack();

    callBack.start();

    Assert.assertEquals(Long.valueOf(10L), callBack.recordedTimeout);
    Assert.assertEquals(TimeUnit.SECONDS, callBack.recordedTimeUnit);
  }

  private static class RecordTimeoutRpcRequestCallBack extends RpcRequestCallBack {

    private Long recordedTimeout;

    private TimeUnit recordedTimeUnit;

    private RecordTimeoutRpcRequestCallBack() {
      super("requestId", new RpcRequestCallBackholder());
    }

    @Override
    public RpcResponse get(Long timeOut, TimeUnit timeUnit) {
      recordedTimeout = timeOut;
      recordedTimeUnit = timeUnit;
      return new RpcResponse();
    }
  }
}
