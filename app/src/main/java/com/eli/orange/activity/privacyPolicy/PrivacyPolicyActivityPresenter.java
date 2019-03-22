package com.eli.orange.activity.privacyPolicy;

import android.content.Context;
import android.net.Uri;

import com.eli.orange.R;

import androidx.browser.customtabs.CustomTabsIntent;

import static com.eli.orange.utils.Constants.PRIVACY_POLICY_URL;

public class PrivacyPolicyActivityPresenter {

    Context mCtx;
    public PrivacyPolicyActivityPresenter( Context mCtx){
        this.mCtx = mCtx;
    }


    public void openCustormChromTabs(){

        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        // and launch the desired Url with CustomTabsIntent.launchUrl()

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(mCtx.getResources().getColor(R.color.primaryColor));
        builder.setStartAnimations(mCtx,R.anim.fab_open,R.anim.rotate_clockwise);
        builder.setExitAnimations(mCtx,R.anim.fab_close, R.anim.rotate_anticlockwise);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(mCtx, Uri.parse(PRIVACY_POLICY_URL));
    }

    public interface  View{
        public void openCustom();
    }
}
