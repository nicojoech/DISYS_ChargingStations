import org.example.Models.Customer;
import org.example.Models.Database;
import org.example.Services.GeneratorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCustomerTest {

    @Test
    public void shouldReturnSpecificCustomer() throws SQLException {
        Database database = mock(Database.class);
        ResultSet resultSet = mock(ResultSet.class);
        Connection connection = mock(Connection.class);

        when(resultSet.getString("first_name")).thenReturn("Luisa");
        when(resultSet.getString("last_name")).thenReturn("Colon");

        GeneratorService generatorService = new GeneratorService();
        Customer customer = generatorService.getCustomerFromDB(1);

        Assertions.assertEquals("Luisa", customer.getFirst_name());
        Assertions.assertEquals("Colon", customer.getLast_name());


    }
}
