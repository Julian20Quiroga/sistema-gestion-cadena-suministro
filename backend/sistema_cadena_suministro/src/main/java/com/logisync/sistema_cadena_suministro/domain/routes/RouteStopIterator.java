package com.logisync.sistema_cadena_suministro.domain.routes;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterador para el recorrido secuencial ordenado de paradas de una ruta vehicular optimizada.
 * Patrón aplicado: Iterator (permite recorrer los waypoints o paradas de entrega de una ruta
 * de forma desacoplada sin exponer la estructura interna de la colección).
 */
public class RouteStopIterator implements Iterator<RouteStop> {

    private final List<RouteStop> stops;
    private int position = 0;

    public RouteStopIterator(List<RouteStop> stops) {
        this.stops = stops != null ? stops : List.of();
    }

    /**
     * Comprueba si quedan paradas pendientes por visitar en la secuencia de la ruta.
     */
    @Override
    public boolean hasNext() {
        return position < stops.size();
    }

    /**
     * Retorna la siguiente parada de la ruta y avanza la posición del cursor.
     */
    @Override
    public RouteStop next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more route stops available");
        }
        return stops.get(position++);
    }
}
