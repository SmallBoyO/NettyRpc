package com.zhanghe.util;

public class SubClass {
	public Integer id;
	public String info;
	public Integer getId() {
		return id;
	}
	public void setId( Integer id ) {
		this.id = id;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo( String info ) {
		this.info = info;
	}
	public SubClass(){
	}
	public SubClass( Integer id ,String info ){
		super();
		this.id = id;
		this.info = info;
	}
	
	@Override
	public String toString() {
		return "SubClass [id=" + id + ", info=" + info + "]";
	}
	
	
}
