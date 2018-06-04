package com.zhanghe.protocol;

public class RpcResponse {
	
	//调用id
	public String id;
	//异常
	public Exception exception;
	//执行结果
	public Object result;
	
	public String getId() {
		return id;
	}
	public void setId( String id ) {
		this.id = id;
	}
	public Exception getException() {
		return exception;
	}
	public void setException( Exception exception ) {
		this.exception = exception;
	}
	public Object getResult() {
		return result;
	}
	public void setResult( Object result ) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "RpcResponse [id=" + id + ", exception=" + exception + ", result=" + result + "]";
	}
	
}
