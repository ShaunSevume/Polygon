import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class CovalentQuerier
{
    public static String hubWallet = "0x1653c6c02900784f4513dbf4bbf4203f77c77088";
    public static MainUI ui;

    public static void main(String[] args) {
        JFrame frame = new JFrame("MoneyHub Demo");
        ui = new MainUI();
        CovalentQuerier pls = new CovalentQuerier();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(ui.getRootPanel(), BorderLayout.CENTER);       //Display the window.
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        //Make some people
        Person[] people = new Person[5];
        people[0] = new Person("Shaun", 50, "0x70620621ACF39Ef767B69179AF053e7073c00aD9");
        people[1] = new Person("Shukri", 50, "0xcCA8b9589895bf5291874189e4845322FA604C72");
        people[2] = new Person("Daniel", 50, "0xD1c5735e3C2dcDAE1FFF74bf0A8cCAE8f1227d0c");
        people[3] = new Person("Mustafa", 50, "0x6e7daaEB312EfF2d8FD7b0d7207Def6a8844344B");
        people[4] = new Person("Saajidah", 50, "0xcCA8b9589895bf5291874189e4845322FA604C72");

        int months = 5; //Set total period
        int currentMonth = 2; //Set current month to view

        String[][] data = new String[people.length + 1][months+1]; //Table will be made according to amount of months and amount of people.

        data[0][0] = "Name";
        for(int i = 1; i <= months ; i++) {
            data[0][i] = "Month " + i; //Populate months row
        }

        for(int i = 0; i < people.length; i++){
            data[i+1][0] = people[i].name; //Populate each column with a person
            for(int j = 1; j < data[i+1].length; j++){
                if(j<currentMonth){
                    data[i+1][j] = "Hash not found"; //Populate months to be retrieved from blockchain with this.
                }
                else{
                    data[i+1][j] = "$" + people[i].committedAmount; //Populate future months with this.
                }
            }
        }

        pls.updateData(data);

        try {
            for (int i = 0; i < people.length; i++) {
                pls.SendRequest(people[i].walletAddress);

                JsonObject el = pls.SendRequest(people[i].walletAddress); //Query the get-transactions-for-address endpoint.

                if (el.get("successful").getAsBoolean() && el.get("to_address").getAsString().equals(hubWallet) && el.get("log_events").getAsJsonArray().size() >= 2 && 50E+15 == el.get("value").getAsLong()) {
                    data[i+1][currentMonth - 1] = el.get("tx_hash").getAsString();
                } else {
                    data[i+1][currentMonth - 1] = "Insufficient blocks."; //<-- Not all the time! Change?
                }

                /* DEBUG, LOOK ELSEWHERE >:(
                String hash = el.get("tx_hash").getAsString();
                Boolean success = el.get("successful").getAsBoolean();
                String from_address = el.get("from_address").getAsString();
                String to_address = el.get("to_address").getAsString();
                Long value = el.get("value").getAsLong();
                String date = el.get("block_signed_at").getAsString();
                int validated_size = el.get("log_events").getAsJsonArray().size();


                System.out.println(value);
                System.out.println(50E+15 == value);
                System.out.println(success);
                System.out.println(to_address);
                System.out.println("Size " + validated_size);
                */
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pls.updateData(data);
    }

    public void updateData(String[][] data){
        //Convert 2D array of String to 2D array of objects, with the first row chopped off to satisfy JTable requirements.
        Object[][] choppedData = new Object[data.length-1][];
        for (int i = 1, j = 0; i < data.length; i++, j++) {
            choppedData[j] = Arrays.copyOfRange(data[i], 0, data.length);
        }
        System.out.println(Arrays.deepToString(choppedData));
        ui.refreshTable(data[0], choppedData); //Refresh the table. Pass in the first row separately because JTables are weird like that... :/
    }

    public JsonObject SendRequest(String address) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        OkHttpClient client = new OkHttpClient.Builder().build();

        String url = "https://api.covalenthq.com/v1/" + System.getenv("CHAIN_ID") + "/address/" + address + "/transactions_v2/?key=" + System.getenv("API_KEY");

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();

        JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
        JsonArray items = jsonResponse.getAsJsonObject("data").getAsJsonArray("items");
        return items.get(0).getAsJsonObject();
    }
}
