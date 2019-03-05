package com.example.demo.inventory.application.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlantInventoryEntryDTO {
    Long id;
    String name;
    String description;
    BigDecimal price;
}
