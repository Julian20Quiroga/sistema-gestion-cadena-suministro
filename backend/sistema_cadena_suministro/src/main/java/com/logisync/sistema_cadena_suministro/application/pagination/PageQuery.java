package com.logisync.sistema_cadena_suministro.application.pagination;

public record PageQuery(int page, int size) {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public PageQuery {
        page = Math.max(page, 0);
        size = size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size);
    }
}
