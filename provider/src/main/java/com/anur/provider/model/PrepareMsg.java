package com.anur.provider.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "prepare_msg")
public class PrepareMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String msg;

    @Column(name = "routing_key")
    private String routingKey;

    private String exchange;

    @Column(name = "param_map")
    private String paramMap;

    private Date createTime;
}