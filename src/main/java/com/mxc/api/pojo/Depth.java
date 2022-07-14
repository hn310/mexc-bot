package com.mxc.api.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Depth {

    private Long lastUpdateId;
    private String[][] bids;
    private String[][] asks;
}
