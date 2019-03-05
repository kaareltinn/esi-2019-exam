package com.example.demo.inventory.domain.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Value
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
@AllArgsConstructor(staticName="of")
public class PlantInventoryEntry {
    @Id
    @GeneratedValue
    Long id;

    String name;
    String description;
    @Column(precision=8, scale=2)
    BigDecimal price;
}
