package com.buildstore.audit.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorType;
    private Long actorId;
    private String action;
    private String subjectType;
    private Long subjectId;
    private String requestId;
    private String correlationId;
    
    @Column(columnDefinition = "TEXT")
    private String beforeState;
    
    @Column(columnDefinition = "TEXT")
    private String afterState;
    
    private String reason;
    private LocalDateTime occurredAt;
    private String ipAddress;
    private String userAgent;
}
