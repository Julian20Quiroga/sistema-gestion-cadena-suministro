package com.logisync.sistema_cadena_suministro.infrastructure.controllers.dtos.responses;

import com.logisync.sistema_cadena_suministro.application.pagination.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <S, T> PageResponse<T> from(PageResult<S> result, Function<? super S, ? extends T> mapper) {
        PageResult<T> mapped = result.<T>map(mapper);
        return new PageResponse<>(mapped.items(), mapped.page(), mapped.size(), mapped.totalElements(),
                mapped.totalPages());
    }
}
