package com.developersertac.scheduledbinanceincrementalfetch;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonParseException;

@SpringBootApplication
@EnableScheduling
public class ScheduledBinanceIncrementalFetch {

	@Autowired
	BinanceKlineFactory bkf = new BinanceKlineFactory();

	public static void main(String[] args) {
		SpringApplication.run(ScheduledBinanceIncrementalFetch.class, args);
	}

	@Scheduled(cron = "${cron.expression}")
	public void fetcher() {
		String symbol = bkf.getConfigValue("parity.symbol");
		String interval = bkf.getConfigValue("parity.interval");
		Long openTimeFrom = 0L;
		Long documentsCount;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		BinanceKlineFactory.fetchLock = true;

		long duration1 = System.currentTimeMillis();
		
		System.out.println("\nUpdate klines for: " + symbol + " start: " + dateFormat.format(System.currentTimeMillis()));
		
		for (Interval i : Interval.values()) { 
			if (i.getCode().equals(interval)) {
		    
			    System.out.println("Interval: " + i.getCode());
			
				try {
					MongoDbCollectionNaming.initName(symbol, i);
	
					long duration2 = System.currentTimeMillis();
					
					documentsCount = bkf.repository.count();
					
					duration2 = System.currentTimeMillis() - duration2;
					
					System.out.println("\tDocuments Count: " + documentsCount + " Completed: " + (duration2/(1000*60)) + ":" + ((duration2/1000)%60) + ":" + (duration2%1000));
					
				} catch (Exception e) {
					
					System.out.println("\tCollection for interval empty or not exists");
					
					documentsCount = 0L;
				}
		
				if (documentsCount == 0) {
					
					long duration2 = System.currentTimeMillis();
					
					openTimeFrom = bkf.findEarliestOpenTime(symbol, i, 0L, 0L, false);
					
					duration2 = System.currentTimeMillis() - duration2;
					
					System.out.println("\t\tFetch from earliest openTime: " + Long.toString(openTimeFrom) + " (" + dateFormat.format(new Date(openTimeFrom)) + ") Completed: " + (duration2/(1000*60)) + ":" + ((duration2/1000)%60) + ":" + (duration2%1000));
				}
				else {
					
					openTimeFrom = bkf.repository.findFirstByOrderByOpenTimeDesc().getOpenTime() + i.getUtsMs();
					
					System.out.println("\t\tFetch from openTime: " + Long.toString(openTimeFrom) + " (" + dateFormat.format(new Date(openTimeFrom)) + ")");
				}
		
				try {
					
					long duration2 = System.currentTimeMillis();
					
					bkf.insertBinanceKlines(symbol, i, openTimeFrom, 500);
					
					duration2 = System.currentTimeMillis() - duration2;
					
					System.out.println("Insert Completed: " + (duration2/(1000*60)) + ":" + ((duration2/1000)%60) + ":" + (duration2%1000));
					
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
		
		duration1 = System.currentTimeMillis() - duration1;
		
		System.out.println("All Completed: " + (duration1/(1000*60)) + ":" + ((duration1/1000)%60) + ":" + (duration1%1000));
		
		BinanceKlineFactory.fetchLock = false;
	}
}
