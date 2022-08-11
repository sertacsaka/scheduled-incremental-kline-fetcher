package com.developersertac.scheduledbinanceincrementalfetch;

import java.io.IOException;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Component
@PropertySource("classpath:application.properties")
public class BinanceKlineFactory {
	
	public static boolean fetchLock = false;

    public BinanceKlineFactory() {
    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Autowired
    private Environment env;

    public String getConfigValue(String configKey){
        return env.getProperty(configKey);
    }
	
    @Autowired
	public BinanceKlineRepository repository;
	
	@Bean
	public MongoDbCollectionNaming collectionNaming() {
        return new MongoDbCollectionNaming();
    }

	public void insertBinanceKlines(String symbol, Interval interval, Long startTime, Integer stepSize) throws JsonParseException, IOException {
		Long latestOpenTime = Instant.now().getEpochSecond() * 1000;
		Long stepBlockSize = stepSize * interval.getUtsMs();
		Long stepFetchSize = (stepSize - 1) * interval.getUtsMs();
		int apiCallCount = 0;
		
		String uriRoot = env.getProperty("binance.api-root");
		String uriPrice = env.getProperty("binance.klines");
		String uri = uriRoot + uriPrice + "?symbol=" + symbol + "&interval=" + interval.getCode();
		
		MongoDbCollectionNaming.initName(symbol, interval);
		
		RestTemplate restTemplate = new RestTemplate();
		
		ObjectMapper ob = new ObjectMapper();
		SimpleModule module = new SimpleModule("CustomBinanceKindleDeserializer", new Version(1, 0, 0, null, null, null));
		
		module.addDeserializer(BinanceKline.class, new CustomBinanceKlineDeserializer());
		ob.registerModule(module);
		
		for (long openTime = startTime; openTime <= latestOpenTime; openTime += stepBlockSize) {
			String loopUri = uri + "&startTime=" + Long.toString(openTime) + "&endTime=" + Long.toString(openTime + stepFetchSize);
			
//			System.out.println(loopUri);
			
			repository.insert(ob.readValue(restTemplate.getForObject(loopUri, String.class), new TypeReference<List<BinanceKline>>(){}));
			apiCallCount++;
		}
		
		System.out.println("\t\t\tDone! API called " + apiCallCount + " times.");
	}
	
	public long findEarliestOpenTime(String symbol, Interval interval, Long fromOpenTime, Long toOpenTime, boolean fromExists) {
//		System.out.println(fromOpenTime + " " + toOpenTime + " " + fromExists);
	    Long inFrom = 0L;
	    Long inTo = 0L;
	    Long outFrom = 0L;
	    Long outTo = 0L;
	    Long middleUtsMs = 0L;
	    boolean inFromExists = false;
	    boolean outFromExists = false;
	    Interval intervalToUse;
	    
	    if (interval.getCode() == "3d") intervalToUse = Interval._1d; else intervalToUse = interval;

	    if ( fromOpenTime == 0L )
	    {
			inFrom = Constants.truncByInterval(Instant.now().getEpochSecond() * 1000, intervalToUse);
			inTo = inFrom;
			inFromExists = true;
	    }
	    else
	    {
	    	inFrom = fromOpenTime;
			inTo = toOpenTime;
			inFromExists = fromExists;
	    }
		
		if (inFromExists) {
			outFrom = Constants.truncByInterval(inFrom - Constants.uts3y, intervalToUse);
			
			if (checkBinanceKline(symbol, intervalToUse, outFrom))
			{
				outTo = outFrom;
				outFromExists = true;
			}
			else
			{
				outTo = inFrom;
				outFromExists = false;
			}
		}
		else
		{
			if (inFrom == inTo || inTo - inFrom == intervalToUse.getUtsMs())
				return inTo;
			else if (intervalToUse.getCode() == "1M" && Period
					.between((new Date(inFrom)).toInstant().atZone(ZoneId.of("UTC")).toLocalDate().withDayOfMonth(1),
							(new Date(inTo)).toInstant().atZone(ZoneId.of("UTC")).toLocalDate().withDayOfMonth(1))
					.getMonths() == 1)
				return inTo;
            
            middleUtsMs = Constants.truncByInterval((inFrom + inTo) / 2, intervalToUse);
            		
    		if (checkBinanceKline(symbol, intervalToUse, middleUtsMs))
    		{
    			outFrom = inFrom;
				outTo = middleUtsMs;
				outFromExists = false;
    		}
            else
            {
            	outFrom = middleUtsMs;
    			outTo = inTo;
				outFromExists = false;
            }
		}
		
		return findEarliestOpenTime(symbol, intervalToUse, outFrom, outTo, outFromExists);
	}

	public boolean checkBinanceKline(String symbol, Interval interval, Long openTime) {
		List<BinanceKline> klines = null;
		
		try {
			klines = getBinanceKlines(symbol, interval, openTime, openTime);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if ( klines.isEmpty() || ( !klines.isEmpty() && klines.size() == 0 ) ) return false;
		
		return true;
	}

	public List<BinanceKline> getBinanceKlines(String symbol, Interval interval, Long startTime, Long endTime) throws JsonParseException, IOException {
		String uriRoot = env.getProperty("binance.api-root");
		String uriPrice = env.getProperty("binance.klines");
		String uri = uriRoot + uriPrice + "?symbol=" + symbol + "&interval=" + interval.getCode();
		
		if (startTime != 0) 
		{
			uri += "&startTime=" + Long.toString(startTime);
			
			if (endTime != 0) 
			{
				uri += "&endTime=" + Long.toString(endTime);
			}
		}
		
		MongoDbCollectionNaming.initName(symbol, interval);
		
		RestTemplate restTemplate = new RestTemplate();
		String json = restTemplate.getForObject(uri, String.class);
		
		ObjectMapper ob = new ObjectMapper();
		SimpleModule module = new SimpleModule("CustomBinanceKindleDeserializer", new Version(1, 0, 0, null, null, null));
		
		module.addDeserializer(BinanceKline.class, new CustomBinanceKlineDeserializer());
		ob.registerModule(module);
		
		return (List<BinanceKline>) ob.readValue(json, new TypeReference<List<BinanceKline>>(){});
	}
}
