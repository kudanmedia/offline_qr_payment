package com.payment.merchant.model.connector.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Reserve {

    private int amount;
    private String returnUrl;
    private int count;
    private String productName;
    private long userIdx;

}
