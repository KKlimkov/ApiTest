import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

public class TestSimpleApiTest {

    Integer SessionIdVar = 0;

    @Test
    public void getSecondPosts() throws IOException {

     //Получаем id параметра из фала из предыдущего теста
     String dataId = "";
        FileReader fr = new FileReader("C:\\\\Users\\\\User\\\\Desktop\\\\Work\\\\AutomationJavaTests\\\\Editor\\\\id\\\\id.txt");
        Scanner scan = new Scanner(fr);
        int i = 1;
        while (scan.hasNextLine()) {
            dataId = scan.nextLine();
            //System.out.println(scan.nextLine());
            i++;
        }
        fr.close();
        System.out.println(dataId);

        //получить SessionId
        SessionIdVar = when().post("http://127.0.0.1:8043/Methods/Login").then().statusCode(200).extract().path("sessionId");
        System.out.println("SessionId: " + SessionIdVar);

        //http://127.0.0.1:8043/Methods/CreateDataSubscription
        String CreateDataSubscription = "{\"requestedPublishingInterval\":100,\"requestedLifetimeInterval\":120000,\"maxNotificationsPerPublish\":0,\"maxSize\":1000,\"sessionId\":"+SessionIdVar+"}";
        System.out.println("CreateDataSubscription: "+ CreateDataSubscription);

        Integer SubscriptionId =
                given().
                contentType("application/json").
                body(CreateDataSubscription).
                when().
                post("http://127.0.0.1:8043/Methods/CreateDataSubscription").
                then().statusCode(200).extract().path("subscriptionId");
        System.out.println("subscriptionId: " + SubscriptionId);

        // http://127.0.0.1:8043/Methods/CreateMonitoredDataItems
        String CreateMonitoredDataItems = "{\"subscriptionId\":"+SubscriptionId+",\"items\":[{\"taskId\":0,\"itemId\":"+dataId+",\"path\":\"\",\"type\":\"BOOL\",\"dataSourceId\":\"MPLCDataSource\",\"clientHandle\":1,\"usesCounts\":1}],\"sessionId\":"+SessionIdVar+"}";
        System.out.println("CreateMonitoredDataItems: "+ CreateMonitoredDataItems);

        given().contentType("application/json").
                body(CreateMonitoredDataItems).
                when().
                post("http://127.0.0.1:8043/Methods/CreateMonitoredDataItems").
                then().
                statusCode(200)
        ;

        String GetTrue = "{\"recs\":[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+dataId+",\"path\":\"\",\"operation\":\"move\",\"type\":\"BOOL\",\"usesCounts\":1,\"value\":true}],\"sessionId\":"+SessionIdVar+"}";
        String GetFalse = "{\"recs\":[{\"taskId\":0,\"dataSourceId\":\"MPLCDataSource\",\"itemId\":"+dataId+",\"path\":\"\",\"operation\":\"move\",\"type\":\"BOOL\",\"usesCounts\":1,\"value\":false}],\"sessionId\":"+SessionIdVar+"}";
        String ReadPublish1 = "{\"ackSequenceNumber\":1,\"subscriptionId\":"+SubscriptionId+",\"sessionId\":"+SessionIdVar+"}";
        String ReadPublish2 = "{\"ackSequenceNumber\":2,\"subscriptionId\":"+SubscriptionId+",\"sessionId\":"+SessionIdVar+"}";

        //http://127.0.0.1:8043/Methods/WriteData
                given().contentType("application/json").
                body(GetTrue).
                when().
                post("http://127.0.0.1:8043/Methods/WriteData").
                then().
                statusCode(200)
        ;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

                given().contentType("application/json").
                body(GetFalse).
                when().
                post("http://127.0.0.1:8043/Methods/WriteData").
                then().
                statusCode(200)
        ;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        given().contentType("application/json").
                body(GetTrue).
                when().
                post("http://127.0.0.1:8043/Methods/WriteData").
                then().
                statusCode(200)
        ;

        ArrayList<String> namesList =
                given().contentType("application/json").
                        body(ReadPublish1).
                        when().
                        post("http://127.0.0.1:8043/Methods/PublishData").
                        then().statusCode(200).extract().path("recs")

                ;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList AnswerPublishData =
                given().contentType("application/json").
                        body(ReadPublish1).
                        when().
                        post("http://127.0.0.1:8043/Methods/PublishData").
                        then().statusCode(200).extract().path("recs")

                ;

        Boolean Result = AnswerPublishData.get(0).toString().contains("value=true");
        System.out.println(Result);

    }
}
