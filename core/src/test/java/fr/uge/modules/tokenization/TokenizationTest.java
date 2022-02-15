package fr.uge.modules.tokenization;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.*;

class TokenizationTest {

    /* private static Connection connection;
    private Tokenization tokenization = new Tokenization();
   
    static {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'src/test/resources/lightdata.sql'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    @Test
    void tokenization() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rawlog");
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            //assertEquals(tokenization.tokenizeLog(resultSet.getString(1)).toString(), resultSet.getString(2));
        }
    }

    @Test
    void shouldThrowNullPointerException(){
        assertThrows(NullPointerException.class, () -> {
            //tokenization.tokenizeLog(null);
        });
    }
     */
}
