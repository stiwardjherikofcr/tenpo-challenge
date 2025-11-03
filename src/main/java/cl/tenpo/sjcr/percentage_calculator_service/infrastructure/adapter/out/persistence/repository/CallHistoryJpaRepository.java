package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.repository;

import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.entity.CallHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface CallHistoryJpaRepository extends JpaRepository<CallHistoryEntity, UUID> {

    @Query("SELECT c FROM CallHistoryEntity c WHERE c.timestamp >= :from AND c.timestamp < :to ORDER BY c.timestamp DESC")
    Page<CallHistoryEntity> findByTimestampBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    Page<CallHistoryEntity> findByEndpointOrderByTimestampDesc(String endpoint, Pageable pageable);

    Page<CallHistoryEntity> findBySuccessTrueOrderByTimestampDesc(Pageable pageable);

    Page<CallHistoryEntity> findBySuccessFalseOrderByTimestampDesc(Pageable pageable);
}
