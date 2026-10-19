package com.logisync.sistema_cadena_suministro.domain.routes;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator: Sequentially traverses optimized route stops.
 */
public class RouteStopIterator implements Iterator<RouteStop> {

    private final List<RouteStop> stops;
    private int position = 0;

    public RouteStopIterator(List<RouteStop> stops) {
        this.stops = stops != null ? stops : List.of();
    }

    @Override
    public boolean hasNext() {
        return position < stops.size();
    }

    @Override
    public RouteStop next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more route stops available");
        }
        return stops.get(position++);
    }
}
