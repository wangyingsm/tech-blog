package com.lanwon.zktest;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.admin.TopicCommand;
import kafka.admin.TopicCommand.TopicCommandOptions;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.kafka.common.security.JaasUtils;

public class App {

	public static void main(String[] args) {

		ZkClient zk = new ZkClient("192.168.0.107:2181", 15*1000, 15*1000);
		zk.setZkSerializer(new App().new ZKStringSerializer());
		ZkUtils zku = ZkUtils.apply(zk, JaasUtils.isZkSecurityEnabled());
		TopicCommand.listTopics(zku, new TopicCommandOptions(new String[0]));
		String[] opts = new String[]{
				"--create",
				"--topic",
				"test02",
				"--partitions",
				"1",
				"--replication-factor",
				"1"
		};
		TopicCommand.createTopic(zku, new TopicCommandOptions(opts));
		zk.close();

	}
	
	private class ZKStringSerializer implements ZkSerializer {
	    public byte[] serialize(Object data) throws ZkMarshallingError {
	        if(data instanceof String){
	            try {
	                return ((String)data).getBytes("UTF-8");
	            } catch (UnsupportedEncodingException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	        return null;
	    }
	    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
	        if (bytes == null)
	          return null;
	        else
	            try {
	                return new String(bytes, "UTF-8");
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }
	        return null;
	    }
	}

}
