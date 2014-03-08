package com.metodica.luzwifiplugin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.metodica.luzwifiplugin.fragments.eKamenoClientFoundFragment;
import com.metodica.luzwifiplugin.fragments.eKamenoNodeFoundFragment;
import com.metodica.luzwifiplugin.fragments.eKamenoNotFoundFragment;

/**
 * Created by Jacob on 2/21/14.
 */
public class LuzWiFiConfigActivity extends FragmentActivity {
    static final String classTAG = "LuzWiFiConfigActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_show);
        Log.d(classTAG, "onCreate Activity");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (isPackageInstalled("com.metodica.ekamenoclient", this))
            ft.replace(R.id.fragmentPlaceholder, new eKamenoClientFoundFragment());
        else
        if (isPackageInstalled("com.metodica.ekamenoserver", this))
            ft.replace(R.id.fragmentPlaceholder, new eKamenoNodeFoundFragment());
        else
            ft.replace(R.id.fragmentPlaceholder, new eKamenoNotFoundFragment());

        ft.commit();
    }

    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}