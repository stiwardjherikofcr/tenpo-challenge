package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "call_history", indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_endpoint", columnList = "endpoint")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String requestParams;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private String response;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private boolean success;

    @Version
    private Long version;
}
