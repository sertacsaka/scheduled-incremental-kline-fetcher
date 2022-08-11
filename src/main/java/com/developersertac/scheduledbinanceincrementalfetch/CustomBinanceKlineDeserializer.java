package com.developersertac.scheduledbinanceincrementalfetch;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CustomBinanceKlineDeserializer extends StdDeserializer<BinanceKline> {
    
	private static final long serialVersionUID = 1L;

	public CustomBinanceKlineDeserializer() {
        this(null);
    }

    public CustomBinanceKlineDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public BinanceKline deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
          
        return new BinanceKline(
                Long.parseLong(node.get(0).asText()),
                new BigDecimal(node.get(1).asText()),
                new BigDecimal(node.get(2).asText()),   
                new BigDecimal(node.get(3).asText()),   
                new BigDecimal(node.get(4).asText()),
                new BigDecimal(node.get(5).asText()),
                Long.parseLong(node.get(6).asText()),
                new BigDecimal(node.get(7).asText()),
                BigInteger.valueOf(Long.parseLong(node.get(8).asText())),
                new BigDecimal(node.get(9).asText()),
                new BigDecimal(node.get(10).asText()),
                node.get(11).asText()
        		);
    }
}
