package com.mgtechno.shared.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonSerialize
@Getter
@Setter
public class Account {
    private Long accountId;
    private Long customerId;
    private String application;
    private boolean verified;
    private String type;
    private BigDecimal balance;
    private String notes;
}
