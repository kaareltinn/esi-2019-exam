package com.example.demo.sales.application.service;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.PurchaseOrder;
import com.example.demo.sales.rest.SalesRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderAssembler extends ResourceAssemblerSupport<PurchaseOrder, PurchaseOrderDTO> {

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public PurchaseOrderAssembler() {
        super(SalesRestController.class, PurchaseOrderDTO.class);
    }

    @Override
    public PurchaseOrderDTO toResource(PurchaseOrder po) {
        PurchaseOrderDTO dto = createResourceWithId(po.getId(), po);
        dto.setStatus(po.getStatus());
        dto.set_id(po.getId());
        dto.setRentalPeriod(BusinessPeriodDTO.of(po.getRentalPeriod().getStartDate(), po.getRentalPeriod().getEndDate()));
        dto.setPlant(plantInventoryEntryAssembler.toResource(po.getPlant()));
        return dto;
    }
}
