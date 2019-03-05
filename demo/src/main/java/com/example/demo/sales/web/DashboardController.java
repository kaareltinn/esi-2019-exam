package com.example.demo.sales.web;

import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.service.PlantInventoryEntryAssembler;
import com.example.demo.sales.application.dto.CatalogQueryDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class	DashboardController	{
    @Autowired
    InventoryService inventoryService;

    @Autowired
    SalesService salesService;

    @GetMapping("/catalog/form")
    public String getQueryForm(Model model)	{
        model.addAttribute("catalogQuery", new CatalogQueryDTO());
        return	"dashboard/catalog/query-form";
    }

    @PostMapping("/catalog/query")
    public String getQueryResult(CatalogQueryDTO query, Model model) throws Exception {
        List<PlantInventoryEntryDTO> plants = inventoryService.findAvailablePlants(query.getName().toLowerCase(),
                query.getRentalPeriod().getStartDate(),
                query.getRentalPeriod().getEndDate());

        model.addAttribute("plants", plants);

        PurchaseOrderDTO po = new PurchaseOrderDTO();
        po.setRentalPeriod(query.getRentalPeriod());
        if(!plants.isEmpty()) {
            po.setPlant(plants.get(0));
            salesService.createPO(po);
        }

        model.addAttribute("po", po);

        return	"dashboard/catalog/query-result";
    }

}