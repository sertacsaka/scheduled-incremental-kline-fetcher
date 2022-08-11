package com.developersertac.scheduledbinanceincrementalfetch;

public final class MongoDbCollectionNaming {
	public static String collectionName;
	
    public String setName() {
        return MongoDbCollectionNaming.collectionName;
    }
	
    public static void initName(String symbol, Interval interval) {
    	MongoDbCollectionNaming.collectionName = symbol + "-" + interval.getCode();
    }
}
