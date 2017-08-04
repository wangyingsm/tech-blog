package com.lanwon.kafkatest;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.ConfigType;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
	
	static byte[] buf ;
	static int len = 1024*1024;
	
	static AtomicLong producerTotal = new AtomicLong();
	static AtomicLong consumerTotal = new AtomicLong();
	
	private static Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		
		new Thread() {
			@Override
			public void run(){
				produce();
			}
		}.start();

		new Thread() {
			@Override
			public void run(){
				consume();
			}
		}.start();
		
		
	}

	private static void produce() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.0.107:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		 
		 Producer<String, byte[]> producer = new KafkaProducer<>(props);
		 Random r = new Random();
		 producerTotal.set(0);
		 try {
			 buf = new byte[1024];
			 for(int i = 1000000; i< 1000000 + len; i++){
				 r.nextBytes(buf);
				 long start = System.currentTimeMillis();
				 producer.send(new ProducerRecord<String, byte[]>("test02", String.valueOf(i), buf));
				 producerTotal.addAndGet(System.currentTimeMillis() - start);
			 }
			 log.info("Producer time : {} ms", producerTotal);
		 } finally {
			 producer.close();
		 }
		
	}

	private static void consume() {
		
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.0.107:9092");
		props.put("group.id", "test02");
		props.put("enable.auto.commit", "false");
	    props.put("auto.commit.interval.ms", "1000");
	    props.put("auto.offset.reset", "earliest");
	    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
	    KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
	    try {
		    consumer.subscribe(Arrays.asList("test02"));
		    consumerTotal.set(0);
		    int received = 0;
		    while(received < len){
		    	long start = System.currentTimeMillis();
		    	ConsumerRecords<String, byte[]> records = consumer.poll(100);
		    	consumer.commitSync();
		    	consumerTotal.addAndGet(System.currentTimeMillis() - start);
		    	received += records.count();
		    	for(ConsumerRecord<String, byte[]> record: records){
		    		//log.info("offset = {}, key = {}, value = {}", record.offset(), record.key(), Arrays.toString(record.value()));
		    	}
		    }			
			log.info("Consumer time : {} ms", consumerTotal);
	    } finally {
	    	consumer.close();
	    }
		
	}
	
}
