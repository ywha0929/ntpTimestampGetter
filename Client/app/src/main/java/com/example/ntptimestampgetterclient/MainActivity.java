package com.example.ntptimestampgetterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MainActivity extends AppCompatActivity {
    private final int port = 5449;
    private final String ip = "192.168.0.2";
    public final String TAG = "ntpClient";
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Thread clientThread = new ClientThread(port,ip,10);
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class ClientThread extends Thread {
        int nTimes;
        int port;
        String ip;
        public ClientThread(int port, String ip, int nTimes) throws IOException {
            this.port = port;
            this.ip = ip;
            this.nTimes = nTimes;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(ip,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"Socket connected");
            for(int i = 0; i< nTimes; i++)
            {

                try {
                    InputStream inputStream =  socket.getInputStream();
                    byte[] buffer = new byte[16];
//                    Log.d(TAG,"ntpClient read Message");
                    inputStream.read(buffer);
//                    Log.d(TAG,"ntpClient read Message");
                    Long T2 = System.nanoTime();
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                    dataInputStream.readLong();
                    Long T1 = dataInputStream.readLong();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    dataOutputStream.writeLong(T2- T1);
                    dataOutputStream.writeLong(System.nanoTime());
                    socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
//                    Log.d(TAG,"ntpClient sent Message");

                } catch (Exception e){

                }
            }
        }
    }
    public long getNanoTimestamp()
    {
        return System.nanoTime();
    }
    public long getMillisTimestamp()
    {
        return System.currentTimeMillis();
    }
}