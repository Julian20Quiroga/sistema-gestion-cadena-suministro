package com.logisync.sistema_cadena_suministro.domain.patterns;

import com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry.LoggingTelemetrySink;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.StandardTelemetryPublication;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.TelemetryData;
import com.logisync.sistema_cadena_suministro.domain.bridge.telemetry.UrgentAlertTelemetryPublication;
import com.logisync.sistema_cadena_suministro.domain.decorator.pricing.BaseOrderPricingCalculator;
import com.logisync.sistema_cadena_suministro.domain.decorator.pricing.ExpressDeliveryDecorator;
import com.logisync.sistema_cadena_suministro.domain.decorator.pricing.OrderPricingCalculator;
import com.logisync.sistema_cadena_suministro.domain.decorator.pricing.TaxDecorator;
import com.logisync.sistema_cadena_suministro.domain.decorator.pricing.VolumeDiscountDecorator;
import com.logisync.sistema_cadena_suministro.domain.factory.document.DocumentHeader;
import com.logisync.sistema_cadena_suministro.domain.factory.document.DocumentItem;
import com.logisync.sistema_cadena_suministro.domain.factory.document.InternalTransferDocumentFactory;
import com.logisync.sistema_cadena_suministro.domain.factory.document.PurchaseOrderDocumentFactory;
import com.logisync.sistema_cadena_suministro.domain.factory.document.SupplyChainDocumentFactory;
import com.logisync.sistema_cadena_suministro.domain.proxy.routing.CachedRouteCalculationProxy;
import com.logisync.sistema_cadena_suministro.domain.proxy.routing.DirectRouteCalculationService;
import com.logisync.sistema_cadena_suministro.domain.proxy.routing.RouteResult;
import com.logisync.sistema_cadena_suministro.domain.storage.prototype.RackLayoutTemplate;
import com.logisync.sistema_cadena_suministro.infrastructure.bridge.telemetry.StreamTelemetrySink;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructuralCreationalPatternsTest {

    @Test
    void bridgePatternDecouplesPublicationFromSink() {
        StreamTelemetrySink streamSink = new StreamTelemetrySink();
        StandardTelemetryPublication standardPub = new StandardTelemetryPublication(streamSink);

        TelemetryData data = new TelemetryData("DEV-1", new BigDecimal("4.6"), new BigDecimal("-74.0"), 50.0, LocalDateTime.now());
        standardPub.publish(data);
        assertEquals(1, streamSink.getBufferSize());

        LoggingTelemetrySink loggingSink = new LoggingTelemetrySink();
        UrgentAlertTelemetryPublication alertPub = new UrgentAlertTelemetryPublication(loggingSink);
        alertPub.publish(data);
        assertEquals("LOGGING", alertPub.getSink().getSinkName());
    }

    @Test
    void decoratorPatternCalculatesPricingWithDiscountsAndFees() {
        OrderPricingCalculator calculator = new BaseOrderPricingCalculator();

        // Base: 100.00
        BigDecimal base = new BigDecimal("100.00");
        assertEquals(base, calculator.calculateFinalPrice(base, 10));

        // With Volume Discount of 10% (threshold 10 units) -> 90.00
        calculator = new VolumeDiscountDecorator(calculator, 10, new BigDecimal("0.10"));
        assertEquals(new BigDecimal("90.00"), calculator.calculateFinalPrice(base, 10));

        // With Express Fee +15.00 -> 105.00
        calculator = new ExpressDeliveryDecorator(calculator, new BigDecimal("15.00"));
        assertEquals(new BigDecimal("105.00"), calculator.calculateFinalPrice(base, 10));

        // With Tax 19% on 105.00 -> 124.95
        calculator = new TaxDecorator(calculator, new BigDecimal("0.19"));
        assertEquals(new BigDecimal("124.95"), calculator.calculateFinalPrice(base, 10));
    }

    @Test
    void proxyPatternProvidesTransparentCaching() {
        DirectRouteCalculationService realService = new DirectRouteCalculationService();
        CachedRouteCalculationProxy proxy = new CachedRouteCalculationProxy(realService);

        RouteResult firstCall = proxy.calculateRoute("Origin", List.of("StopA", "StopB"));
        assertFalse(firstCall.fromCache());
        assertEquals(1, proxy.getCacheSize());

        RouteResult secondCall = proxy.calculateRoute("Origin", List.of("StopA", "StopB"));
        assertTrue(secondCall.fromCache());
        assertEquals(firstCall.totalDistanceKm(), secondCall.totalDistanceKm());
    }

    @Test
    void abstractFactoryCreatesConsistentDocumentFamilies() {
        SupplyChainDocumentFactory poFactory = new PurchaseOrderDocumentFactory();
        DocumentHeader poHeader = poFactory.createHeader("PO-001", 100);
        DocumentItem poItem = poFactory.createItem(5, 10, new BigDecimal("20.00"));

        assertEquals("PURCHASE_ORDER", poHeader.getDocumentType());
        assertEquals(new BigDecimal("200.00"), poItem.getSubtotal());

        SupplyChainDocumentFactory transferFactory = new InternalTransferDocumentFactory(2);
        DocumentHeader trHeader = transferFactory.createHeader("TR-001", 1);
        DocumentItem trItem = transferFactory.createItem(5, 10, BigDecimal.ZERO);

        assertEquals("INTERNAL_TRANSFER", trHeader.getDocumentType());
        assertEquals(BigDecimal.ZERO, trItem.getSubtotal());
    }

    @Test
    void prototypePatternClonesLayoutTemplatesDeeply() {
        RackLayoutTemplate template = new RackLayoutTemplate("Standard Rack", 1, 4, 50, List.of("L1", "L2", "L3", "L4"));
        RackLayoutTemplate clone = template.cloneSpace("Rack Section B", 60);

        assertNotSame(template, clone);
        assertEquals("Rack Section B", clone.getName());
        assertEquals(60, clone.getCapacityPerLevel());
        assertEquals(4, clone.getLocationNames().size());
    }
}
