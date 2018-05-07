package com.anur.messageserver.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "transaction_msg")
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

    @Column(name = "msg_id")
    private Integer msgId;

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

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return creater
     */
    public String getCreater() {
        return creater;
    }

    /**
     * @param creater
     */
    public void setCreater(String creater) {
        this.creater = creater;
    }

    /**
     * @return editor
     */
    public String getEditor() {
        return editor;
    }

    /**
     * @param editor
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return edit_time
     */
    public Date getEditTime() {
        return editTime;
    }

    /**
     * @param editTime
     */
    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }

    /**
     * @return dead
     */
    public String getDead() {
        return dead;
    }

    /**
     * @param dead
     */
    public void setDead(String dead) {
        this.dead = dead;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return msg_id
     */
    public Integer getMsgId() {
        return msgId;
    }

    /**
     * @param msgId
     */
    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    /**
     * @return msg_content
     */
    public String getMsgContent() {
        return msgContent;
    }

    /**
     * @param msgContent
     */
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    /**
     * @return msg_data_type
     */
    public String getMsgDataType() {
        return msgDataType;
    }

    /**
     * @param msgDataType
     */
    public void setMsgDataType(String msgDataType) {
        this.msgDataType = msgDataType;
    }

    /**
     * @return msg_send_queue
     */
    public String getMsgSendQueue() {
        return msgSendQueue;
    }

    /**
     * @param msgSendQueue
     */
    public void setMsgSendQueue(String msgSendQueue) {
        this.msgSendQueue = msgSendQueue;
    }

    /**
     * @return msg_send_time
     */
    public Date getMsgSendTime() {
        return msgSendTime;
    }

    /**
     * @param msgSendTime
     */
    public void setMsgSendTime(Date msgSendTime) {
        this.msgSendTime = msgSendTime;
    }

    /**
     * @return field1
     */
    public String getField1() {
        return field1;
    }

    /**
     * @param field1
     */
    public void setField1(String field1) {
        this.field1 = field1;
    }

    /**
     * @return field2
     */
    public String getField2() {
        return field2;
    }

    /**
     * @param field2
     */
    public void setField2(String field2) {
        this.field2 = field2;
    }

    /**
     * @return field3
     */
    public String getField3() {
        return field3;
    }

    /**
     * @param field3
     */
    public void setField3(String field3) {
        this.field3 = field3;
    }
}