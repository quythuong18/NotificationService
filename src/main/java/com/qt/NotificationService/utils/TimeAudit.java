package com.qt.NotificationService.utils;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
public abstract class TimeAudit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

}
