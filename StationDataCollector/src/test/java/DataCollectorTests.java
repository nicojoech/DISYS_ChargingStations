import org.example.Model.ChargeInfoSDC;
import org.example.Service.ServiceSDC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DataCollectorTests {

    @Test
    public void shouldConvertJsonProperly() {

        List<ChargeInfoSDC> chargeInfoSDCList = new ArrayList<>();
        chargeInfoSDCList.add(new ChargeInfoSDC(1, "20.6", 3));
        chargeInfoSDCList.add(new ChargeInfoSDC(2, "34.2", 1));

        String expectedJson =
        "[{\"id\":" + chargeInfoSDCList.get(0).getId() + ",\"kwh\":\"" + chargeInfoSDCList.get(0).getKwh() + "\",\"customer_id\":" + chargeInfoSDCList.get(0).getCustomer_id() + "}" +
        ",{\"id\":" + chargeInfoSDCList.get(1).getId() + ",\"kwh\":\"" + chargeInfoSDCList.get(1).getKwh() + "\",\"customer_id\":" + chargeInfoSDCList.get(1).getCustomer_id() + "}]";

        ServiceSDC serviceSDC = new ServiceSDC();
        String actualJson = serviceSDC.convertToJson(chargeInfoSDCList);

        Assertions.assertEquals(expectedJson, actualJson);

    }
}
