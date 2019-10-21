package mcr.apps.gridcoinremote;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetMiningInfoFixture {

    @Mock
    private IRpcWorker mockRpcWorker;

    private GridcoinRpc sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        JSONObject rpcResponse = new JSONObject();
        JSONObject result = new JSONObject();
        result.put("netstakeweight", 1.339703077355589E10);
        result.put("Magnitude Unit", 0.2);
        result.put("staking", true);
        result.put("CPID", "f681786ba2c10c87c502328dae680cab");
        result.put("blocks", 1743738);
        JSONObject difficulty = new JSONObject();
        difficulty.put("proof-of-stake", 23.65180712530539);
        result.put("difficulty", difficulty);
        rpcResponse.put("result", result);
        rpcResponse.put("id", null);
        rpcResponse.put("error", null);

        when(mockRpcWorker.invokeRPC(anyString(), anyString(), Mockito.any())).thenReturn(rpcResponse);

        sut = new GridcoinRpc(mockRpcWorker);
    }

    @Test
    public void GivenGetMiningInfoWhenGetNetWeightThenExpectedValueReturned() throws Exception {
        //String expected = "{\"result\":{\"kernel-diff-best\":1.755612043008384,\"MiningInfo 1\":\"Eligible for Research Rewards\",\"NeuralPopularity\":-1,\"stakeweight\":{\"legacy\":2723.09993358,\"valuesum\":2723.09993358,\"maximum\":74490,\"combined\":217846,\"minimum\":40111},\"netstakeweight\":1.339703077355589E10,\"side-staking\":{\"side-staking-enabled\":false},\"mining-error\":\"\",\"Magnitude Unit\":0.2,\"PopularNeuralHash\":\"\",\"staking\":true,\"mining-kernels-found\":2,\"kernel-diff-sum\":7.993505273314053E-5,\"time-to-stake_days\":88.6754050925926,\"CPID\":\"f681786ba2c10c87c502328dae680cab\",\"MiningInfo 8\":\"\",\"BoincRewardPending\":0,\"MiningInfo 7\":\"\",\"MiningInfo 6\":\"\",\"MiningInfo 5\":\"\",\"pooledtx\":0,\"MiningInfo 2\":\"Poll: Gridcoin_Marketing_and_Engagement_Initiative\",\"mining-created\":2,\"blocks\":1743738,\"expectedtime\":7661555,\"MyNeuralHash\":\"c14fd394de3e75d0b9bcf737a466ac7f\",\"netstakingGRCvalue\":1.674628846694486E8,\"mining-version\":10,\"difficulty\":{\"proof-of-stake\":23.65180712530539,\"last-search-interval\":1571667216},\"mining-accepted\":2,\"stake-splitting\":{\"stake-splitting-enabled\":false},\"testnet\":false,\"errors\":\"\"},\"id\":null,\"error\":null}";
        //String expected = "{\"result\":{\"staking\":true,\"difficulty\":{\"proof-of-stake\":23.65180712530539},\"netstakeweight\":1.339703077355589E10,\"blocks\":1743738,\"CPID\":\"f681786ba2c10c87c502328dae680cab\",\"Magnitude Unit\":0.2},\"id\":null,\"error\":null}";
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("13397030773.55589", data.NetWeight);
    }

    @Test
    public void GivenGetMiningInfoWhenGetStakingThenExpectedValueReturned() throws Exception {
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("true", data.stakingString);
    }

    @Test
    public void GivenGetMiningInfoWhenGetPoRDiffThenExpectedValueReturned() throws Exception {
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("23.65180712530539", data.PoRDiff);
    }

    @Test
    public void GivenGetMiningInfoWhenGetBlocksThenExpectedValueReturned() throws Exception {
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("1743738", data.blocksString);
    }

    @Test
    public void GivenGetMiningInfoWhenGetCpidThenExpectedValueReturned() throws Exception {
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("f681786ba2c10c87c502328dae680cab", data.CPIDString);
    }

    @Test
    public void GivenGetMiningInfoWhenGetGrcMagUnitThenExpectedValueReturned() throws Exception {
        GridcoinData data = new GridcoinData();
        sut.populateMiningInfo(data);
        assertEquals("0.2", data.GRCMagUnit);
    }
}
