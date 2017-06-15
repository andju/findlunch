package edu.hm.cs.projektstudium.findlunch.webapp.push;

import java.io.IOException;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

public class SseSend implements Runnable {
	
	SseEmitter emitter;
	
	public SseSend(SseEmitter emitter) {
		this.emitter = emitter;
	}
	
	@Override
	public void run() {
		
		try {
			Thread.sleep(2500L);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SseEventBuilder builder = emitter.event().data("hallo").id("1").name("test").reconnectTime(10000L);
		System.out.println("E: "+emitter);
		try {
			emitter.send(builder);
			System.out.println("SEND "+builder);
		} catch (IOException e) {
			System.out.println("ERR EMMITTER: "+emitter);
		}
	}

}
