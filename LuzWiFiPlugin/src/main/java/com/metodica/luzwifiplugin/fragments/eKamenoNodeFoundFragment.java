package com.metodica.luzwifiplugin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.metodica.luzwifiplugin.R;

/**
 * Created by Jacob on 1/11/14.
 */
public class eKamenoNodeFoundFragment extends Fragment {
    EditText configItem1 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plugin_config, container, false);
        ((TextView)(v.findViewById(R.id.eKsubtitle))).setText(getString(R.string.NodeInstalled));
        ((TextView)(v.findViewById(R.id.info))).setText(getActivity().getString(R.string.NodeInstalledInfo));
        Button actionButton = (Button)v.findViewById(R.id.actionButton);
        configItem1 = (EditText)v.findViewById(R.id.configItem1);
        actionButton.setText(getActivity().getString(R.string.SaveButton));

        // Get the last configured Data or
        SharedPreferences sp = getActivity().getSharedPreferences("EKAMENOPLUGINCONFIG", Context.MODE_PRIVATE);
        configItem1.setText(sp.getString("WIFICONTROLLERIP", "192.168.1.100"));

        // configure the button to save the data
        actionButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                try {
                    saveConfig();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    @Override
    public void onDestroy() {
        saveConfig();
        super.onDestroy();
    }

    public void saveConfig() {
        if (configItem1 != null) {
            SharedPreferences sp = getActivity().getSharedPreferences("EKAMENOPLUGINCONFIG", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("WIFICONTROLLERIP", configItem1.getText().toString());
            editor.commit();
            Toast.makeText(getActivity(), "OK", Toast.LENGTH_LONG).show();
        }
    }
}
