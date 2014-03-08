package com.metodica.luzwifiplugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;

import com.metodica.luzwifiplugin.async.AsyncAlarmWorker;
import com.metodica.luzwifiplugin.async.AsyncPartyWorker;
import com.metodica.luzwifiplugin.async.AsyncTemplateWorker;
import com.metodica.nodeplugin.INodePluginV1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LuzWiFiPlugin extends Service {
    // PLUGIN VARIABLES
    static final String LOG_TAG = "luzwifiplugin";
    static final String PLUGIN_NAME = "luzwifiplugin";
    static final String ACTION = "com.metodica.luzwifiplugin";
    static final String CATEGORY = "com.metodica.nodeplugin.LUZWIFI_PLUGIN";

    public String WiFiControllerIP = null;
    public int WiFiControllerPort = 50000;
    public AsyncTemplateWorker worker = null;

    public void foundController() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("EKAMENOPLUGINCONFIG", Context.MODE_PRIVATE);
        WiFiControllerIP = sp.getString("WIFICONTROLLERIP", "192.168.1.100");
    }

    public void assingWorker(AsyncTemplateWorker newWorker) {
        stopWorker();
        worker = newWorker;
    }

    public void startWorker() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
            worker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            worker.execute();
        }
    }

    public void stopWorker() {
        if (worker != null) {
            worker.stopAsyncTask();
            worker.cancel(true);
        }
        worker = null;
    }

    public void sendLuzWifiMessage(byte[] sendBuffer, int repeat) {
        sendLocalLuzWifiMessage(sendBuffer, repeat, true);
    }

    public void sendLocalLuzWifiMessage(byte[] sendBuffer, int repeat, boolean wait) {
        //             sendBuffer = message.getBytes();
//            if (WiFiControllerIP == null) WiFiControllerIP = "192.168.1.50";
        try
        {
            foundController();
//                WiFiControllerIP = "192.168.1.52";
////                WiFiControllerIP = "192.168.1.131";

            InetAddress address = InetAddress.getByName(WiFiControllerIP);
            Log.d("NETWORK", "Address successfully resolved: " + address.getHostAddress());

//                DatagramSocket socket = new DatagramSocket();
//                Log.d( "NETWORK", "UDP Socket successfully created" );

            DatagramPacket packet = new DatagramPacket( sendBuffer, sendBuffer.length, address, WiFiControllerPort );
            DatagramSocket socket;
//                socket.send( packet );
//                socket.close();
//                sendBuffer[0] += 128;
//                DatagramPacket repeatPacket = new DatagramPacket( sendBuffer, sendBuffer.length, address, WiFiControllerPort );
            for (int i=0;i < repeat;i++) {
                socket = new DatagramSocket();
                // Bulb need a time between commands
                if (wait) Thread.sleep(130);
                socket.send( packet );
                socket.close();
            }
        }
        catch (Exception ioe)
        {
            Log.d( "NETWORK", "Failed to send UDP packet due to Exception: " + ioe.getMessage() );
            ioe.printStackTrace();
        }

    }

    private final INodePluginV1.Stub addBinder = new INodePluginV1.Stub() {

        // Function Name: 	run
        // Description: 	Uses the COMMAND and DATAs configured in "getXMLDefaults" to
        //					launch real actions.
        // Parameters:
        //					String command:
        //					String data1:
        //					String data2:
        // 					String data3
        //					String actionID:
        //
        // Return:			XML String

        public boolean runPlugin(String command, String data1, String data2, String data3, String actionID, int flagsInUse) throws RemoteException {
            // 	flagsInUse EXPLANATION:
            //	00000001	PLUGIN UP (NOT USED HERE)
            //	00000010	PLUGIN WORKING (NOT USED HERE)
            //	00000100	USING AN ACTIVITY
            //	00001000	USING CAMERA HARDWARE
            //	00RR0000	RESERVED FLAGS
            //	XX000000	CUSTOM FLAGS FOR YOUR PLUGINS

            // FIRST set WORKING flag ON
            setFlag(FLAG_WORKING);

            stopWorker();
            if (command.equalsIgnoreCase("COLORON")) {
                try {
                    byte[] msg = new byte[]{(byte)0x01, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN ON", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLOROFF")) {
                try {
                    byte[] msg = new byte[]{(byte)0x02, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORBPLUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x23, (byte)0x00, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORBMINUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x24, (byte)0x00, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORSPLUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x25, (byte)0x00, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORSMINUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x26, (byte)0x00, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORMODEPLUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x17, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORMODEMINUS")) {
                try {
                    byte[] msg = new byte[]{(byte)0x28, (byte)0x00, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);
                    sendReturn(200, "SHOWTEXT", "LOW", "TURN OFF", actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else if (command.equalsIgnoreCase("COLORSETMODE")) {
                try {
                    byte[] msg = new byte[]{(byte)0x14, (byte)0x55};
                    sendLuzWifiMessage(msg, 1);

                    if (data1.equalsIgnoreCase("PARTY")) {
                        msg = new byte[]{(byte)0x17, (byte)0x55};
                        sendLuzWifiMessage(msg, 9);
                        sendReturn(200, "SHOWTEXT", "LOW", getString(R.string.colorsetparty) + " OK", actionID);
                    } else if (data1.equalsIgnoreCase("MUSIC")) {
                        assingWorker(new AsyncPartyWorker(LuzWiFiPlugin.this, 150));
                        startWorker();
                        sendReturn(200, "SHOWTEXT", "LOW", getString(R.string.colorsetmusic) + " OK", actionID);
                    } else if (data1.equalsIgnoreCase("RELAX")) {
                        msg = new byte[]{(byte)0x17, (byte)0x55};
                        sendLuzWifiMessage(msg, 12);
                        sendReturn(200, "SHOWTEXT", "LOW", getString(R.string.colorsetrelax) + " OK", actionID);
                    } else if (data1.equalsIgnoreCase("ALARM")) {
                        assingWorker(new AsyncAlarmWorker(LuzWiFiPlugin.this, 1000));
                        startWorker();
                        sendReturn(200, "SHOWTEXT", "LOW", getString(R.string.colorsetalarm) + " OK", actionID);
                    } else {
                        sendReturn(200, "SHOWTEXT", "LOW", getString(R.string.colorsetwhite) + " OK", actionID);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }

            } else {

            }

            // FREE the plugin if it is not WORKING IN ACTIVITY
            // Unset Work Flag only if you don't launch any activity.. Otherwise do it in the activity
            unsetFlag(FLAG_WORKING);
            return true;
        }



        // Function Name: 	getXMLDefaults
        // Description: 	Each <DEFAULT> will be an action you can use from your eKameno Client
        //					and it needs a <COMMAND> (Which should be the ID of the action to do)
        //					and three <DATA>s which are variables to launch this COMMAND.
        // Parameters:		None
        // Return:			XML String (Take care to close everything you open)

        @Override
        public String getXMLDefaults() throws RemoteException {
            return 	"<DEFAULT>" +
                    "<NAME>" + getString(R.string.coloron) + "</NAME>" +
                    "<COMMAND>COLORON</COMMAND>" +
                    "<DATA1></DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.coloroff) + "</NAME>" +
                    "<COMMAND>COLOROFF</COMMAND>" +
                    "<DATA1></DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

//                    "<DEFAULT>" +
//                    "<NAME>" + getString(R.string.colorplus) + "</NAME>" +
//                    "<COMMAND>COLORBPLUS</COMMAND>" +
//                    "<DATA1></DATA1>" +
//                    "<DATA2></DATA2>" +
//                    "<DATA3></DATA3>" +
//                    "</DEFAULT>" +
//
//                    "<DEFAULT>" +
//                    "<NAME>" + getString(R.string.colorbminus) + "</NAME>" +
//                    "<COMMAND>COLORBMINUS</COMMAND>" +
//                    "<DATA1></DATA1>" +
//                    "<DATA2></DATA2>" +
//                    "<DATA3></DATA3>" +
//                    "</DEFAULT>" +
//
//                    "<DEFAULT>" +
//                    "<NAME>" + getString(R.string.colorsplus) + "</NAME>" +
//                    "<COMMAND>COLORSPLUS</COMMAND>" +
//                    "<DATA1></DATA1>" +
//                    "<DATA2></DATA2>" +
//                    "<DATA3></DATA3>" +
//                    "</DEFAULT>" +
//
//                    "<DEFAULT>" +
//                    "<NAME>" + getString(R.string.colorsminus) + "</NAME>" +
//                    "<COMMAND>COLORSMINUS</COMMAND>" +
//                    "<DATA1></DATA1>" +
//                    "<DATA2></DATA2>" +
//                    "<DATA3></DATA3>" +
//                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colormodeplus) + "</NAME>" +
                    "<COMMAND>COLORMODEPLUS</COMMAND>" +
                    "<DATA1></DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

//                    "<DEFAULT>" +
//                    "<NAME>" + getString(R.string.colormodeminus) + "</NAME>" +
//                    "<COMMAND>COLORMODEMINUS</COMMAND>" +
//                    "<DATA1></DATA1>" +
//                    "<DATA2></DATA2>" +
//                    "<DATA3></DATA3>" +
//                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colorsetwhite) + "</NAME>" +
                    "<COMMAND>COLORSETMODE</COMMAND>" +
                    "<DATA1>WHITE</DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colorsetrelax) + "</NAME>" +
                    "<COMMAND>COLORSETMODE</COMMAND>" +
                    "<DATA1>RELAX</DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colorsetparty) + "</NAME>" +
                    "<COMMAND>COLORSETMODE</COMMAND>" +
                    "<DATA1>PARTY</DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +


                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colorsetmusic) + "</NAME>" +
                    "<COMMAND>COLORSETMODE</COMMAND>" +
                    "<DATA1>MUSIC</DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>" +

                    "<DEFAULT>" +
                    "<NAME>" + getString(R.string.colorsetalarm) + "</NAME>" +
                    "<COMMAND>COLORSETMODE</COMMAND>" +
                    "<DATA1>ALARM</DATA1>" +
                    "<DATA2></DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>";
        }



        // Function Name: 	getXMLCustomOptions
        // Description: 	Not working in this version yet.
        // Parameters:		None
        // Return:			XML String

        @Override
        public String getXMLCustomOptions() throws RemoteException {
            return "";
        }



        // Function Name: 	initiate
        // Description: 	Some plugins need to initiate something before executing, this function
        //					executes when the plugin becomes active one only time (if it not crash).
        // Parameters:		None
        // Return:			None

        @Override
        public void initiate() throws RemoteException {
            // DO here whatever the plugin needs to initiate it
            // In TemplatePlugin case there is nothing to do
            foundController();
        }

        // Function Name: 	is_compatible
        // Description: 	Return True if the plugin can be executed in this platform and
        //					False if it is not.
        // Parameters:		None
        // Return:			Boolean

        @Override
        public boolean is_compatible() throws RemoteException {
            // The example works in every platform cause it do nothing
            return true;
        }



























        //////////////////\\\\\\\\\\\\\\\\\\\
        /////							\\\\\
        /////    - DO NOT TOUCH PART -	\\\\\
        /////	   SYSTEM FUNCTIONS		\\\\\
        /////							\\\\\
        //////////////////\\\\\\\\\\\\\\\\\\\

        // DO NOT CROSS THIS LINE!!															\\
        // --------------------------------------------------------------------------------	\\

        @Override
        public String getPluginShowName() throws RemoteException {
            return getString(R.string.PluginShowName);
        }

        @Override
        public String getPluginName() throws RemoteException {
            return PLUGIN_NAME;
        }

        @Override
        public int getStatusFlag() throws RemoteException {
            return statusFlag;
        }

        @Override
        public boolean run(String command, String data1, String data2, String data3, String actionID, int flagsInUse) throws RemoteException {
            sendReturn(200, "VOID", "MEDIUM", command + " received", actionID);
            return runPlugin(command, data1, data2, data3, actionID, flagsInUse);
        }

        @Override
        public String getResource() throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }
    };


    static final String PLUGINRESPONSE = "com.metodica.ekamenoserver.PLUGINRESPONSE";

    // 	FLAGS EXPLANATION:
    //	00000001	PLUGIN UP
    //	00000010	PLUGIN WORKING
    //	00000100	USING AN ACTIVITY
    //	00001000	USING CAMERA HARDWARE
    //	00RR0000	RESERVED FLAGS
    //	XX000000	CUSTOM FLAGS FOR YOUR PLUGINS
    public static final int FLAG_LINKED = 1 << 0;
    public static final int FLAG_WORKING = 1 << 1;
    public static final int FLAG_ACTIVITY = 1 << 2;
    public static final int FLAG_CAMERA = 1 << 3;
    public static final int FLAG_RESERVED1 = 1 << 4;
    public static final int FLAG_RESERVED2 = 1 << 5;
    public static final int FLAG_CUSTOM1 = 1 << 6;
    public static final int FLAG_CUSTOM2 = 1 << 7;

    private static int statusFlag;

    public void onStart(Intent intent, int startId) {
        Log.d(LOG_TAG, "onStart()");
        statusFlag = FLAG_LINKED;
        super.onStart( intent, startId );
    }

    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind()");
        return addBinder;
    }


    //////////////////\\\\\\\\\\\\\\\\\\\
    /////		FLAGS WORKZONE		\\\\\
    //////////////////\\\\\\\\\\\\\\\\\\\

    public static synchronized void setFlag(int newFlag) {
        statusFlag |= newFlag;
    }

    public static synchronized void unsetFlag(int newFlag) {
        statusFlag &= ~newFlag;
    }

    public static synchronized boolean isFlag(int status, int newFlag) {
        return ((status & newFlag) == newFlag);
    }

    //////////////////\\\\\\\\\\\\\\\\\\\
    /////		SEND RETURNS		\\\\\
    //////////////////\\\\\\\\\\\\\\\\\\\

    private void sendReturn(int errorCode, String type, String criticity, String data, String _actionID) {
        // SEND IMAGE AS RETURN AND CLOSE
        Intent i = new Intent(LuzWiFiPlugin.PLUGINRESPONSE);
        Bundle extras = new Bundle();

        if (_actionID != null) extras.putString("ACTIONID", _actionID);
        else extras.putString("ACTIONID", "");

        extras.putInt("ERRORCODE", errorCode);
        extras.putString("TYPE", type);
        extras.putString("DATA", data);
        extras.putString("CRITICITY", criticity);

        i.putExtras(extras);
        sendBroadcast(i);
    }
}
