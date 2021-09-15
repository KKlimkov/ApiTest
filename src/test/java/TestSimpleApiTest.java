import org.junit.jupiter.api.*;
import org.openqa.selenium.json.Json;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;
import java.util.Optional;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestSimpleApiTest {

    public static Integer SessionIdVar;
    public static Integer SubscriptionId = 0;
    public static String IdText = "";
    public static String IdPump = "";
    public static String host = "http://"+System.getProperty("HostIP")+"/Methods/";
    //public static String host = "http://"+"127.0.0.1:8043"+"/Methods/";

    public static String  RequestResultWriteData(String Flag) throws IOException {
        String URL = host + "WriteData";
        Response LoginRequest = null;
        Integer RequestCode;
        String Result = "";

        String WriteData = "{\"recs\":[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+IdPump+
                ",\"path\":\"PumpControl_ID_36236.RunButton\",\"operation\":\"move\",\"type\":\"BOOL\"," +
                "\"usesCounts\":1,\"value\":"+Flag+"}],\"sessionId\":"+SessionIdVar+"}";

        System.out.println("WriteData: "+ WriteData);

        LoginRequest =  given().
                        contentType("application/json").
                        body(WriteData).
                        when().
                        post(URL).
                        then().statusCode(200).extract().response();

        RequestCode = LoginRequest.path("code");
        assertFalse(RequestCode != 0);
        Result = "Test passed";
        return (Result);
    }

    public static Boolean RequestResultPublishData(String ackSequenceNumber) throws IOException {
        String URL = host + "PublishData";
        Response LoginRequest = null;
        Integer RequestCode = 0;
        ArrayList AnswerPublishData;
        String ReadPublish0 = "{\"ackSequenceNumber\":"+ackSequenceNumber+",\"subscriptionId\":"+SubscriptionId+",\"sessionId\":"+SessionIdVar+"}";
        System.out.println("ReadPublish0: "+ ReadPublish0);
        LoginRequest =  given().
                        contentType("application/json").
                        body(ReadPublish0).
                        when().
                        post(URL).
                        then().statusCode(200).extract().response();
        RequestCode = LoginRequest.path("code");
        AnswerPublishData = LoginRequest.path("recs");
        assertFalse(RequestCode != 0);
        Boolean Result = AnswerPublishData.get(1).toString().contains("value=true");
        System.out.println(Result);
        return Result;
    }


    @BeforeAll

    static void GetIdFromIDETest() throws IOException {
        //Получаем id параметра из файла из предыдущего теста

        String fileName = "C:\\Users\\kiril\\Desktop\\Autotests\\IDE\\Data.csv";
        Optional<String> line = Files.lines(Paths.get(fileName)).findFirst();
        String[] words = line.get().split(",");
        IdText = words[0];
        IdPump = words[1];
    }


    @DisplayName("GetState")
    @Test
    @Tag("GetState")
    @Order(1)
    public void GetState() {
        String NameMethod = "GetState";
        String URL = host + NameMethod;
        Integer RequestCode = 0;

        //получить Состояние сервера
        RequestCode = when().post(URL).then().statusCode(200).extract().path("code");
        System.out.println("GetState RequestCode: " + RequestCode);
        assertFalse(RequestCode != 0);
    }

    @DisplayName("GetLoginData")
    @Test
    @Tag("GetLoginData")
    @Order(2)
    public void GetLoginData() {
        String NameMethod = "GetLoginData";
        String URL = host + NameMethod;
        Integer RequestCode = 0;

        //получить Состояние сервера
        RequestCode = when().post(URL).then().statusCode(200).extract().path("code");
        System.out.println("GetState RequestCode: " + RequestCode);
        assertFalse(RequestCode != 0);
    }

    @DisplayName("Login")
    @Test
    @Tag("Login")
    @Order(3)
    public void Login() {
        String NameMethod = "Login";
        String URL = host + NameMethod;
        Response LoginRequest = null;
        Integer RequestCode = 0;

        //получить SessionId
        LoginRequest = when().post(URL).then().statusCode(200).extract().response();
        RequestCode = LoginRequest.path("code");
        SessionIdVar = LoginRequest.path("sessionId");
        System.out.println(SessionIdVar);
        assertFalse(RequestCode != 0);
    }

    @DisplayName("CreateDataSubscription")
    @Test
    @Tag("CreateDataSubscription")
    @Order(4)
    public void CreateDataSubscription() {
        String NameMethod = "CreateDataSubscription";
        String URL = host + NameMethod;
        Response LoginRequest = null;
        Integer RequestCode = 0;

        //http://127.0.0.1:8043/Methods/CreateDataSubscription
        String CreateDataSubscription = "{\"requestedPublishingInterval\":100,\"requestedLifetimeInterval\":120000,\"maxNotificationsPerPublish\":0,\"maxSize\":1000,\"sessionId\":"+SessionIdVar+"}";
        System.out.println("CreateDataSubscription: "+ CreateDataSubscription);

        LoginRequest =
                given().
                        contentType("application/json").
                        body(CreateDataSubscription).
                        when().
                        post(URL).
                        then().statusCode(200).extract().response();

        SubscriptionId = LoginRequest.path("subscriptionId");
        RequestCode = LoginRequest.path("code");
        System.out.println("subscriptionId: " + SubscriptionId);
        assertFalse(RequestCode != 0);
    }

    @DisplayName("CreateMonitoredDataItems")
    @Test
    @Tag("CreateMonitoredDataItems")
    @Order(5)
    public void CreateMonitoredDataItems() {
        String NameMethod = "CreateMonitoredDataItems";
        String URL = host + NameMethod;
        Response LoginRequest = null;
        Integer RequestCode = 0;

        // http://127.0.0.1:8043/Methods/CreateMonitoredDataItems
        String CreateMonitoredDataItems = "{\"subscriptionId\":"+SubscriptionId+"," +
                "\"items\":[{\"taskId\":0,\"itemId\":"
                +IdPump+",\"path\":\"Sostoyaniya_Rezhim\",\"type\":\"BaseObjects.Mode\",\"dataSourceId\":\"MPLCDataSource\"," +
                "\"clientHandle\":1,\"usesCounts\":1},{\"taskId\":0,\"itemId\":"+IdPump+",\"path\":\"UprVihod\"," +
                "\"type\":\"BOOL\",\"dataSourceId\":\"MPLCDataSource\",\"clientHandle\":2,\"usesCounts\":1}," +
                "{\"taskId\":0,\"itemId\":"+IdText+",\"path\":\"\",\"type\":\"BOOL\",\"dataSourceId\":\"MPLCDataSource\"" +
                ",\"clientHandle\":3,\"usesCounts\":1}],"
                +"\"sessionId\":"+SessionIdVar+"}";
        System.out.println("CreateMonitoredDataItems: "+ CreateMonitoredDataItems);

        LoginRequest =
                given().
                        contentType("application/json").
                        body(CreateMonitoredDataItems).
                        when().
                        post(URL).
                        then().statusCode(200).extract().response();

        RequestCode = LoginRequest.path("code");
        assertFalse(RequestCode != 0);
    }

    @DisplayName("WriteDataTrue")
    @Test
    @Tag("WriteDataTrue")
    @Order(6)
    public void WriteData() throws IOException, InterruptedException {
        RequestResultWriteData("true");
        Thread.sleep(1000);
    }

    @DisplayName("PublishData")
    @Test
    @Tag("PublishData")
    @Order(7)
    public void PublishData() throws IOException, InterruptedException {
        RequestResultPublishData("0");
        Thread.sleep(1000);
    }

    @DisplayName("WriteDataFalse")
    @Test
    @Tag("WriteDataFalse")
    @Order(8)
    public void WriteData1() throws IOException, InterruptedException {
        RequestResultWriteData("false");
        Thread.sleep(1000);
    }

    @DisplayName("PublishData1")
    @Test
    @Tag("PublishData")
    @Order(9)
    public void PublishData1() throws IOException, InterruptedException {
        RequestResultPublishData("1");
        Thread.sleep(1000);
    }

    @DisplayName("WriteDataTrue1")
    @Test
    @Tag("WriteDataTrue")
    @Order(10)
    public void WriteData2() throws IOException, InterruptedException {
        RequestResultWriteData("true");
        Thread.sleep(1000);
    }

    @DisplayName("PublishData2")
    @Test
    @Tag("PublishData")
    @Order(11)
    public void PublishData2() throws IOException {
        RequestResultPublishData("2");
    }

}
