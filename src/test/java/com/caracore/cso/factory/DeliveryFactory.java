package com.caracore.cso.factory;

import com.caracore.cso.entity.Delivery;
import java.util.UUID;

public class DeliveryFactory {
    public static Delivery createUniqueDelivery() {
        Delivery delivery = new Delivery();
        delivery.setStart("Start-" + UUID.randomUUID());
        delivery.setDestination("Dest-" + UUID.randomUUID());
        delivery.setContact("Contato-" + UUID.randomUUID());
        delivery.setDescription("Mensagem-" + UUID.randomUUID());
        delivery.setVolume("1");
        delivery.setWeight("1");
        delivery.setKm("1");
        delivery.setAdditionalCost(0.0);
        delivery.setCost(0.0);
        delivery.setReceived(true);
        delivery.setCompleted(false);
        return delivery;
    }
}


