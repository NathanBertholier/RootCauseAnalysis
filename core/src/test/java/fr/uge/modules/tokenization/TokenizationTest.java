package fr.uge.modules.tokenization;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class TokenizationTest {
    private static Connection connection;
    private Tokenization tokenization = new Tokenization();

    static {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'src/test/resources/lightdata.sql'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    //@Test
    void tokenization() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rawlog");
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            assertEquals(resultSet.getString(2), tokenization.tokenizeLog(1,resultSet.getString(1)).tokens
                    .stream()
                    .map(token -> token.value)
                    .toList()
                    .toString());
        }
    }

}
