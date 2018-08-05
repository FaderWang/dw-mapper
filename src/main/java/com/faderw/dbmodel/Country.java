package com.faderw.dbmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by FaderW on 2018/8/5
 */
@Table(name = "country")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Country {

    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    private String name;
}
