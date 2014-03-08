package com.metodica.luzwifiplugin.async;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.metodica.luzwifiplugin.LuzWiFiPlugin;

/**
 * Created by Jacob on 9/9/13.
 */
public class AsyncPartyWorker extends AsyncTemplateWorker {
    private static final String classTAG = "LuzWiFi Async Task";
    LuzWiFiPlugin luzwifiService;
    private int bufferSize = 0;
    boolean isFinished = false;
    long beat = 500;

    // AUDIO
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    AudioRecord recorder;


    public AsyncPartyWorker(LuzWiFiPlugin ctx, long newBeat) {
        luzwifiService = ctx;
        beat = newBeat;

        bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
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
//        recorder.startRecording();
    }

    @Override
    protected Boolean doInBackground(String... empty) {
        // This works in parallel
        byte data[] = new byte[bufferSize];
        int read = 0;

        byte[] conversion;
        byte[] temp = new byte[]{(byte)0x1b, (byte)0x00, (byte)0xff, (byte)0x00, (byte)0x55};
        byte[] rgbOn = new byte[]{(byte)0x1b, (byte)0xff, (byte)0x00, (byte)0x00, (byte)0x55};
        luzwifiService.sendLuzWifiMessage(rgbOn, 1);

        Log.d(classTAG, "Buffer Size: " + bufferSize);

        try {
            recorder.startRecording();
            while (true) {
                read = recorder.read(data, 0, bufferSize);
                if(AudioRecord.ERROR_INVALID_OPERATION != read) {
                    conversion = audioToByteArray(data);
                    if (conversion.length >= 3)
                        temp = new byte[]{
                                (byte)0x1b,
                                conversion[0],
                                conversion[1],
                                conversion[2],
                                (byte)0x55};

                    luzwifiService.sendLocalLuzWifiMessage(temp, 1, false);
                    Log.d(classTAG, "Send Color: " + conversion[0] + ", " + conversion[1] + ", " + conversion[2]);


                } else
                    throw new IllegalStateException(
                        "read() returned AudioRecord.ERROR_INVALID_OPERATION");

                if (isFinished) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private byte[] audioToByteArray(byte[] data) {
        byte[] bytes = new byte[3];
        int dataDivider = 16;
        int multiplier = 3;
        // RED If something goes wrong
        if (data != null && data.length < 2)
            return new byte[]{(byte)0xff, (byte)0x00, (byte)0x00};

//        int lastInt = (data[0]<<8) + (data[1] & 0xff);
        int newInt;
//        int cross0Axis = 0;
//        long sumLevel = 0;
//        int peaks = 0;
//        boolean direction = true;
        int R = 0; int Rbase = 5;
        int G = 0; int Gbase = 5;
        int B = 0; int Bbase = 5;
        int midLowLimit = 30;
        int highMidLimit = 20;
        int threshold = 800;
        int peakBetween0Cross = 0;
        int samplesBetween0Cross = 0;
        for (int i = 2; i < data.length; i+=2) {
            newInt = (data[i+1]<<8) + (data[i] & 0xff);
//            newInt = (data[i]<<8) + (data[i+1] & 0xff);
            if (Math.abs(newInt) > peakBetween0Cross) peakBetween0Cross = Math.abs(newInt);
//            Log.d(classTAG, "Sample Value: " + newInt);

// Big Endian
//            if (data[i]>>7 != data[i-2]>>7) {

// Little Endian
            if (data[i+1]>>7 != data[i-1]>>7) {
//                Log.d(classTAG, "Zero Cross Space: " + (data[i]>>7));
//                Log.d(classTAG, "Zero Cross Space: " + samplesBetween0Cross);
                if (peakBetween0Cross > threshold) {
                    if (samplesBetween0Cross < highMidLimit) B++;
                    else if (samplesBetween0Cross < midLowLimit) G++;
                    else R++;
                }

//                cross0Axis++;
                samplesBetween0Cross = 0;
                peakBetween0Cross = 0;
            }
            samplesBetween0Cross++;

//            //Detect PEAKS
//            if ((lastInt > newInt) == direction) {
//                sumLevel += Math.abs(newInt);
//                peaks++;
//            }
//            direction = lastInt > newInt;
        }

        // There are too much crosses in 4096 samples ;)
        // Max int value = 32768
//        Log.d(classTAG, "Sum Peak Level: " + sumLevel);
//        Log.d(classTAG, "Peaks: " + peaks);
//        int R = ((int)(sumLevel / peaks)) / 128;
//        int G = ((peaks > 2000) ? (peaks -2000) / 10 : peaks / 10);
//        int B = cross0Axis / dataDivider;

//        // With Multipliers
//        R = R*multiplier + Rbase;
//        G = G*multiplier + Gbase;
//        B = B*multiplier + Bbase;

        // Without Multipliers
        R = R + Rbase;
        G = G + Gbase;
        B = B + Bbase;

        // Help only the most significant color
        if (R > G) {
            if (R > B) R = R*multiplier + Rbase;
            else B = B*multiplier + Bbase;
        } else {
            if (G > B) G = G*multiplier + Gbase;
            else B = B*multiplier + Bbase;
        }


        if (R > 255) {
            Log.d(classTAG, "Too much R");
            bytes[0] = (byte)0xff;
        } else bytes[0] = (byte)R;

        if (G > 255) {
            Log.d(classTAG, "Too much G");
            bytes[1] = (byte)0xff;
        } else bytes[1] = (byte)G;

        if (B > 255) {
            Log.d(classTAG, "Too much B");
            bytes[2] = (byte)0xff;
        } else bytes[2] = (byte)B;
        return bytes;
    }

//    private int convertByteToInt(byte[] b)
//    {
//         // Big Endian
//        (b[0]<<8) + (b[1] & 0xff);
//         // Little Endian
//        (b[1]<<8) + (b[0] & 0xff);
//    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        recorder.stop();
        recorder.release();
    }
}
