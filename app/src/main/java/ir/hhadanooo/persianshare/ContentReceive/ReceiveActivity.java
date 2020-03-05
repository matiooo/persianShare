package ir.hhadanooo.persianshare.ContentReceive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import ir.hhadanooo.persianshare.ContentTransfer.PortalReceiver.ActivityPortalReceiver;
import ir.hhadanooo.persianshare.R;

public class ReceiveActivity extends AppCompatActivity {

    ImageView img_barcode;
    TextView tv_name_wifi;

    public static final String HOTSPOT_NAME = "AndroidShare_2580";
    public static final String HOTSPOT_PASSWORD = "25802580";

    Handler handler;
    boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        img_barcode = findViewById(R.id.img_barcode_receive);
        tv_name_wifi = findViewById(R.id.tv_name_wifi_receive);
        handler = new Handler();

        TurnOnHotspot();




    }
    public void TurnOnHotspot()
    {

        //turn on hotspot in android lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            WifiManager wifiManager;
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = HOTSPOT_NAME;
            wifiConfig.preSharedKey = HOTSPOT_PASSWORD;
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            try {
                if (wifiManager.isWifiEnabled()) { //disables wifi hotspot if it's already enabled
                    wifiManager.setWifiEnabled(false);
                }else {
                    wifiManager.setWifiEnabled(true);
                }
                if (wifiManager.isWifiEnabled()) { //disables wifi hotspot if it's already enabled
                    wifiManager.setWifiEnabled(false);
                }
                Method method = wifiManager.getClass()
                        .getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifiManager, wifiConfig, true);

            } catch (Exception e) {
                Log.e("NAME_LOG", "", e);
            }

            start_server(false);
        }
        //Turn on hotspot in android vesrion m
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            //request permission write setting
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                if(!Settings.System.canWrite(this)) {
                    Log.v("NAME_LOG", " " + !Settings.System.canWrite(this));
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + this.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent,1);
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true)
                        {
                            if(Settings.System.canWrite(ReceiveActivity.this)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WifiManager wifiManager;
                                        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                                        WifiConfiguration wifiConfig = new WifiConfiguration();
                                        wifiConfig.SSID = HOTSPOT_NAME;
                                        wifiConfig.preSharedKey = HOTSPOT_PASSWORD;
                                        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                                        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                                        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                                        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                                        try {
                                            if (wifiManager.isWifiEnabled()) { //disables wifi hotspot if it's already enabled
                                                wifiManager.setWifiEnabled(false);
                                            }else {
                                                wifiManager.setWifiEnabled(true);
                                            }

                                            if (wifiManager.isWifiEnabled()) { //disables wifi hotspot if it's already enabled
                                                wifiManager.setWifiEnabled(false);
                                            }

                                            Method method = wifiManager.getClass()
                                                    .getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                                            method.invoke(wifiManager, wifiConfig, true);
                                        } catch (Exception e) {
                                            Log.e("NAME_LOG", "", e);
                                        }
                                    }

                                });
                                break;

                            }
                        }

                    }
                });
                thread.start();

            }

            //disable wifi and enable hotsput

            start_server(false);

        }
        //Turn on hotspot in android oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    //mReservation = reservation;
                    Log.i("NAME_LOG", "onStarted: ");
                    Log.d("NAME_LOG", "Wifi Hotspot is on now");
                    //HOTSPOT_NAME = reservation.getWifiConfiguration().SSID;
                    //HOTSPOT_PASSWORD = reservation.getWifiConfiguration().preSharedKey;
                    Log.i("NAME_LOG", "SSID : " + HOTSPOT_NAME);
                    Log.i("NAME_LOG", "PASS : " + HOTSPOT_PASSWORD);

                    start_server(true);
                }
                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.i("NAME_LOG", "onStopped: ");
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.i("NAME_LOG", "onFailed: ");
                }
            }, new Handler());

        }
    }

    public void start_server(boolean check)
    {
        handler.postDelayed(new check_client_connected(),3000);
        QRGEncoder qrgEncoder;
        if(check)
        {
            qrgEncoder = new QRGEncoder(HOTSPOT_NAME + ":" + HOTSPOT_PASSWORD
                    ,null, QRGContents.Type.TEXT,250);

        }else {
            qrgEncoder = new QRGEncoder(HOTSPOT_NAME + ":" + HOTSPOT_PASSWORD
                    ,null, QRGContents.Type.TEXT,250);

        }
        tv_name_wifi.setText(HOTSPOT_NAME + "\n" + HOTSPOT_PASSWORD);


        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            img_barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v("bina", e.toString());
        }


    }

    public int getClientList() {
        int macCount = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null ) {

                    String mac = splitted[3];

                    if (mac.matches("..:..:..:..:..:..")) {
                        macCount++;
                        return macCount;


                    }

                }
            }
        } catch(Exception e) {

            e.fillInStackTrace();
        }
        return 0;
    }

    public class check_client_connected implements Runnable
    {

        @Override
        public void run() {

            if(getClientList() > 0)
            {
                check = true;
                startActivity(new Intent(ReceiveActivity.this, ActivityPortalReceiver.class));
            }

            if(!check)
            {
                handler.postDelayed(this,3000);
            }
        }
    }

}