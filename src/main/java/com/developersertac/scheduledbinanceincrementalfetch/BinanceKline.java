package com.developersertac.scheduledbinanceincrementalfetch;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Data;

@Data
@Document("#{@collectionNaming.setName()}")
public class BinanceKline {
	@Id
	private String id;
	private Long openTime; //1499040000000
	@Field(targetType = FieldType.DECIMAL128)
	private BigDecimal open; //"0.01634790"
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal high; //"0.80000000"
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal low; //"0.01575800"
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal close; //"0.01577100"
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal volume; //"148976.11427815"
    private Long closeTime; //1499644799999
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal quoteAssetVolume; //"2434.19055334"
    private BigInteger numberOfTrades; // 308
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal takerBuyBaseAssetVolume; //"1756.87402397"
	@Field(targetType = FieldType.DECIMAL128)
    private BigDecimal takerBuyQuoteAssetVolume; //"28.46694368"
    private String ignore; //"17928899.62484339"

    public BinanceKline(Long openTime, 
    					BigDecimal open, 
    					BigDecimal high, 
    					BigDecimal low, 
    					BigDecimal close,
    					BigDecimal volume, 
    					Long closeTime, 
    					BigDecimal quoteAssetVolume, 
    					BigInteger numberOfTrades,
    					BigDecimal takerBuyBaseAssetVolume, 
    					BigDecimal takerBuyQuoteAssetVolume, 
    					String ignore) {
		this.openTime = openTime;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.closeTime = closeTime;
		this.quoteAssetVolume = quoteAssetVolume;
		this.numberOfTrades = numberOfTrades;
		this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
		this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
		this.ignore = ignore;
	}

}
