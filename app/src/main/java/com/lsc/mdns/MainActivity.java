package com.lsc.mdns;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;


public class MainActivity extends AppCompatActivity {

    TextView myIP;
    JmDNS bonjourService;
    ServiceListener bonjourServiceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        myIP = (TextView) findViewById(R.id.textView);

        new Thread(){
            @Override
            public void run()
            {
                //把网络访问的代码放在这里
                try {
                    String bonjourServiceType = "_http._tcp.local.";
                    String bonjourServiceName = "esp8266";
                    bonjourService = JmDNS.create();
                    bonjourService.addServiceListener(bonjourServiceType, bonjourServiceListener);
                    ServiceInfo[] serviceInfos = bonjourService.list(bonjourServiceType);
                    for (ServiceInfo info : serviceInfos) {
                        if (bonjourServiceName.equals(info.getName()))
                        {
                            //处理完成后给handler发送消息
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = info.getInetAddress().getHostAddress();
                            handler.sendMessage(msg);
                            Log.e("JmDNS: ", "IpAddress IS "+info.getInetAddress().getHostAddress());
                        }
                    }
                    bonjourService.close();
                }
                catch (Exception e){
                    Log.e("JmDNS", e.toString());
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                myIP.setText(msg.obj.toString());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
