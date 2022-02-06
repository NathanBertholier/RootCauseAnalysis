package fr.umlv;

import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PerfTest {

    @Test
    public void perf() throws SQLException {
        Connection conn = DriverManager.
                getConnection("jdbc:h2:mem:test;INIT=RUNSCRIPT FROM 'target/test-classes/heavydata.sql'");
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Test");
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {

        }
    }
}
