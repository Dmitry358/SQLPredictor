package com.zucchetti.sitepainter.SQLPredictor;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

class DataBaseConnecterTest {
    // errore tipo di dato nella tabella not double
    // url, nome utente, pwd errati
    // nome db??, tabella, campi inesistente

    @Test
    public void testGetTrainingDataWithCorrectInput() {
        double[][] expected = {
          {75.5, 180.2, 30.0}
        };

        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockQueryResult = mock(ResultSet.class);
        ResultSetMetaData mockQueryResultMetaData = mock(ResultSetMetaData.class);

        try {
            MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockQueryResult);
            when(mockQueryResult.getMetaData()).thenReturn(mockQueryResultMetaData);

            when(mockQueryResultMetaData.getColumnCount()).thenReturn(3);
            when(mockQueryResultMetaData.getColumnName(1)).thenReturn("weight");
            when(mockQueryResultMetaData.getColumnName(2)).thenReturn("height");
            when(mockQueryResultMetaData.getColumnName(3)).thenReturn("age");

            when(mockQueryResult.next()).thenReturn(true, false);
            for (int i = 0; i < expected.length; i++) {
                final int index = i; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                when(mockQueryResult.getDouble("weight")).thenReturn(expected[index][0]);
                when(mockQueryResult.getDouble("height")).thenReturn(expected[index][1]);
                when(mockQueryResult.getDouble("age")).thenReturn(expected[index][2]);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        DataBaseConnecter dbConnecter = new DataBaseConnecter("dataBaseURL", "username", "password");

        double[][] actual = dbConnecter.getTrainingData("persons", new String[] {"weight", "height"}, "age");

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i]);
        }
    }

    @Test
    public void testGetTrainingDataWithNonExistentFieldName() {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockQueryResult = mock(ResultSet.class);
        ResultSetMetaData mockQueryResultMetaData = mock(ResultSetMetaData.class);

        try {
            MockedStatic<DriverManager> mockDriverManager = mockStatic(DriverManager.class);
            mockDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString())).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Table does not exist"));
            /*
            when(mockQueryResult.getMetaData()).thenThrow(new SQLException("Table does not exist"));
            when(mockQueryResult.getMetaData()).thenReturn(mockQueryResultMetaData);
            */
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        DataBaseConnecter dbConnecter = new DataBaseConnecter("dataBaseURL", "username", "password");

        double[][] actual = dbConnecter.getTrainingData("persons", new String[] {"weight", "height"}, "age");
        assertNotNull(actual);
    }


    @Test
    void testGetDataBaseURL(){
        DataBaseConnecter connector = new DataBaseConnecter(
                "jdbc:mysql://localhost:3306/mydb",
                "admin",
                "pwd"
        );

        String resultDataBaseName = connector.getDataBaseURL();
        String expectedDataBaseName = "jdbc:mysql://localhost:3306/mydb";

        assertEquals(resultDataBaseName, expectedDataBaseName);
    }
    @Test
    void testGetUsername(){
        DataBaseConnecter connector = new DataBaseConnecter(
                "jdbc:mysql://localhost:3306/mydb",
                "admin",
                "pwd"
        );

        String resultUsername = connector.getUsername();
        String expectedUsername = "admin";

        assertEquals(resultUsername, expectedUsername);
    }
    @Test
    void testGetPassword(){
        DataBaseConnecter connector = new DataBaseConnecter(
                "jdbc:mysql://localhost:3306/mydb",
                "admin",
                "pwd"
        );

        String resultPassword = connector.getPassword();
        String expectedPassword = "pwd";

        assertEquals(resultPassword, expectedPassword);
    }
}