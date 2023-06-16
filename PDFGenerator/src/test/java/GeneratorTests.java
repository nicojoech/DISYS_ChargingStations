import org.example.Models.ChargeInfo;
import org.example.Models.Customer;
import org.example.Services.GeneratorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GeneratorTests {

    private static final DecimalFormat dfZero = new DecimalFormat("0.00");

    @Test
    public void shouldReturnSpecificCustomer() {

        Customer expectedCustomer = new Customer();
        expectedCustomer.setId(1);
        expectedCustomer.setFirst_name("Luisa");
        expectedCustomer.setLast_name("Colon");

        GeneratorService generatorService = new GeneratorService();
        Customer actualCustomer = generatorService.getCustomerFromDB(expectedCustomer.getId());

        Assertions.assertEquals(expectedCustomer.getFirst_name(), actualCustomer.getFirst_name());
        Assertions.assertEquals(expectedCustomer.getLast_name(), actualCustomer.getLast_name());

    }

    @Test
    public void shouldReturnTotalAmount(){
        List<ChargeInfo> chargeInfoList = new ArrayList<>();
        chargeInfoList.add(new ChargeInfo(1, "20.5", 2));
        chargeInfoList.add(new ChargeInfo(2, "41.5", 2));
        chargeInfoList.add(new ChargeInfo(3, "23.0", 3));

        float expectedCalculation = 20.5f * 0.48f + 41.5f * 0.48f + 23 * 0.48f;
        String expectedTotalAmount = dfZero.format(expectedCalculation);

        GeneratorService generatorService = new GeneratorService();
        String actualTotalAmount = generatorService.getTotalAmount(chargeInfoList);

        Assertions.assertEquals(expectedTotalAmount, actualTotalAmount);


    }
}
