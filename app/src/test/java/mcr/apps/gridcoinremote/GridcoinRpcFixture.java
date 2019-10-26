package mcr.apps.gridcoinremote;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GridcoinRpcFixture {

    @Mock
    private IRpcWorker mockRpcWorker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void GivenNullResponseWhenGetMiningInfoThenErrorInDataGatheringSet() {
        GridcoinRpc sut = new GridcoinRpc(mockRpcWorker);
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);

        assertTrue(data.ErrorInDataGathering);
    }


    @Test
    public void GivenExpectedValueWhenGetBalanceThenExpectedValueReturned() {
        String expectedValue = "12345";
        JSONObject rpcResponse = new JSONObject();
        rpcResponse.put("result", expectedValue);
        when(mockRpcWorker.invokeRPC(anyString(), Mockito.any())).thenReturn(rpcResponse);
        GridcoinRpc sut = new GridcoinRpc(mockRpcWorker);
        GridcoinData data = new GridcoinData();
        sut.populateBalance(data);

        assertEquals(expectedValue, data.BalanceString);
    }
}
