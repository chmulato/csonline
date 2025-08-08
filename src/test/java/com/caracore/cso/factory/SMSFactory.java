package com.caracore.cso.factory;

import com.caracore.cso.entity.SMS;
import com.caracore.cso.entity.Delivery;
import java.util.UUID;

public class SMSFactory {
    private static long idSeq = System.currentTimeMillis();
    public static SMS createUniqueSMS(Delivery delivery) {
        SMS sms = new SMS();
        sms.setId(++idSeq);
        sms.setDelivery(delivery);
        sms.setPiece(1);
        sms.setType("S");
        sms.setMobileTo("99999" + UUID.randomUUID().toString().substring(0, 4));
        sms.setMobileFrom("88888" + UUID.randomUUID().toString().substring(0, 4));
        sms.setMessage("Teste-" + UUID.randomUUID());
        sms.setDatetime("2025-07-27T10:00:00");
        return sms;
    }
}


