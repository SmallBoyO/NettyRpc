package com.zhanghe.server;

import com.zhanghe.ThreadPool.RpcThreadPool;
import com.zhanghe.server.RpcClient;
import com.zhanghe.service.TestService;
import com.zhanghe.service.TestServiceImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class benchmark {
	public static void main( String[] args ) throws ClassNotFoundException, InterruptedException {
		final TestServiceImpl service = new TestServiceImpl();
		
		long start = System.currentTimeMillis();
		for(int i = 0;i<5000;i++){
			service.hello();
		}
		long end = System.currentTimeMillis();
		System.out.println("本地调用耗时:"+(end-start));
		
		RpcClient r = new RpcClient("127.0.0.1", 6666);
		final TestService testService = (TestService) r.proxy(TestService.class.getName());
		testService.hello();
		start = System.currentTimeMillis();
		for(int i = 0;i<5000;i++){
			testService.hello();
		}
		end = System.currentTimeMillis();
		System.out.println("远程调用耗时:"+(end-start));
		start = System.currentTimeMillis();
		ThreadPoolExecutor exe =  (ThreadPoolExecutor)RpcThreadPool.getExecutor(500,5000);
		for(int i = 0;i<5000;i++){
			exe.execute(new Runnable() {
				@Override
				public void run() {
					testService.hello();
				}
			});
		}
		exe.shutdown();
		try{
			exe.awaitTermination(100, TimeUnit.MINUTES);
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("远程调用耗时:"+(end-start));

		start = System.currentTimeMillis();
		ThreadPoolExecutor exe2 =  (ThreadPoolExecutor)RpcThreadPool.getExecutor(500,5000);
		for(int i = 0;i<5000;i++){
			exe2.execute(new Runnable() {
				@Override
				public void run() {
					service.hello();
				}
			});
		}
		exe2.shutdown();
		try{
			exe2.awaitTermination(100, TimeUnit.MINUTES);
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		end = System.currentTimeMillis();
		System.out.println("直接调用耗时:"+(end-start));
	}
}
