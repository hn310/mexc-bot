package com.mxc.api;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.mxc.api.basic.Config;
import com.mxc.api.pojo.OrderPlaceResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	public static void main(String[] args) {
		MexcApiV3Auth mx = new MexcApiV3Auth(new Config().getRequestHost());
		Gson gson = new Gson();
		
		Map<String, String> params = new HashMap<>();
		params.put("symbol", "BTCUSDT");
		params.put("side", "SELL");
		params.put("type", "LIMIT");
		params.put("quantity", "1");
		params.put("price", "100000");
		params.put("recvWindow", "60000");

		// 下单
//		OrderPlaceResp placeResp = mx.placeOrder(params);
//		log.info("==>>placeResp:{}", gson.toJson(placeResp));

		// 测试下单
		OrderPlaceResp placeRespTest = mx.placeOrderTest(params);
		log.info("==>>placeRespTest:{}", gson.toJson(placeRespTest));

		
	}
}
