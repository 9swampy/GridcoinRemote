package mcr.apps.gridcoinremote;

import android.util.Base64;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class RpcWorkerUrl implements IRpcWorker {

    private final GridcoinRpcSettings gridcoinRpcSettings;
    private static final String TAG = RpcWorkerUrl.class.getName();
    private final ReentrantLock lock = new ReentrantLock();

    public RpcWorkerUrl(GridcoinRpcSettings gridcoinRpcSettings) {
        this.gridcoinRpcSettings = gridcoinRpcSettings;
    }

    public JSONObject invokeRPC(String method, List<String> params) {

        JSONObject responseJsonObj = null;
        this.lock.lock();
        try {
            JSONObject json = new JSONObject();
            json.put("method", method);
            if (null != params) {
                JSONArray array = new JSONArray();
                array.addAll(params);
                json.put("params", params);
            }

            HttpURLConnection connection = null;
            OutputStream os = null;
            try {
                URL url = new URL("http://" + this.gridcoinRpcSettings.ipFieldString + ":" + this.gridcoinRpcSettings.portFieldString);
                connection = (HttpURLConnection) url.openConnection();
                String encoded = Base64.encodeToString((this.gridcoinRpcSettings.UsernameFieldString + ":" + this.gridcoinRpcSettings.PasswordFieldString).getBytes(), Base64.NO_WRAP);  //Java 8
                connection.setRequestProperty("Authorization", "Basic " + encoded);

                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Log.d(TAG, "executing request to " + url);

                os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));

                writer.write(json.toJSONString());

                writer.flush();
                writer.close();
                os.close();
                int responseCode = connection.getResponseCode();

                Log.d(TAG, "----------------------------------------");
                Log.d(TAG, String.format("%s %s", responseCode, HttpStatus.getByCode(responseCode).toString()));
                StringBuilder responseBuilder = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }

                String response = responseBuilder.toString();
                Log.d(TAG, String.format("Response content length: %s", response.length()));
                JSONParser parser = new JSONParser();
                responseJsonObj = (JSONObject) parser.parse(response);

            } catch (Exception e) {
                Log.d(TAG, "invokeRPC Error: ", e);
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ignored) {
                    }
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }

            Log.d(TAG, "----------------------------------------");
        } finally {
            this.lock.unlock();
        }

        return responseJsonObj;
    }
}
