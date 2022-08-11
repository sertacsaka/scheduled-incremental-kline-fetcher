package com.developersertac.scheduledbinanceincrementalfetch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestSpringBootController {

	@Autowired
	BinanceKlineFactory bkf = new BinanceKlineFactory();
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@GetMapping("/kline-statistics")
	public String klineStatistics(@RequestParam String symbol, @RequestParam String interval) {
		
		if (BinanceKlineFactory.fetchLock) {
			return "DB update executing!<br>Try after a while.";
		}
		
		for (Interval i : Interval.values()) {
			if (i.getCode().equals(interval)) {
				MongoDbCollectionNaming.initName(symbol, i);
				break;
			}
		}
		
		String description = "";
		
		try {
		
			String latestOpenPrice = bkf.repository.findFirstByOrderByOpenTimeDesc().getOpen().toEngineeringString();
			String lastOpenTime = dateFormat.format(new Date(bkf.repository.findFirstByOrderByOpenTimeDesc().getOpenTime()));
			String firstOpenTime = dateFormat.format(new Date(bkf.repository.findFirstByOrderByOpenTimeAsc().getOpenTime()));
			String minCloseValue = bkf.repository.findFirstByOrderByCloseAsc().getClose().toEngineeringString();
			String maxCloseValue = bkf.repository.findFirstByOrderByCloseDesc().getClose().toEngineeringString();
			String klineCount = String.valueOf(bkf.repository.count());
	
			description += "<h2>" + symbol + " Parity Statistics for " + interval + " interval</h2>";
			description += "<ul>";
			description += "<li><b>Latest open price:</b> " + latestOpenPrice + "</li>";
			description += "<li><b>Last open time:</b> " + lastOpenTime + "</li>";
			description += "<li><b>First open time:</b> " + firstOpenTime + "</li>";
			description += "<li><b>Minimum close value:</b> " + minCloseValue + "</li>";
			description += "<li><b>Maximum close value:</b> " + maxCloseValue + "</li>";
			description += "<li><b>Kline count:</b> " + klineCount + "</li>";
			description += "</ul>";
			
		} catch (Exception e) {
			
			description = "DB or collection not exists yet!<br>Refresh after creation";
		}
		
		return description;
	}
}
