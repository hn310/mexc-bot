package com.mxc.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.mxc.api.basic.Config;
import com.mxc.api.basic.SignatureUtil;
import com.mxc.api.pojo.Account;
import com.mxc.api.pojo.MyTrades;
import com.mxc.api.pojo.Order;
import com.mxc.api.pojo.OrderCancelResp;
import com.mxc.api.pojo.OrderPlaceResp;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MexcApiV3Auth {
	private static final OkHttpClient OK_HTTP_CLIENT = createOkHttpClient();
	private String requestHost;

	public MexcApiV3Auth(String requestHost) {
		this.requestHost = requestHost;
	}

	private static OkHttpClient createOkHttpClient() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		return new OkHttpClient.Builder().connectTimeout(45, TimeUnit.SECONDS).readTimeout(45, TimeUnit.SECONDS)
				.writeTimeout(45, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor)
				.addInterceptor(new SignatureInterceptor(Config.API_SECRET, Config.API_KEY)).build();
	}

	private <T> T get(String uri, Map<String, String> params, TypeReference<T> ref) {
		try {
			Response response = OK_HTTP_CLIENT
					.newCall(new Request.Builder().url(requestHost + uri + "?" + toQueryString(params)).get().build())
					.execute();
			return handleResponse(response, ref);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T post(String uri, Map<String, String> params, TypeReference<T> ref) {
		try {
			Response response = OK_HTTP_CLIENT
					.newCall(new Request.Builder().url(this.requestHost.concat(uri))
							.post(RequestBody.create(toQueryString(params), MediaType.get("text/plain"))).build())
					.execute();
			return handleResponse(response, ref);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T delete(String uri, Map<String, String> params, TypeReference<T> ref) {
		try {
			return handleResponse(OK_HTTP_CLIENT
					.newCall(new Request.Builder().url(this.requestHost.concat(uri))
							.delete(RequestBody.create(toQueryString(params), MediaType.get("text/plain"))).build())
					.execute(), ref);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T handleResponse(Response response, TypeReference<T> ref) throws IOException {
		Gson gson = new Gson();
		assert response.body() != null;
		String content = response.body().string();
		if (200 != response.code()) {
			throw new RuntimeException(content);
		}
		return gson.fromJson(content, ref.getType());
	}

	private String toQueryString(Map<String, String> params) {
		return params.entrySet().stream()
				.map((entry) -> entry.getKey() + "=" + SignatureUtil.urlEncode(entry.getValue()))
				.collect(Collectors.joining("&"));
	}

	public Order getOrder(Map<String, String> params) {
		return get("/api/v3/order", params, new TypeReference<Order>() {
		});
	}

	public List<Order> allOrders(Map<String, String> params) {
		return get("/api/v3/allOrders", params, new TypeReference<List<Order>>() {
		});
	}

	public List<Order> openOrders(Map<String, String> params) {
		return get("/api/v3/openOrders", params, new TypeReference<List<Order>>() {
		});
	}

	public List<MyTrades> myTrades(Map<String, String> params) {
		return get("/api/v3/myTrades", params, new TypeReference<List<MyTrades>>() {
		});
	}

	public Account account(Map<String, String> params) {
		return get("/api/v3/account", params, new TypeReference<Account>() {
		});
	}

	public OrderPlaceResp placeOrder(Map<String, String> params) {
		return post("/api/v3/order", params, new TypeReference<OrderPlaceResp>() {
		});
	}

	public OrderPlaceResp placeOrderTest(Map<String, String> params) {
		return post("/api/v3/order/test", params, new TypeReference<OrderPlaceResp>() {
		});
	}

	public OrderCancelResp cancelOrder(Map<String, String> params) {
		return delete("/api/v3/order", params, new TypeReference<OrderCancelResp>() {
		});
	}

	public List<OrderCancelResp> cancelOpenOrders(Map<String, String> params) {
		return delete("/api/v3/openOrders", params, new TypeReference<List<OrderCancelResp>>() {
		});
	}
}
