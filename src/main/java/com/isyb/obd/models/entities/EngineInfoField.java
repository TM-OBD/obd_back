package com.isyb.obd.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "engine_info_fields")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EngineInfoField {
    @Id
    public long id;
    public String symbol;
    public String field_name;
}
