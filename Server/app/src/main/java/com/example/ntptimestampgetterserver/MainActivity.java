package com.example.ntptimestampgetterserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private final int port = 5449;
    ServerSocket serverSocket;
    Socket socket;
    public final String TAG = "ntpServer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Thread serverThread = new ServerThread(port,10);
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class ServerThread extends Thread {
        int nTimes;
        public ServerThread(int port, int nTimes) throws IOException {
            serverSocket = new ServerSocket(port);
            this.nTimes = nTimes;

        }
        @Override
        public void run() {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"serverSocket connected");
            for(int i = 0; i<  nTimes; i++)
            {

                try {

                    InputStream inputStream =  socket.getInputStream();
                    byte[] buffer = new byte[1000];
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    dataOutputStream.writeLong(1);
                    dataOutputStream.writeLong(System.nanoTime());
                    socket.getOutputStream().write(byteArrayOutputStream.toByteArray());
                    socket.getOutputStream().flush();
//                    Log.d(TAG,"ntpServer sent message");
                    inputStream.read(buffer);
                    Long T4 = System.nanoTime();
//                    Log.d(TAG,"ntpServer read Message");
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                    DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                    Long offsetOne = dataInputStream.readLong();
                    Long T3 = dataInputStream.readLong();
                    Long offsetTwo = T3-T4;
                    Log.d(TAG,"time difference ["+i+"]th : "+(offsetTwo+offsetOne)/2);

                } catch (IOException e) {
                    e.printStackTrace();
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