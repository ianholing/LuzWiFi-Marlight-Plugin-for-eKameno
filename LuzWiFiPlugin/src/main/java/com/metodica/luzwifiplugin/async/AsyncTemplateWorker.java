package com.metodica.luzwifiplugin.async;

import android.os.AsyncTask;

import com.metodica.luzwifiplugin.LuzWiFiPlugin;

/**
 * Created by Jacob on 9/9/13.
 */
public abstract class AsyncTemplateWorker extends AsyncTask<String, Void, Boolean> {
    private static final String classTAG = "LuzWiFi Async Task";

    abstract public void setBeat(long newBeat);
    abstract public void stopAsyncTask();
}
