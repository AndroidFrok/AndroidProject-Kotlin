package com.hjq.demo.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.util.Log;

import com.hjq.demo.R;

import java.util.List;

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        /*List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                int signalStrength = cellInfoGsm.getCellSignalStrength().getDbm();
                Log.d("Signal Strength", "GSM Signal Strength: " + signalStrength + " dBm");
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                int signalStrength = cellInfoLte.getCellSignalStrength().getDbm();
                Log.d("Signal Strength", "LTE Signal Strength: " + signalStrength + " dBm");
            }
        }*/
    }
}