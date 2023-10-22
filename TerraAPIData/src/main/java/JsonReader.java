import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import java.sql.*;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

    private final String url = "jdbc:postgres://dpg-ckq5ptg1hnes738cp9p0-a.oregon-postgres.render.com/terradb_gkjk";
    private final String user = "user";
    private final String password = "822j9f3rcJRpJ0xVpKxFufOHUuh65y07";
    private static String id = "Andry";

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }


    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public static void main(String[] args) throws IOException, JSONException {
        String sql = "SELECT payload_url FROM terra_data_payloads WHERE user_id=" + id;

        JsonReader reader = new JsonReader();
        Connection conn = reader.connect();
        try {
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery(sql);
            while(res.next()) {
                String currUrl = res.getString(1);
                JSONObject json = readJsonFromUrl(currUrl);
                JSONObject json2 = json.getJSONObject("sleep_durations_data");
                System.out.println(json2.get("sleep_efficiency"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}