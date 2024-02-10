package com.isyb.obd.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "engine_info_fields")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EngineInfoField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    @Column(nullable = false)
    public String symbol;
    @Column(nullable = false)
    public String field_name;
}
