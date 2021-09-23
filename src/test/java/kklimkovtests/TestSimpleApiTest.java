package kklimkovtests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Owner("KKlimkov")
@Layer("API")
@Feature("BaseObjects")

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSimpleApiTest {

    public static Integer SessionIdVar;
    public static Integer SubscriptionId = 0;
    public static String IdText = "";
    public static String IdPump = "";
    //public static String host = "http://"+System.getProperty("HostIP")+"/Methods/";
    public static String host = "http://"+"127.0.0.1:8043"+"/Methods/";

    @BeforeAll

    static void GetIdFromIDETest() throws IOException {
        //Получаем id параметра из файла из предыдущего теста
        String fileName = "C:\\Users\\Public\\Autotests\\Data.csv";
        Optional<String> line = Files.lines(Paths.get(fileName)).findFirst();
        String[] words = line.get().split(",");
        IdText = words[0];
        IdPump = words[1];
    }


    @DisplayName("Тест на включение насоса только с помощью API")
    @Test
    @Story("Base.Objects Pump")
    @Tags({@Tag("API")})
    @Order(1)
    public void BaseObjectPumpApiTest() throws InterruptedException, IOException {
      APISteps.GetState(host);
      APISteps.GetLogin(host);
      SessionIdVar = APISteps.Login(host);
      SubscriptionId = APISteps.CreateDataSubscription(host,SessionIdVar);
      APISteps.CreateMonitoredDataItems(host,SessionIdVar,"{\"subscriptionId\":"+SubscriptionId+"," +
                "\"items\":[{\"taskId\":0,\"itemId\":"
                +IdPump+",\"path\":\"Sostoyaniya_Rezhim\",\"type\":\"BaseObjects.Mode\",\"dataSourceId\":\"MPLCDataSource\"," +
                "\"clientHandle\":1,\"usesCounts\":1},{\"taskId\":0,\"itemId\":"+IdPump+",\"path\":\"UprVihod\"," +
                "\"type\":\"BOOL\",\"dataSourceId\":\"MPLCDataSource\",\"clientHandle\":2,\"usesCounts\":1}," +
                "{\"taskId\":0,\"itemId\":"+IdText+",\"path\":\"\",\"type\":\"BOOL\",\"dataSourceId\":\"MPLCDataSource\"" +
                ",\"clientHandle\":3,\"usesCounts\":1}],");
      APISteps.WriteData(host,SessionIdVar,"[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+IdPump+
                ",\"path\":\"PumpControl_ID_36236.RunButton\",\"operation\":\"move\",\"type\":\"BOOL\"," +
                "\"usesCounts\":1,\"value\":true}]");
      APISteps.RequestResultPublishData(host,SessionIdVar,SubscriptionId,"0","true");

      APISteps.WriteData(host,SessionIdVar,"[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+IdPump+
                ",\"path\":\"PumpControl_ID_36236.RunButton\",\"operation\":\"move\",\"type\":\"BOOL\"," +
                "\"usesCounts\":1,\"value\":false}]");
      APISteps.RequestResultPublishData(host,SessionIdVar,SubscriptionId,"1","false");

      APISteps.WriteData(host,SessionIdVar,"[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+IdPump+
                ",\"path\":\"PumpControl_ID_36236.RunButton\",\"operation\":\"move\",\"type\":\"BOOL\"," +
                "\"usesCounts\":1,\"value\":true}]");
      APISteps.RequestResultPublishData(host,SessionIdVar,SubscriptionId,"2","true");
    }
}
