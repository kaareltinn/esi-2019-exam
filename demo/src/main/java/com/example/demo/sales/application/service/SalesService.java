package com.example.demo.sales.application.service;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.application.service.BusinessPeriodValidator;
import com.example.demo.common.domain.BusinessPeriod;
import com.example.demo.inventory.application.service.InventoryService;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.InventoryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.POStatus;
import com.example.demo.sales.domain.PurchaseOrder;
import com.example.demo.sales.domain.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.util.List;

@Service
public class SalesService {

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PurchaseOrderRepository poRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    public PurchaseOrderDTO findPO(Long id) {
        PurchaseOrder po = poRepository.findById(id).orElse(null);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO createPO(PurchaseOrderDTO poDTO) throws Exception {

        BusinessPeriod period = BusinessPeriod.of(
                         poDTO.getRentalPeriod().getStartDate(),
                         poDTO.getRentalPeriod().getEndDate());

        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new Exception("Invalid PO Period");

        if(poDTO.getPlant() == null)
            throw new Exception("Invalid Input Plant");

        PlantInventoryEntry plant = plantInventoryEntryRepository.findById(poDTO.getPlant().get_id()).orElse(null);

        if(plant == null)
            throw new Exception("Plant NOT Found");

        PurchaseOrder po = PurchaseOrder.of(plant, period);
        poRepository.save(po);

        List<PlantInventoryItem> availableItems = inventoryRepository.findAvailableItems(
                po.getPlant().getId(),
                po.getRentalPeriod().getStartDate(),
                po.getRentalPeriod().getEndDate());

        if(availableItems.size() == 0) {
            po.setStatus(POStatus.REJECTED);
            throw new Exception("No available items");
        }

        PlantReservation reservation = new PlantReservation();
        reservation.setSchedule(po.getRentalPeriod());
        reservation.setPlant(availableItems.get(0));

        plantReservationRepository.save(reservation);
        po.getReservations().add(reservation);

        poRepository.save(po);
        return purchaseOrderAssembler.toResource(po);

    }

    public PurchaseOrderDTO changePeriod(Long id, BusinessPeriodDTO periodDTO) throws Exception {
        PurchaseOrder po = poRepository.findById(id).orElse(null);

        if(po == null)
            throw new Exception("PO Not Found");
        if(po.getStatus() != POStatus.OPEN)
            throw new Exception("PO period cannot be changed due to it is not accepted");

        BusinessPeriod period = BusinessPeriod.of(
                periodDTO.getStartDate(),
                periodDTO.getEndDate()
        );

        DataBinder binder = new DataBinder(period);
        binder.addValidators(new BusinessPeriodValidator());
        binder.validate();

        if (binder.getBindingResult().hasErrors())
            throw new Exception("Invalid PO Period");

        List<PlantInventoryItem> availableItems = inventoryRepository.findAvailableItems(
                po.getPlant().getId(),
                po.getRentalPeriod().getStartDate(),
                po.getRentalPeriod().getEndDate());

        if(availableItems.size() == 0) {
            throw new Exception("No available items");
        }

        for (PlantReservation reservation : po.getReservations()) {
            PlantInventoryItem item = reservation.getPlant();
            if (availableItems.contains(item)) {
                reservation.setSchedule(period);
                plantReservationRepository.save(reservation);
                po.setRentalPeriod(period);
                poRepository.save(po);
                return purchaseOrderAssembler.toResource(po);
            }
        }

        // create new reservation
        po.setRentalPeriod(period);
        poRepository.save(po);

        PlantReservation reservation = new PlantReservation();
        reservation.setSchedule(po.getRentalPeriod());
        reservation.setPlant(availableItems.get(0));

        plantReservationRepository.save(reservation);
        po.getReservations().add(reservation);

        poRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO acceptPO(Long id) throws Exception {
        PurchaseOrder po = getPO(id);
        po.setStatus(POStatus.OPEN);
        poRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    public PurchaseOrderDTO rejectPO(Long id) throws Exception {
        PurchaseOrder po = getPO(id);
        while (!po.getReservations().isEmpty()) {
            plantReservationRepository.delete(po.getReservations().remove(0));
        }
        po.setStatus(POStatus.CLOSED);
        poRepository.save(po);
        return purchaseOrderAssembler.toResource(po);
    }

    private PurchaseOrder getPO(Long id) throws Exception {
        PurchaseOrder po = poRepository.findById(id).orElse(null);
        if(po == null)
            throw new Exception("PO Not Found");
        if(po.getStatus() != POStatus.PENDING)
            throw new Exception("PO cannot be accepted/rejected due to it is not Pending");
        return po;
    }
}
