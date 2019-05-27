package com.example.demo.sales.domain;

import com.example.demo.common.domain.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantReservation;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
public class PurchaseOrder {
    @Id
    @GeneratedValue
    Long id;

    @OneToMany
    List<PlantReservation> reservations;

    @ManyToOne
    PlantInventoryEntry plant;

    LocalDate issueDate;
    LocalDate paymentSchedule;

    @Column(precision=8,scale=2)
    BigDecimal total;

    @Enumerated(EnumType.STRING)
    POStatus status;

    @Embedded
    BusinessPeriod rentalPeriod;

    public static PurchaseOrder of(PlantInventoryEntry entry, BusinessPeriod period) {
        PurchaseOrder po = new PurchaseOrder();
        po.plant = entry;
        po.rentalPeriod = period;
        po.reservations = new ArrayList<>();
        po.issueDate = LocalDate.now();
        po.status = POStatus.PENDING;
        return po;
    }

    public void setStatus(POStatus newStatus) {
        status = newStatus;
    }

    public void addReservation(PlantReservation reservation) {
        reservations.add(reservation);
    }

    public void setRentalPeriod(BusinessPeriod newPeriod) { rentalPeriod = newPeriod; }
}
