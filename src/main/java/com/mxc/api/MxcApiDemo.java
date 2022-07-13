package com.mxc.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mxc.api.basic.Config;
import com.mxc.api.basic.JsonUtil;
import com.mxc.api.basic.RestClientWrapper;
import com.mxc.api.request.PlaceOrderReq;
import com.mxc.api.response.MarketDepthWrapper;
import com.mxc.api.response.Result;

public class MxcApiDemo {

    public static final String REQUEST_HOST = "https://www.mexc.com";

    public static void main(String[] args) throws Exception {
        RestClientWrapper clientWrapper = new RestClientWrapper(Config.API_KEY, Config.API_SECRET);

        //交易对深度
        Result<MarketDepthWrapper> depth = clientWrapper.depth(Maps.newHashMap(ImmutableMap.<String, String>builder()
                .put("symbol", "ETH_USDT")
                .put("depth", "10")
                .build()));
        printResult("Trading Pair Depth", depth);

//        //查询当前委托单
//        Result<List<OpenOrderResp>> openOrders = clientWrapper.openOrders((Maps.newHashMap(ImmutableMap.<String, String>builder()
//                .put("symbol", "ETH_USDT")
//                .put("start_time", "1572076703000")
//                .put("trade_type", "BID")
//                .put("limit", "100")
//                .build())));
//        printResult("查询当前委托单", openOrders);
//
//        //下单
//        Result<String> result = clientWrapper.placeOrder(buildPlaceOrderReq());
//        printResult("下单",result);
//
//        //根据Id取消订单
//        Map<String, String> params = new HashMap<>(1);
//        params.put("order_ids", "202fee729a77479fb2e6a14c386e1e38");
//        Result<Map<String, String>> cancelResult = clientWrapper.cancel(params);
//        printResult("根据Id取消订单",cancelResult);

    }

    private static PlaceOrderReq buildPlaceOrderReq() {
        PlaceOrderReq req = new PlaceOrderReq();
        req.setOrderType("LIMIT_ORDER");
        req.setTradeType("BID");
        req.setSymbol("ETH_USDT");
        req.setPrice("1");
        req.setQuantity("5");
        return req;
    }

    private static void printResult(String mark, Object object) throws Exception {
        System.out.println(mark + " ->> " + JsonUtil.writeValue(object));
    }
}
