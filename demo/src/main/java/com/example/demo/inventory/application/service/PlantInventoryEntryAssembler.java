package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.rest.InventoryRestController;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlantInventoryEntryAssembler
        extends ResourceAssemblerSupport<PlantInventoryEntry, PlantInventoryEntryDTO> {

    public PlantInventoryEntryAssembler() {
        super(InventoryRestController.class, PlantInventoryEntryDTO.class);
    }

    @Override
    public PlantInventoryEntryDTO toResource(PlantInventoryEntry plant) {
        PlantInventoryEntryDTO dto = createResourceWithId(plant.getId(), plant);
        dto.set_id(plant.getId());
        dto.setName(plant.getName());
        dto.setDescription(plant.getDescription());
        dto.setPrice(plant.getPrice());
        return dto;
    }
}
