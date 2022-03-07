package fr.uge.modules.tokenization;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    

    @Test
    void tokenization() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rawlog");
        final ResultSet resultSet = preparedStatement.executeQuery();
        final List<String> line1 = List.of("/wp-includes/css/dist/block-library/style.min.css.gzip", "Hit", "82.255.169.186", "400", "https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/");
        final List<String> line2 = List.of("/wp-includes/css/dist/block-library/style.min.css.gzip", "Hit", "82.255.169.186", "404", "https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/");
        final List<String> line3 = List.of("/wp-includes/css/dist/block-library/style.min.css.gzip", "Hit", "127.168.0.191", "200", "https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/");
        final List<String> line4 = List.of("/wp-includes/css/dist/block-library/style.min.css.gzip", "Hit", "127.168.0.12", "301", "https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/");
        final List<String> line5 = List.of("/wp-includes/css/dist/block-library/style.min.css.gzip", "Hit", "127.168.0.125", "200", "https://www.centreon.com/blog/tuto-deployer-sa-supervision-rapidement-avec-les-plugin-packs/");
        resultSet.next();
        assertTrue(tokenization.tokenizeLog(1, resultSet.getString(1)).tokens
                .stream()
                .map(token -> token.value)
                .toList().containsAll(line1));
        resultSet.next();
        assertTrue(tokenization.tokenizeLog(1, resultSet.getString(1)).tokens
                .stream()
                .map(token -> token.value)
                .toList().containsAll(line2));
        resultSet.next();
        assertTrue(tokenization.tokenizeLog(1, resultSet.getString(1)).tokens
                .stream()
                .map(token -> token.value)
                .toList().containsAll(line3));
        resultSet.next();
        assertTrue(tokenization.tokenizeLog(1, resultSet.getString(1)).tokens
                .stream()
                .map(token -> token.value)
                .toList().containsAll(line4));
        resultSet.next();
        assertTrue(tokenization.tokenizeLog(1, resultSet.getString(1)).tokens
                .stream()
                .map(token -> token.value)
                .toList().containsAll(line5));

    }

}
