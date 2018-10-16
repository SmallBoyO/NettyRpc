package com.zhanghe.protocol;

import java.util.Arrays;

public class RpcRequest {

	/**
	 * 调用id
	 */
	public String id;
	
	/**
	 * 调用类型
	 * -1: 断开连接
	 *  1: Rpc 调用
	 */
	public Integer type;
	
	/**
	 * 类名
	 */
	public String className;
	/**
	 * 方法名
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
    
	public String getId() {
		return id;
	}
	public void setId( String id ) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName( String className ) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName( String methodName ) {
		this.methodName = methodName;
	}
	public Class<?>[] getTypeParameters() {
		return typeParameters;
	}
	public void setTypeParameters( Class<?>[] typeParameters ) {
		this.typeParameters = typeParameters;
	}
	public Object[] getParametersVal() {
		return parametersVal;
	}
	public void setParametersVal( Object[] parametersVal ) {
		this.parametersVal = parametersVal;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType( Integer type ) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "RpcRequest [id=" + id + ", type=" + type + ", className=" + className + ", methodName=" + methodName + ", typeParameters="
				+ Arrays.toString(typeParameters) + ", parametersVal=" + Arrays.toString(parametersVal) + "]";
	}
	
	
}
