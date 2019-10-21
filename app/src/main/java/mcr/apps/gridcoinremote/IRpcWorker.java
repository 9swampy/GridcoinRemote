package mcr.apps.gridcoinremote;

import org.json.simple.JSONObject;

import java.util.List;

public interface IRpcWorker {
    JSONObject invokeRPC(String id, String method, List<String> params);
}
