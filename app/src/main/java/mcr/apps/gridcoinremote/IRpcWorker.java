package mcr.apps.gridcoinremote;

import org.json.simple.JSONObject;

import java.util.List;

interface IRpcWorker {
    JSONObject invokeRPC(String method, List<String> params);
}
