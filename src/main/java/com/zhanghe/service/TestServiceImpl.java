package com.zhanghe.service;

public class TestServiceImpl implements TestService {

	@Override
	public String hello() {
		throw new RuntimeException("--");
	}

}
