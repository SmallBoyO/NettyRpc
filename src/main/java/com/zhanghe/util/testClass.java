package com.zhanghe.util;

import java.math.BigDecimal;
import java.util.List;

public class testClass {
	public Integer id;
	public String info;
	public List<String> infos;
	public BigDecimal price;
	public SubClass sub;
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
	public List<String> getInfos() {
		return infos;
	}
	public void setInfos( List<String> infos ) {
		this.infos = infos;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice( BigDecimal price ) {
		this.price = price;
	}
	
	public SubClass getSub() {
		return sub;
	}
	public void setSub( SubClass sub ) {
		this.sub = sub;
	}
	public testClass(){
		
	}
	
	public testClass( Integer id ,String info ,List<String> infos ,BigDecimal price ,SubClass sub ){
		super();
		this.id = id;
		this.info = info;
		this.infos = infos;
		this.price = price;
		this.sub = sub;
	}
	@Override
	public String toString() {
		return "testClass [id=" + id + ", info=" + info + ", infos=" + infos + ", price=" + price + ", sub=" + sub + "]";
	}
	
}
