package com.br.barayefrokht;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class SplashActivity extends AppCompatActivity {

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

        // Start the splash screen timer
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(6000); // 3 seconds
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        splashThread.start();
    }
}
