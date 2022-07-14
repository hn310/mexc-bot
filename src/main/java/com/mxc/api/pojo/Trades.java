package com.mxc.api.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Trades {
    private Long id;
    private String price;
    private String qty;
    private String quoteQty;
    private Long time;
    private Boolean isBuyerMaker;
    private Boolean isBestMatch;
}
