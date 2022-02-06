package fr.umlv;

import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testH2DB() throws SQLException {
        Connection conn = DriverManager.
                getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'target/test-classes/lightdata.sql'");
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Test");
        final ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        assertAll(
                () -> assertEquals(0, resultSet.getInt(1)),
                () -> assertEquals("a", resultSet.getString(2))
        );
        conn.nativeSQL("TRUNCATE TABLE");
        conn.close();
    }
}
