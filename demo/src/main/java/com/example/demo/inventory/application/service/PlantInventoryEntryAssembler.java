package com.example.demo.inventory.application.service;

import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlantInventoryEntryAssembler {

    public PlantInventoryEntryDTO toResource(PlantInventoryEntry plant) {
        if(plant == null)
            return null;
        PlantInventoryEntryDTO dto = new PlantInventoryEntryDTO();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setDescription(plant.getDescription());
        dto.setPrice(plant.getPrice());
        return dto;
    }

    public List<PlantInventoryEntryDTO> toResources(List<PlantInventoryEntry> plants) {
        List<PlantInventoryEntryDTO> res = new ArrayList<>();
        for (PlantInventoryEntry p : plants) {
            res.add(toResource(p));
        }
        return res;

        // return plants.stream().map(p -> toResource(p)).collect(Collectors.toList());
    }
}
