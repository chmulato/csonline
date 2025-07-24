package br.com.mulato.cso.dao.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.mulato.cso.dry.DBConnection;
import br.com.mulato.cso.exception.DAOException;
import br.com.mulato.cso.model.LoginVO;
import br.com.mulato.cso.model.UserVO;
import br.com.mulato.cso.utils.InitProperties;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UserDAOImpl - Data Access Object para Usuário")
class UserDAOImplTest {

    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
    }

    @Test
    @DisplayName("Deve lançar DAOException se id for nulo")
    void testFindThrowsExceptionIfIdNull() {
        assertThrows(DAOException.class, () -> userDAO.find(null, false));
    }

    @Test
    @DisplayName("Deve lançar DAOException se id for <= 0")
    void testFindThrowsExceptionIfIdZeroOrNegative() {
        assertThrows(DAOException.class, () -> userDAO.find(0, false));
        assertThrows(DAOException.class, () -> userDAO.find(-1, false));
    }

    @Test
    @DisplayName("Deve retornar UserVO válido se encontrado no banco")
    void testFindReturnsUserVO() throws Exception {
        try (MockedStatic<DBConnection> dbMock = mockStatic(DBConnection.class);
             MockedStatic<InitProperties> propMock = mockStatic(InitProperties.class)) {
            dbMock.when(DBConnection::getConnectionDB).thenReturn(connection);
            propMock.when(InitProperties::getViewSql).thenReturn(false);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.findColumn("ID")).thenReturn(1);
            when(resultSet.getInt(1)).thenReturn(123);
            when(resultSet.findColumn("ROLE")).thenReturn(2);
            when(resultSet.getString(2)).thenReturn("admin");
            when(resultSet.findColumn("NAME")).thenReturn(3);
            when(resultSet.getString(3)).thenReturn("Usuário Teste");
            when(resultSet.findColumn("LOGIN")).thenReturn(4);
            when(resultSet.getString(4)).thenReturn("login");
            when(resultSet.findColumn("EMAIL")).thenReturn(5);
            when(resultSet.getString(5)).thenReturn("email@teste.com");
            when(resultSet.findColumn("EMAIL2")).thenReturn(6);
            when(resultSet.getString(6)).thenReturn("email2@teste.com");
            when(resultSet.findColumn("ADDRESS")).thenReturn(7);
            when(resultSet.getString(7)).thenReturn("Rua Teste");
            when(resultSet.findColumn("MOBILE")).thenReturn(8);
            when(resultSet.getString(8)).thenReturn("11999999999");

            UserVO user = userDAO.find(123, false);
            assertNotNull(user);
            assertEquals(123, user.getId());
            assertEquals("admin", user.getRole());
            assertEquals("Usuário Teste", user.getName());
            assertEquals("login", user.getLogin().getLogin());
            assertEquals("email@teste.com", user.getEmail());
            assertEquals("email2@teste.com", user.getEmail2());
            assertEquals("Rua Teste", user.getAddress());
            assertEquals("11999999999", user.getMobile());
        }
    }

    @Test
    @DisplayName("Deve retornar null se usuário não encontrado")
    void testFindReturnsNullIfNotFound() throws Exception {
        try (MockedStatic<DBConnection> dbMock = mockStatic(DBConnection.class);
             MockedStatic<InitProperties> propMock = mockStatic(InitProperties.class)) {
            dbMock.when(DBConnection::getConnectionDB).thenReturn(connection);
            propMock.when(InitProperties::getViewSql).thenReturn(false);
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);
            UserVO user = userDAO.find(123, false);
            assertNull(user);
        }
    }
}
