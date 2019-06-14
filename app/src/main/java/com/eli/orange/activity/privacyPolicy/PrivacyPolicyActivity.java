package com.eli.orange.activity.privacyPolicy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.net.Uri;
import android.os.Bundle;

import com.eli.orange.R;


public class PrivacyPolicyActivity extends AppCompatActivity implements PrivacyPolicyActivityPresenter.View {
    private PrivacyPolicyActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        presenter = new PrivacyPolicyActivityPresenter(this);
        openCustom();

    }
    @Override
    public void openCustom() {
        presenter.openCustormChromTabs();
    }
}
