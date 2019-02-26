package com.example.demo.models;

import lombok.Data;

@Data
public class PurchaseOrderDTO {
    Long _id;
    BusinessPeriodDTO rentalPeriod;
}
