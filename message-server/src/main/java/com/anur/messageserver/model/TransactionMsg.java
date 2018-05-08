package com.anur.messageserver.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "transaction_msg")
@Data
public class TransactionMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private Integer version;

    private String creater;

    private String editor;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "edit_time")
    private Date editTime;

    private String dead;

    private String status;

    @Column(name = "msg_content")
    private String msgContent;

    @Column(name = "msg_data_type")
    private String msgDataType;

    @Column(name = "msg_send_queue")
    private String msgSendQueue;

    @Column(name = "msg_send_time")
    private Date msgSendTime;

    private String field1;

    private String field2;

    private String field3;
}