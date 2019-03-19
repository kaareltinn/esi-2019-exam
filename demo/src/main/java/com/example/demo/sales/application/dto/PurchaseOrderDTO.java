package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.BusinessPeriod;
import com.example.demo.common.rest.ResourceSupport;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.sales.domain.POStatus;
import lombok.Data;


@Data
public class PurchaseOrderDTO extends ResourceSupport {
    Long _id;
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plant;
    POStatus status;
}
