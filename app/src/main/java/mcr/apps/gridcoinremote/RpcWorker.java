package mcr.apps.gridcoinremote;

import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

@SuppressWarnings("deprecation")
public class RpcWorker implements IRpcWorker {

    private final GridcoinRpcSettings gridcoinRpcSettings;

    public RpcWorker(GridcoinRpcSettings gridcoinRpcSettings)
    {
        this.gridcoinRpcSettings = gridcoinRpcSettings;
    }

    public JSONObject invokeRPC(String id, String method, List<String> params) {

        CloseableHttpClient httpclient = HttpClientBuilder.create().build();

        JSONObject json = new JSONObject();
        json.put("method", method);
        if (null != params) {
            JSONArray array = new JSONArray();
            array.addAll(params);
            json.put("params", params);
        }
        JSONObject responseJsonObj = null;
        try {
            StringEntity myEntity = new StringEntity(json.toJSONString());
            System.out.println(json);
            HttpPost httppost = new HttpPost("http://" + this.gridcoinRpcSettings.ipFieldString + ":" + this.gridcoinRpcSettings.portFieldString);
            httppost.setEntity(myEntity);
            final String basicAuth = "Basic " + Base64.encodeToString((this.gridcoinRpcSettings.UsernameFieldString + ":" + this.gridcoinRpcSettings.PasswordFieldString).getBytes(), Base64.NO_WRAP);
            httppost.setHeader("Authorization", basicAuth);
            System.out.println("executing request" + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
            }
            JSONParser parser = new JSONParser();
            responseJsonObj = (JSONObject) parser.parse(EntityUtils.toString(entity));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return responseJsonObj;
    }
}
