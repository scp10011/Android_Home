package com.example.circl.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("widget","init OK");
        String parameter = null;
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)){
            Uri data = intent.getData();
            Log.i("widget","onReceive OK");
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            switch (buttonId) {
                case R.id.WG_BT1:
                    parameter = "prev";
                    Log.i("widget","prev OK");
                    break;
                case R.id.WG_BT2:
                    parameter = "stop";
                    Log.i("widget","stop OK");
                    break;
                case R.id.WG_BT3:
                    parameter = "play";
                    Log.i("widget","play OK");
                    break;
            }
            dataput put = new dataput(parameter);
            put.start();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.i("widget","Update OK");
        RemoteViews RV = new RemoteViews(context.getPackageName(),R.layout.new_app_widget);
        RV.setOnClickPendingIntent(R.id.WG_BT1,getPendingIntent(context , R.id.WG_BT1));
        RV.setOnClickPendingIntent(R.id.WG_BT2,getPendingIntent(context , R.id.WG_BT2));
        RV.setOnClickPendingIntent(R.id.WG_BT3,getPendingIntent(context , R.id.WG_BT3));
        ComponentName componentName = new ComponentName(context, NewAppWidget.class);
        Log.i("widget","RV OK");
        appWidgetManager.updateAppWidget(componentName, RV);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private PendingIntent getPendingIntent(Context context,int buttonId){
        Intent intent = new Intent();
        intent.setClass(context, NewAppWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("harvic:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    public class dataput extends Thread {
        public String get_ip,msg;
        public dataput(String msg) {
            this.msg = msg;
        }
        public void run() {
            try {
                DatagramSocket socket;
                socket = new DatagramSocket();
                InetAddress serverAddress = InetAddress.getByName("255.255.255.255");
                byte data[] = this.msg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length ,serverAddress ,23333);
                socket.send(packet);
                socket.close();
            }
            catch (SocketException e) {}
            catch (IOException e) {}
        }
    }
}

