package org.prebid.mobile.javademo.activities.ads.gam.original;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;

import org.json.JSONException;
import org.prebid.mobile.AdUnit;
import org.prebid.mobile.InterstitialAdUnit;
import org.prebid.mobile.OnBidRequestResponseListener;
import org.prebid.mobile.javademo.activities.BaseAdActivity;
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse;
import org.prebid.mobile.rendering.models.openrtb.BidRequest;

import java.net.URI;

import de.agmammc.agmasdk.android.AgmaSdk;

public class GamOriginalApiDisplayInterstitial extends BaseAdActivity {

    private static final String AD_UNIT_ID = "/21808260008/prebid-demo-app-original-api-display-interstitial";
    private static final String CONFIG_ID = "prebid-demo-display-interstitial-320-480";

    private AdUnit adUnit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAd();
    }

    private void createAd() {
        adUnit = new InterstitialAdUnit(CONFIG_ID);
        adUnit.setAutoRefreshInterval(getRefreshTimeSeconds());

        final AdManagerAdRequest.Builder builder = new AdManagerAdRequest.Builder();

        // Setup Agma SDK Listener
        adUnit.onBidRequestResponseListener = new OnBidRequestResponseListener() {
            @Override
            public void onBidRequest(@Nullable BidRequest request) {
                Log.d("onBidRequest", request.toString());
                try {
                    AgmaSdk.getInstance(getApplicationContext()).didReceivePrebidRequest(request.getJsonObject());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onBidResponse(@Nullable BidResponse response) {
                Log.d("onBidResponse", response.toString());
            }
        };

        adUnit.fetchDemand(builder, resultCode -> {
            AdManagerAdRequest request = builder.build();
            AdManagerInterstitialAd.load(this, AD_UNIT_ID, request, createListener());
        });
    }

    private AdManagerInterstitialAdLoadCallback createListener() {
        return new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialManager) {
                interstitialManager.show(GamOriginalApiDisplayInterstitial.this);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e("GamInterstitial", loadAdError.getMessage());
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adUnit != null) {
            adUnit.stopAutoRefresh();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
