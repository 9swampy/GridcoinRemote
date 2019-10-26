package mcr.apps.gridcoinremote;

import android.os.Build;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import androidx.annotation.RequiresApi;

class GridcoinRpc {
    @SuppressWarnings("SpellCheckingInspection")
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
        //noinspection SpellCheckingInspection
        JSONObject json = invokeRPC("getbalance", null);
        if (null != json) {
            gridcoinData.BalanceString  = json.getOrDefault("result",gridcoinData.BalanceString).toString();
        }

        Log.d(TAG, String.format("getBalance: %s", gridcoinData.BalanceString));
    }

    public void populateNewAddress(String account, GridcoinData gridcoinData) {
        String[] params = {account};
        JSONObject json = invokeRPC(COMMAND_GET_NEW_ADDRESS, Arrays.asList(params));
        if (null != json) {
            gridcoinData.AddressString = (String) json.get("result");
        }

        Log.d(TAG, String.format("getNewAddress: %s", gridcoinData.AddressString));
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void populateAddress(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC("listreceivedbyaddress", null);
        if (null != json) {
            JSONArray param = (JSONArray) json.get("result");
            if (null != param) {
                for (int i = 0; i < param.size(); i++) {
                    JSONObject item = (JSONObject) param.get(i);
                    if (null != item) {
                        if (item.get("involvesWatchonly") != null) {
                            if ((boolean) item.get("involvesWatchonly"))
                                gridcoinData.AddressString = (String) item.get("address");
                        }
                    }
                }
            }
        }
    }

    public void populatePrimaryAddress(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC("listaddressgroupings", null);
        JSONArray jsonResult = (JSONArray) json.get("result");
                if (null != jsonResult) {
            JSONArray addresses = (JSONArray) jsonResult.get(0);
            if (null != addresses) {
                double maxBalance = 0;
                String primary = "Undetermined";
                for (int i = 0; i < addresses.size(); i++) {
                    JSONArray address = (JSONArray) addresses.get(i);
                    if (null != address) {
                        System.out.println(String.format("Address: %s", address.get(0)));
                        System.out.println(String.format("Amount: %s", address.get(1)));
                        double amount = (double) address.get(1);
                        if (amount > maxBalance) {
                            maxBalance = amount;
                            primary = (String) address.get(0);
                        }
                    }
                }

                gridcoinData.AddressString = primary;
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void populateMiningInfo(GridcoinData gridcoinData) {
        try {
            JSONObject json = invokeRPC("getmininginfo", null);
            if (null == json) {
                gridcoinData.ErrorInDataGathering = true;
            }
            else
            {
                JSONObject jsonResult = (JSONObject) json.get("result");
                if (null != jsonResult) {
                    Function<String, Object> jsonExtractor = (s) -> jsonResult.get(s);
                    UpdateValue(jsonExtractor, "staking", s -> gridcoinData.stakingString = s.toString());
                    UpdateValue(jsonExtractor, "blocks", s -> gridcoinData.blocksString = s.toString());
                    UpdateValue(jsonExtractor, "CPID", s -> gridcoinData.CPIDString = s.toString());
                    UpdateValue(jsonExtractor, "Magnitude Unit", s -> gridcoinData.GRCMagUnit = s.toString());
                    UpdateValue(jsonExtractor, "netstakeweight", s -> gridcoinData.NetWeight = BigDecimal.valueOf(Double.parseDouble(s.toString())).toPlainString());

                    JSONObject json3 = (JSONObject) jsonResult.get("difficulty");
                    if (null != json3) {
                        UpdateValue((s) -> json3.get(s), "proof-of-stake", s -> gridcoinData.PoRDiff = s.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, String.format("Exception: %s", e.toString()));
            e.printStackTrace();
            gridcoinData.ErrorInDataGathering = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void UpdateValue(Function<String, Object> extractor, String key, Consumer<Object> fieldSetter) {
        Object value = extractor.apply(key);
        if (null != value) {
            fieldSetter.accept(value.toString());
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void populateMyMag(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC("mymagnitude", null);
        if (null != json) {
            JSONArray array = (JSONArray) json.get("result");
            if (null != array) {
                JSONObject json2 = (JSONObject) array.get(1);
                if (null != json2) {
                    gridcoinData.MyMag = json2.getOrDefault("Magnitude (Last Superblock)", gridcoinData.MyMag).toString();
                }
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    public void populateInfo(GridcoinData gridcoinData) {
        JSONObject json = invokeRPC("getinfo", null);
        if (null != json) {
            JSONObject json2 = (JSONObject) json.get("result");
            if (null != json2) {
                gridcoinData.ClientVersion = json2.getOrDefault("version", gridcoinData.ClientVersion).toString();
                gridcoinData.NodeConnections = json2.getOrDefault("connections",gridcoinData.NodeConnections).toString();
            }
        }
    }

    private JSONObject invokeRPC(String method, List<String> params) {
        return this.rpcWorker.invokeRPC(method, params);
    }
}
