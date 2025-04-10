package com.qt.NotificationService.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.qt.NotificationService.utils.TimeAudit;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Document("notification_message")
public class NotificationMessage extends TimeAudit {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String fromUsername;
    private String toUsername;

    private String message;

    private NotificationTypes type;
    private NotiMetadata notiMetadata;

    private Boolean isRead;
    @JsonIgnore
    private Boolean isPushed;
}
