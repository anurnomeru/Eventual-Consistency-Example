package com.anur.provider.model;

import lombok.Data;

import javax.persistence.*;

@Table(name = "provider_order")
@Data
public class ProviderOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
}