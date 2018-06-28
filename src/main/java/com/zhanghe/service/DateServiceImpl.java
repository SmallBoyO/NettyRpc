package com.zhanghe.service;

import java.util.Date;


public class DateServiceImpl implements DateService {

	public String now() {
		return new Date().toString();
	}

}
