package fr.umlv;

import fr.umlv.tokenization.Tokenization;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Connection connection;

    static {
        try {
            connection = DriverManager.
                    getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'target/test-classes/lightdata.sql'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tokenizationDateTime() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM data");
        Tokenization tokenization = new Tokenization();
        final ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            assertEquals(resultSet.getString(2), tokenization.tokenizeLog(resultSet.getString(1)).toString());
        }
    }
}
