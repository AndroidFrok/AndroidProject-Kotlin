package com.hjq.demo.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hjq.demo.R

class JavaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java)

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
