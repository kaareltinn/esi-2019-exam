package com.example.demo.sales.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.BusinessPeriod;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.sales.domain.POStatus;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderDTO extends ResourceSupport {
    Long _id;
    BusinessPeriodDTO rentalPeriod;
    PlantInventoryEntryDTO plant;
    POStatus status;
}
