package mcr.apps.gridcoinremote;

import android.util.Base64;
import android.util.Log;

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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GridcoinRpc {
    private static final String COMMAND_GET_NEW_ADDRESS = "getnewaddress";

    private static final String TAG = GridcoinData.class.getSimpleName();

    private JSONObject invokeRPC(String id, String method, List<String> params) {

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
            System.out.println(json.toString());
            HttpPost httppost = new HttpPost("http://" + SignIn.ipFieldString + ":" + SignIn.portFieldString);
            httppost.setEntity(myEntity);
            final String basicAuth = "Basic " + Base64.encodeToString((SignIn.UsernameFieldString + ":" + SignIn.PasswordFieldString).getBytes(), Base64.NO_WRAP);
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

    public void populateBalance(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "getbalance", null);
        Log.d(TAG, String.format("getBalance: %s", json.get("result").toString()));
        gridcoinData.BalanceString =  json.get("result").toString();
    }

    public void populateNewAddress(String account, GridcoinData gridcoinData) {
        String[] params = {account};
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_NEW_ADDRESS, Arrays.asList(params));
        Log.d(TAG, String.format("getNewAddress: %s", json.get("result").toString()));
        gridcoinData.AddressString = (String) json.get("result");
    }

    public void populateAddress(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "listreceivedbyaddress", null);
        JSONArray param = (JSONArray) json.get("result");
        //Iterator i = param.iterator();
        for (int i = 0; i < param.size(); i++) {
            JSONObject item = (JSONObject) param.get(i);
            if (item.get("involvesWatchonly") != null) {
                if ((boolean) item.get("involvesWatchonly"))
                    gridcoinData.AddressString = (String) item.get("address");
            }
        }
    }

    public void populateMiningInfo(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "getmininginfo", null);
        JSONObject json2 = (JSONObject) json.get("result");
        gridcoinData.stakingString = json2.get("staking").toString();
        gridcoinData.blocksString = json2.get("blocks").toString();
        gridcoinData.CPIDString = json2.get("CPID").toString();
        gridcoinData.GRCMagUnit = json2.get("Magnitude Unit").toString();
        double NetWeightDouble = (double) json2.get("netstakeweight");
        gridcoinData.NetWeight = BigDecimal.valueOf(NetWeightDouble).toPlainString();
        JSONObject json3 = (JSONObject) json2.get("difficulty");
        gridcoinData.PoRDiff = json3.get("proof-of-stake").toString();
    }

    public void populateMyMag(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "mymagnitude", null);
        JSONArray array = (JSONArray) json.get("result");
        JSONObject json2 = (JSONObject) array.get(1);
        gridcoinData.MyMag = json2.get("Magnitude (Last Superblock)").toString();
    }

    public void populateInfo(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "getinfo", null);
        JSONObject json2 = (JSONObject) json.get("result");
        gridcoinData.ClientVersion = json2.get("version").toString();
        gridcoinData.NodeConnections = json2.get("connections").toString();
    }
}
