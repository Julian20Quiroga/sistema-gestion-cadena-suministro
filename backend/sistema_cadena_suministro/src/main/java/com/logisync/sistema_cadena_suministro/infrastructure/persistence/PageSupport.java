package com.logisync.sistema_cadena_suministro.infrastructure.persistence;

import com.logisync.sistema_cadena_suministro.application.pagination.PageQuery;
import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public final class PageSupport {

    private PageSupport() {
    }

    public static Pageable pageable(PageQuery query, Sort sort) {
        return PageRequest.of(query.page(), query.size(), sort);
    }

    public static Pageable pageable(PageQuery query) {
        return pageable(query, Sort.by("id"));
    }

    public static <E, D> PageResult<D> toResult(Page<E> page, Function<E, D> mapper) {
        return new PageResult<>(page.getContent().stream().map(mapper).toList(), page.getNumber(), page.getSize(),
                page.getTotalElements());
    }
}
