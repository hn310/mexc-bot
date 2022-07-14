package com.mxc.api.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderPlaceResp {
    private String symbol;
    private String orderId;
    private Long orderListId;
    private String clientOrderId;
    private Long transactTime;
}
