package com.metodica.luzwifiplugin.async;

import android.os.AsyncTask;

import com.metodica.luzwifiplugin.LuzWiFiPlugin;

/**
 * Created by Jacob on 9/9/13.
 */
public class AsyncAlarmWorker extends AsyncTemplateWorker {
    private static final String classTAG = "LuzWiFi Async Task";
    LuzWiFiPlugin luzwifiService;
    boolean isFinished = false;
    long beat = 500;

    public AsyncAlarmWorker(LuzWiFiPlugin ctx, long newBeat) {
        luzwifiService = ctx;
        beat = newBeat;
    }

    public void setBeat(long newBeat) {
        beat = newBeat;
    }

    public void stopAsyncTask() {
        isFinished = true;
    }

    @Override
    protected void onPreExecute() {
        // This method works in UI Thread
    }

    @Override
    protected Boolean doInBackground(String... empty) {
        // This works in parallel
        int cycles = 10;
        byte[] red = new byte[]{(byte)0x1b, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x55};
        byte[] off = new byte[]{(byte)0x1b, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x55};
        byte[] rgbOn = new byte[]{(byte)0x01, (byte)0x55};
        luzwifiService.sendLuzWifiMessage(rgbOn, 1);

        try {
            while (cycles > 0) {
                luzwifiService.sendLuzWifiMessage(red, 1);
                Thread.sleep(beat/2);
                if (isFinished) break;
                luzwifiService.sendLuzWifiMessage(off, 1);
                Thread.sleep(beat/2);
                if (isFinished) break;
                cycles--;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {

    }
}
