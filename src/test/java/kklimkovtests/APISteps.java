package kklimkovtests;

import io.qameta.allure.Step;
import java.io.IOException;
import java.util.ArrayList;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class APISteps {
    @Step("Метод GetState")
    public static void GetState(String Host) {
        String NameMethod = "GetState";
        String URL = Host + NameMethod;
        Integer RequestCode = 0;
        //получить Состояние сервера
        RequestCode = when().post(URL).then().statusCode(200).extract().path("code");
        System.out.println("GetState RequestCode: " + RequestCode);
        assertFalse(RequestCode != 0);
    }

    @Step("Метод GetLogin")
    public static void GetLogin(String Host) {
        String NameMethod = "GetLoginData";
        String URL = Host + NameMethod;
        Integer RequestCode = 0;
        //получить Состояние сервера
        RequestCode = when().post(URL).then().statusCode(200).extract().path("code");
        System.out.println("GetState RequestCode: " + RequestCode);
        assertFalse(RequestCode != 0);
    }

    @Step("Метод Login")
    public static Integer Login(String Host) {
        String NameMethod = "Login";
        String URL = Host + NameMethod;
        Integer SessionIdVarTest = null;
        Response LoginRequest = null;
        Integer RequestCode = 0;
        //получить SessionId
        LoginRequest = when().post(URL).then().statusCode(200).extract().response();
        RequestCode = LoginRequest.path("code");
        SessionIdVarTest = LoginRequest.path("sessionId");
        System.out.println(SessionIdVarTest);
        assertFalse(RequestCode != 0);
        return SessionIdVarTest;
    }

    @Step("Метод CreateDataSubscription")
    public static Integer CreateDataSubscription(String Host, Integer SessionIdVar) {
        String NameMethod = "CreateDataSubscription";
        String URL = Host + NameMethod;
        Response LoginRequest = null;
        Integer RequestCode = 0;
        Integer SubscriptionId;
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
        return SubscriptionId;
    }

    @Step("Метод CreateDataSubscription")
    public static void CreateMonitoredDataItems(String Host, Integer SessionIdVar, String Request) {
        String NameMethod = "CreateMonitoredDataItems";
        String URL = Host + NameMethod;
        Response LoginRequest = null;
        Integer RequestCode = 0;
        String CreateMonitoredDataItems = Request
                +"\"sessionId\":"+SessionIdVar+"}";
        System.out.println("CreateMonitoredDataItems: "+ CreateMonitoredDataItems);
        LoginRequest =  given().
                        contentType("application/json").
                        body(CreateMonitoredDataItems).
                        when().
                        post(URL).
                        then().statusCode(200).extract().response();
        RequestCode = LoginRequest.path("code");
        assertFalse(RequestCode != 0);
    }

    @Step("Метод WriteData для BOOL параметра")
    public static void WriteData(String Host, Integer SessionIdVar, String Request) throws IOException, InterruptedException {
            String URL = Host + "WriteData";
            Response LoginRequest = null;
            Integer RequestCode;

            String WriteData = "{\"recs\":"+Request+",\"sessionId\":"+SessionIdVar+"}";
            System.out.println("WriteData: "+ WriteData);
            LoginRequest =  given().
                    contentType("application/json").
                    body(WriteData).
                    when().
                    post(URL).
                    then().statusCode(200).extract().response();
            RequestCode = LoginRequest.path("code");
            assertFalse(RequestCode != 0);
            Thread.sleep(1000);
    }

    @Step("Метод PublishData для BOOL параметра")
    public static void RequestResultPublishData(String Host, Integer SessionIdVar, Integer SubscriptionId, String ackSequenceNumber, String Expect) throws IOException, InterruptedException {
        String URL = Host + "PublishData";
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
        Boolean Result = AnswerPublishData.get(1).toString().contains("value="+Expect);
        assertTrue(Result);
        Thread.sleep(1000);
    }
}


