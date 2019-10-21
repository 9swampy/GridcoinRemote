package mcr.apps.gridcoinremote;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GridcoinRpc {
    private static final String COMMAND_GET_NEW_ADDRESS = "getnewaddress";

    private final IRpcWorker rpcWorker;

    private static final String TAG = GridcoinRpc.class.getName();

    public GridcoinRpc(IRpcWorker rpcWorker) {
        this.rpcWorker = rpcWorker;
    }

    public GridcoinRpc() {
        this(new RpcWorkerUrl(GridcoinRpcSettings.getInstance()));
    }

    public void populateBalance(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), "getbalance", null);
        Log.d(TAG, String.format("getBalance: %s", json.get("result").toString()));
        gridcoinData.BalanceString = json.get("result").toString();
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
        try {
            JSONObject json = invokeRPC(UUID.randomUUID().toString(), "getmininginfo", null);
            JSONObject json2 = (JSONObject) json.get("result");
            gridcoinData.stakingString = json2.get("staking").toString();
            gridcoinData.blocksString = json2.get("blocks").toString();
            gridcoinData.CPIDString = json2.get("CPID").toString();
            gridcoinData.GRCMagUnit = json2.get("Magnitude Unit").toString();
            double netStakeWeight = (double) json2.get("netstakeweight");
            gridcoinData.NetWeight = BigDecimal.valueOf(netStakeWeight).toPlainString();
            JSONObject json3 = (JSONObject) json2.get("difficulty");
            gridcoinData.PoRDiff = json3.get("proof-of-stake").toString();
        } catch (Exception e) {
            Log.d(TAG, String.format("Exception: %s", e.toString()));
            e.printStackTrace();
            gridcoinData.ErrorInDataGathering = true;
        }
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

    private JSONObject invokeRPC(String id, String method, List<String> params) {
        return this.rpcWorker.invokeRPC(id, method, params);
    }
}
