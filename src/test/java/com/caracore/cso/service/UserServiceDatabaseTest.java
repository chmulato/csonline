package com.caracore.cso.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Teste deprecated: use UserServiceDatabaseTestFixed
 * @deprecated Substituído por UserServiceDatabaseTestFixed para evitar conflitos de unicidade
 */
@Deprecated
public class UserServiceDatabaseTest {
    
    @Test
    @DisplayName("Stub: consulte UserServiceDatabaseTestFixed")
    public void testStub() {
        // Este teste foi movido para UserServiceDatabaseTestFixed para evitar duplicação de logins
        // e violações de constraint unique durante execução da suíte
        org.junit.jupiter.api.Assertions.assertTrue(true, "Consulte UserServiceDatabaseTestFixed");
    }
}



