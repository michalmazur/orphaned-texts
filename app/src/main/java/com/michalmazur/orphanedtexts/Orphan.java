package com.michalmazur.orphanedtexts;

import android.database.Cursor;

import java.util.Date;

public class Orphan {
    private int id;
    private int referenceNumber;
    private int count;
    private int sequence;
    private int destinationPort;
    private String address;
    private String contactName;
    private String messageBody;
    private Date date;

    public Orphan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName == null ? "" : contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(Cursor c, int index) {
        if (index >= 0) {
            this.id = c.getInt(index);
        }
    }

    public void setDate(Cursor c, int index) {
        if (index >= 0) {
            this.date = new Date(c.getLong(index));
        }
    }

    public void setReferenceNumber(Cursor c, int index) {
        if (index >= 0) {
            this.referenceNumber = c.getInt(index);
        }
    }

    public void setCount(Cursor c, int index) {
        if (index >= 0) {
            this.count = c.getInt(index);
        }
    }

    public void setSequence(Cursor c, int index) {
        if (index >= 0) {
            this.sequence = c.getInt(index);
        }
    }

    public void setDestinationPort(Cursor c, int index) {
        if (index >= 0) {
            this.destinationPort = c.getInt(index);
        }
    }

    public void setAddress(Cursor c, int index) {
        if (index >= 0) {
            this.address = c.getString(index);
        }
    }

}