package com.zhanghe.protocol.v1.request;

import com.zhanghe.protocol.v1.CommandType;
import com.zhanghe.protocol.v1.Packet;

import java.util.Arrays;

public class RpcRequest extends Packet {
    /**
     * 请求id
     */
    public String requestId;
    /**
     * 调用接口类名
     */
    public String className;
    /**
     * 调用方法名
     */
    public String methodName;

    /**
     * 传入参数的类型
     */
    private Class<?>[] typeParameters;
    /**
     * 传入参数
     */
    private Object[] parametersVal;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(Class<?>[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public Object[] getParametersVal() {
        return parametersVal;
    }

    public void setParametersVal(Object[] parametersVal) {
        this.parametersVal = parametersVal;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public Byte getCommand() {
        return CommandType.RPC_REQUEST;
    }

    @Override
    public boolean needSerilize() {
        return true;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", typeParameters=" + Arrays.toString(typeParameters) +
                ", parametersVal=" + Arrays.toString(parametersVal) +
                '}';
    }
}
