package br.com.mulato.cso.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.mulato.cso.service.BusinessService;
import br.com.mulato.cso.model.BusinessVO;
import br.com.mulato.cso.model.PriceListVO;
import br.com.mulato.cso.model.PriceVO;
import br.com.mulato.cso.exception.WebException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.mulato.cso.service.impl.BusinessServiceImpl;
import br.com.mulato.cso.dry.FactoryDAO;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.exception.DAOException;

import org.mockito.MockedStatic;

public class BusinessServiceTest {

    private BusinessServiceImpl businessService;

    @BeforeEach
    public void setUp() {
        businessService = new BusinessServiceImpl();
    }

    @Test
    public void testListAllBusinessSuccess() throws WebException {
        List<BusinessVO> mockList = Collections.singletonList(new BusinessVO());
        try (MockedStatic<FactoryDAO> factoryMock = Mockito.mockStatic(FactoryDAO.class)) {
            var factory = mock(br.com.mulato.cso.dry.FactoryDAO.class);
            var businessDAOMock = mock(br.com.mulato.cso.dao.BusinessDAO.class);
            factoryMock.when(FactoryDAO::getInstancia).thenReturn(factory);
            when(factory.getBusinessDAO()).thenReturn(businessDAOMock);
            when(businessDAOMock.listAll()).thenReturn(mockList);
            List<BusinessVO> result = businessService.listAllBusiness();
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Test
    public void testSaveBusinessNullThrows() {
        assertThrows(WebException.class, () -> businessService.save(null));
    }

    @Test
    public void testSaveBusinessMissingNameThrows() {
        BusinessVO vo = new BusinessVO();
        assertThrows(WebException.class, () -> businessService.save(vo));
    }

    @Test
    public void testSaveBusinessSuccessInsert() throws DAOException, WebException {
        BusinessVO vo = new BusinessVO();
        vo.setName("Empresa Teste");
        vo.setRole("BUSINESS");
        LoginVO login = new LoginVO();
        login.setLogin("user");
        login.setPassword("123");
        login.setRepeat("123");
        vo.setLogin(login);
        vo.setEmail("email@teste.com");

        try (MockedStatic<FactoryDAO> factoryMock = Mockito.mockStatic(FactoryDAO.class)) {
            var factory = mock(br.com.mulato.cso.dry.FactoryDAO.class);
            var businessDAOMock = mock(br.com.mulato.cso.dao.BusinessDAO.class);
            factoryMock.when(FactoryDAO::getInstancia).thenReturn(factory);
            when(factory.getBusinessDAO()).thenReturn(businessDAOMock);
            doNothing().when(businessDAOMock).insert(vo);
            businessService.save(vo);
            verify(businessDAOMock, times(1)).insert(vo);
        }
    }

    @Test
    public void testFindBusinessInvalidIdThrows() {
        assertThrows(WebException.class, () -> businessService.find(null));
        assertThrows(WebException.class, () -> businessService.find(0));
    }

    @Test
    public void testFindBusinessSuccess() throws DAOException, WebException {
        BusinessVO vo = new BusinessVO();
        try (MockedStatic<FactoryDAO> factoryMock = Mockito.mockStatic(FactoryDAO.class)) {
            var factory = mock(br.com.mulato.cso.dry.FactoryDAO.class);
            var businessDAOMock = mock(br.com.mulato.cso.dao.BusinessDAO.class);
            factoryMock.when(FactoryDAO::getInstancia).thenReturn(factory);
            when(factory.getBusinessDAO()).thenReturn(businessDAOMock);
            when(businessDAOMock.find(1)).thenReturn(vo);
            BusinessVO result = businessService.find(1);
            assertNotNull(result);
        }
    }

    @Test
    public void testDeleteBusinessInvalidIdThrows() {
        assertThrows(WebException.class, () -> businessService.delete(null));
        assertThrows(WebException.class, () -> businessService.delete(0));
    }

    @Test
    public void testDeleteBusinessSuccess() throws DAOException, WebException {
        try (MockedStatic<FactoryDAO> factoryMock = Mockito.mockStatic(FactoryDAO.class)) {
            var factory = mock(br.com.mulato.cso.dry.FactoryDAO.class);
            var businessDAOMock = mock(br.com.mulato.cso.dao.BusinessDAO.class);
            factoryMock.when(FactoryDAO::getInstancia).thenReturn(factory);
            when(factory.getBusinessDAO()).thenReturn(businessDAOMock);
            doNothing().when(businessDAOMock).delete(1);
            businessService.delete(1);
            verify(businessDAOMock, times(1)).delete(1);
        }
    }

    // Adicione outros métodos conforme necessário
}
