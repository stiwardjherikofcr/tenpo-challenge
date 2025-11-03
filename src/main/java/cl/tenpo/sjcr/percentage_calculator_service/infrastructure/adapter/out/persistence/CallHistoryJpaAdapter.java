package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence;

import cl.tenpo.sjcr.percentage_calculator_service.domain.model.CallHistory;
import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.entity.CallHistoryEntity;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.mapper.CallHistoryMapper;
import cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence.repository.CallHistoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class CallHistoryJpaAdapter implements CallHistoryRepositoryPort {

    private final CallHistoryJpaRepository repository;
    private final CallHistoryMapper mapper;

    public CallHistoryJpaAdapter(
            CallHistoryJpaRepository repository,
            CallHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CallHistory save(CallHistory callHistory) {
        CallHistoryEntity entity = mapper.toEntity(callHistory);
        CallHistoryEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CallHistory> findById(CallHistory.CallHistoryId id) {
        UUID entityId = id.getValue();
        return repository.findById(entityId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CallHistory> findAll(Pageable pageable) {
        Page<CallHistoryEntity> entityPage = repository.findAll(pageable);
        Page<CallHistory> domainPage = entityPage.map(mapper::toDomain);
        return SpringPageResultAdapter.of(domainPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CallHistory> findByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<CallHistoryEntity> entityPage = repository.findByTimestampBetween(
                from, to, pageable);
        Page<CallHistory> domainPage = entityPage.map(mapper::toDomain);
        return SpringPageResultAdapter.of(domainPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CallHistory> findByEndpoint(String endpoint, Pageable pageable) {
        Page<CallHistoryEntity> entityPage = repository.findByEndpointOrderByTimestampDesc(
                endpoint, pageable);
        Page<CallHistory> domainPage = entityPage.map(mapper::toDomain);
        return SpringPageResultAdapter.of(domainPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CallHistory> findSuccessfulCalls(Pageable pageable) {
        Page<CallHistoryEntity> entityPage = repository.findBySuccessTrueOrderByTimestampDesc(pageable);
        Page<CallHistory> domainPage = entityPage.map(mapper::toDomain);
        return SpringPageResultAdapter.of(domainPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<CallHistory> findFailedCalls(Pageable pageable) {
        Page<CallHistoryEntity> entityPage = repository.findBySuccessFalseOrderByTimestampDesc(pageable);
        Page<CallHistory> domainPage = entityPage.map(mapper::toDomain);
        return SpringPageResultAdapter.of(domainPage);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
}
