package com.revolut.testtask.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;

    @Column(name = "FROMID")
    private Integer fromId;

    @Column(name = "TOID")
    private Integer toId;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @Column(name = "DATE")
    @CreationTimestamp
    private Date date;

    public Transaction(Integer fromId, Integer toId, Integer amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    public Transaction() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fromId, that.fromId) &&
                Objects.equals(toId, that.toId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, fromId, toId, amount, date);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
