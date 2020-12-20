package com.asiczen.organization.services.organization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "errortable")
public class ErrorTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String actualData;
    private LocalDateTime timeStamp;
    private boolean status;
    private String failureReason;

    public ErrorTable(String actualData, LocalDateTime timeStamp, boolean status, String failureReason) {
        this.actualData = actualData;
        this.timeStamp = timeStamp;
        this.status = status;
        this.failureReason = failureReason;
    }
}
