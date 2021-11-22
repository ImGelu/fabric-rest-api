package com.hyperledger.fabcar.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    private String id;
    private String brand;
    private String model;
    private String color;
    private String owner;
}
