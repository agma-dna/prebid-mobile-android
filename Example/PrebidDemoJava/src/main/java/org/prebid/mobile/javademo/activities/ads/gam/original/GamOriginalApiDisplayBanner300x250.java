package org.prebid.mobile.javademo.activities.ads.gam.original;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

import org.json.JSONException;
import org.prebid.mobile.BannerAdUnit;
import org.prebid.mobile.BannerParameters;
import org.prebid.mobile.OnBidRequestResponseListener;
import org.prebid.mobile.Signals;
import org.prebid.mobile.addendum.AdViewUtils;
import org.prebid.mobile.addendum.PbFindSizeError;
import org.prebid.mobile.javademo.activities.BaseAdActivity;
import org.prebid.mobile.rendering.bidding.data.bid.BidResponse;
import org.prebid.mobile.rendering.models.openrtb.BidRequest;

import java.util.Collections;

import de.agmammc.agmasdk.android.AgmaSdk;

public class GamOriginalApiDisplayBanner300x250 extends BaseAdActivity {

    private static final String AD_UNIT_ID = "/21808260008/prebid_demo_app_original_api_banner_300x250_order";
    private static final String CONFIG_ID = "prebid-demo-banner-300-250";
    private static final int WIDTH = 300;
    private static final int HEIGHT = 250;

    public BannerAdUnit adUnit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createAd();
    }

    private void createAd() {
        adUnit = new BannerAdUnit(CONFIG_ID, WIDTH, HEIGHT);

        BannerParameters parameters = new BannerParameters();
        parameters.setApi(Collections.singletonList(Signals.Api.MRAID_2));
        adUnit.setBannerParameters(parameters);

        /* For GAM less than version 20 use PublisherAdView */
        final AdManagerAdView gamView = new AdManagerAdView(this);
        gamView.setAdUnitId(AD_UNIT_ID);
        gamView.setAdSizes(new AdSize(WIDTH, HEIGHT));

        getAdWrapperView().addView(gamView);

        gamView.setAdListener(createListener(gamView));

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

        adUnit.setAutoRefreshInterval(getRefreshTimeSeconds());
        adUnit.fetchDemand(builder, resultCode -> {
            /* For GAM less than version 20 use PublisherAdRequest */
            AdManagerAdRequest request = builder.build();
            gamView.loadAd(request);
        });
    }

    private AdListener createListener(AdManagerAdView gamView) {
        return new AdListener() {
            @Override
            public void onAdLoaded() {
                AdViewUtils.findPrebidCreativeSize(gamView, new AdViewUtils.PbFindSizeListener() {
                    @Override
                    public void success(
                        int width,
                        int height
                    ) {
                        gamView.setAdSizes(new AdSize(width, height));
                    }

                    @Override
                    public void failure(@NonNull PbFindSizeError error) {
                    }
                });
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
}
