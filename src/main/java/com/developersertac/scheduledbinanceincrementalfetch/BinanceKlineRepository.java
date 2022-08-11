package com.developersertac.scheduledbinanceincrementalfetch;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BinanceKlineRepository extends MongoRepository<BinanceKline, String> {

	BinanceKline findFirstByOrderByOpenTimeDesc();
	BinanceKline findFirstByOrderByOpenTimeAsc();
	BinanceKline findFirstByOrderByCloseDesc();
	BinanceKline findFirstByOrderByCloseAsc();
}
