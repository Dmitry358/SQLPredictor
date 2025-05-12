package com.zucchetti.sitepainter.SQLPredictor;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyString;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataBaseConnecterTest {
    // errore tipo di dato nella tabella not double
    // url, nome utente, pwd errati
    // nome db??, tabella, campi inesistente

    /*
    @Test
    public void testGetTrainingData_withoutRealDB() throws Exception {
        // Mocks
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);

        // Mock behavior
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);

        when(mockMetaData.getColumnCount()).thenReturn(2);
        when(mockMetaData.getColumnName(1)).thenReturn("field1");
        when(mockMetaData.getColumnName(2)).thenReturn("class");

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getDouble("field1")).thenReturn(1.5);
        when(mockResultSet.getDouble("class")).thenReturn(0.0);

        // Mock static DriverManager
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            DataBaseConnecter connector = new DataBaseConnecter("url", "user", "pass");
            double[][] result = connector.getTrainingData("table", new String[]{"field1"}, "class");

            assertEquals(1, result.length);
            assertEquals(2, result[0].length);
            assertEquals(1.5, result[0][0]);
            assertEquals(0.0, result[0][1]);
        }
    }
    */

    @Test
    void testGetTrainingDataWithMockito() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        ResultSetMetaData mockMetaData = mock(ResultSetMetaData.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(3);
        when(mockMetaData.getColumnName(1)).thenReturn("feature1");
        when(mockMetaData.getColumnName(2)).thenReturn("feature2");
        when(mockMetaData.getColumnName(3)).thenReturn("class");

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDouble("feature1")).thenReturn(1.0, 3.0);
        when(mockResultSet.getDouble("feature2")).thenReturn(2.0, 4.0);
        when(mockResultSet.getDouble("class")).thenReturn(0.0, 1.0);

        DataBaseConnecter db = new DataBaseConnecter("url", "user", "pass");
        //db.setTestConnection(mockConnection);

        String[] fields = {"feature1", "feature2"};
        String classField = "class";

        double[][] result = db.getTrainingData("my_table", fields, classField);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(3, result[0].length);
        assertEquals(1.0, result[0][0]);
        assertEquals(4.0, result[1][1]);
        assertEquals(1.0, result[1][2]);
    }

    @Test
    void testConstructorWithCorrectValues() throws Exception {
        DataBaseConnecter connector = new DataBaseConnecter(
                "jdbc:mysql://localhost:3306/mydb",
                "admin",
                "pwd"
        );

        Field urlField = DataBaseConnecter.class.getDeclaredField("dataBaseURL");
        Field userField = DataBaseConnecter.class.getDeclaredField("username");
        Field passField = DataBaseConnecter.class.getDeclaredField("password");

        urlField.setAccessible(true);
        userField.setAccessible(true);
        passField.setAccessible(true);

        assertEquals("jdbc:mysql://localhost:3306/mydb", urlField.get(connector));
        assertEquals("admin", userField.get(connector));
        assertEquals("pwd", passField.get(connector));
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