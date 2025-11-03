package cl.tenpo.sjcr.percentage_calculator_service.infrastructure.adapter.out.persistence;

import cl.tenpo.sjcr.percentage_calculator_service.domain.port.out.CallHistoryRepositoryPort;
import org.springframework.data.domain.Page;

import java.util.List;

public class SpringPageResultAdapter<T> implements CallHistoryRepositoryPort.PageResult<T> {

    private final Page<T> page;

    public SpringPageResultAdapter(Page<T> page) {
        this.page = page;
    }

    @Override
    public List<T> getContent() {
        return page.getContent();
    }

    @Override
    public int getTotalPages() {
        return page.getTotalPages();
    }

    @Override
    public long getTotalElements() {
        return page.getTotalElements();
    }

    @Override
    public int getPageNumber() {
        return page.getNumber();
    }

    @Override
    public int getPageSize() {
        return page.getSize();
    }

    @Override
    public boolean hasNext() {
        return page.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    @Override
    public boolean isFirst() {
        return page.isFirst();
    }

    @Override
    public boolean isLast() {
        return page.isLast();
    }

    public static <T> SpringPageResultAdapter<T> of(Page<T> page) {
        return new SpringPageResultAdapter<>(page);
    }
}
