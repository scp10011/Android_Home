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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class Home extends AppWidgetProvider {
    private String posturl = "http://192.168.1.19/led_c.php";
    private String token = "liexpscp10011";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("widget","Update OK");
        RemoteViews RV = new RemoteViews(context.getPackageName(),R.layout.home);
        RV.setOnClickPendingIntent(R.id.HM_BT,getPendingIntent(context , R.id.HM_BT));
        ComponentName componentName = new ComponentName(context, Home.class);
        Log.i("widget","RV OK");
        appWidgetManager.updateAppWidget(componentName, RV);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)){
            Uri data = intent.getData();
            Log.i("widget","onReceive OK");
            int buttonId = Integer.parseInt(data.getSchemeSpecificPart());
            if (buttonId == R.id.HM_BT){
                httpc http = new httpc(5, 2);
                http.start();
            }
        }

        super.onReceive(context, intent);
    }

    private PendingIntent getPendingIntent(Context context, int buttonId){
        Intent intent = new Intent();
        intent.setClass(context, Home.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("harvic:" + buttonId));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    private class httpc extends Thread {
        private int ioid, state;
        public httpc(int ioid, int state) {
            this.ioid = ioid;
            this.state = state;
        }
        public void run() {
            String time = Long.toString(System.currentTimeMillis());
            String Random = getRandomString(10);
            System.out.println(token + time + Random);
            String check = SHA1(token + time + Random);
            posturl = posturl + "?check=" + check + "&time=" + time + "&random=" + Random;
            String params = "ioid=" + this.ioid + "&state=" + this.state;
            System.out.println(params);
            byte[] data = params.getBytes();
            URL url;
            System.out.println("################");
            System.out.println(posturl);
            try {
                url = new URL(posturl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", data.length + "");
                conn.setDoOutput(true);
                conn.getOutputStream().write(data);
                if (conn.getResponseCode() == 200) {
                    System.out.println("!ok!");
                }
            } catch (MalformedURLException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}

