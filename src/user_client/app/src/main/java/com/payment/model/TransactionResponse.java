package com.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class TransactionResponse {
    private String productName;
    private int amount;
}
