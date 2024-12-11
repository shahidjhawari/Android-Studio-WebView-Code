package com.br.barayefrokht;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.AdError;

public class SplashActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity fullscreen
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {});

        // Load banner ads
        AdView topAdView = findViewById(R.id.topAdView);
        AdView bottomAdView = findViewById(R.id.bottomAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        topAdView.loadAd(adRequest);
        bottomAdView.loadAd(adRequest);

        // Load interstitial ad
        loadInterstitialAd();

        // Start the splash screen timer
        new android.os.Handler().postDelayed(this::showInterstitialAd, 6000); // 6 seconds delay
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3634516383748300/6438213623", // Replace with your Ad Unit ID
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    public void onAdFailedToLoad(AdError adError) {
                        interstitialAd = null; // Ad failed to load
                    }
                });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(SplashActivity.this);
            interstitialAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Proceed to MainActivity after the ad is dismissed
                    launchMainActivity();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // If the ad fails to show, proceed to MainActivity
                    launchMainActivity();
                }
            });
        } else {
            // If the ad is not loaded, directly proceed to MainActivity
            launchMainActivity();
        }
    }

    private void launchMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
