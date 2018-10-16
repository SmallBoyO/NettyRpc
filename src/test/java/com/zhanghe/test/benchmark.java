package com.zhanghe.test;

import com.zhanghe.server.RpcClient;
import com.zhanghe.service.TestService;
import com.zhanghe.service.TestServiceImpl;

public class benchmark {
	public static void main( String[] args ) throws ClassNotFoundException, InterruptedException {
		TestServiceImpl service = new TestServiceImpl();
		
		long start = System.currentTimeMillis();
		for(int i = 0;i<100;i++){
			service.hello();
		}
		long end = System.currentTimeMillis();
		System.out.println("本地调用耗时:"+(end-start));
		
		RpcClient r = new RpcClient("127.0.0.1", 6666);
		TestService testService = (TestService) r.proxy(TestService.class.getName());
		System.out.println("绑定成功");
		start = System.currentTimeMillis();
//		while(true){
//			testService.hello();
//		}
		for(int i=0;i<100;i++){
			testService.hello();
		}
		end = System.currentTimeMillis();
		System.out.println("远程调用耗时:"+(end-start));
		r.close();
		System.out.println("关闭");
	}
}
