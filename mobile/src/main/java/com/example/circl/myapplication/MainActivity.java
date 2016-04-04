package com.example.circl.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private Button bt;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.button);
        tv = (TextView)findViewById(R.id.textView2);
        bt.setOnClickListener(new MyListener());

    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            tv.setText("成功");
            Log.i("cs","成功");
            dataput put = new dataput("play");
            put.start();
        }

    }

    public class dataput extends Thread {
        public String get_ip,msg;
        public dataput(String msg) {
            this.msg = msg;
        }
        public void run() {
            try {
                DatagramSocket socket;
                socket = new DatagramSocket(10012);
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
