package com.zhanghe.service;

import java.util.Date;


public class DateServiceImpl implements DateService {

	@Override
	public String now() {
		return new Date().toString();
	}

}
