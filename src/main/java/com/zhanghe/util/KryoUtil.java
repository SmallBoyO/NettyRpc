package com.zhanghe.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.zhanghe.protocol.RpcRequest;

public class KryoUtil {
	public static void main( String[] args ) throws FileNotFoundException {
//		List<String> list = new ArrayList<String>();
//		list.add("infos1");
//		list.add("infos2");
//		SubClass sub = new SubClass(1, "subinfo");
//		testClass a = new testClass(1, "info", list,BigDecimal.ZERO,sub);
//		Kryo kryo = new Kryo();
//	    kryo.register(testClass.class);
//	    Output output = new Output(new FileOutputStream("E:\\20170315WorkSpace\\netty-rpc\\rpc.bin"));
//	    kryo.writeObject(output, a);
//	    output.close();
//	    
//	    Input input = new Input(new FileInputStream("E:\\20170315WorkSpace\\netty-rpc\\rpc.bin"));
//	    testClass b = kryo.readObject(input, testClass.class);
//	    input.close();
//	    System.out.println(b);
	    
		RpcRequest re = new RpcRequest();
		re.setId("1");
		re.setClassName("com.zhanghe");
		re.setMethodName("test");
		re.setParametersVal(new Object[]{"1","2"});
		re.setTypeParameters(new Class[]{String.class,String.class});
		
		Kryo kryo = new Kryo();
	    kryo.register(RpcRequest.class);
	    Output output = new Output(new FileOutputStream("E:\\20170315WorkSpace\\netty-rpc\\rpc.bin"));
	    kryo.writeObject(output, re);
	    output.close();
	    
	    Input input = new Input(new FileInputStream("E:\\20170315WorkSpace\\netty-rpc\\rpc.bin"));
	    RpcRequest b = kryo.readObject(input, RpcRequest.class);
	    input.close();
	    System.out.println(b);
		
	}
}
