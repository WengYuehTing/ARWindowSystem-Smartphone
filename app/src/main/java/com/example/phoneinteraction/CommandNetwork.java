package com.example.phoneinteraction;

import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandNetwork {
    private static CommandNetwork instance = new CommandNetwork();
    private CommandNetwork() {}
    public static CommandNetwork getInstance() { return instance;}

    private ServerSocket mServerSocket;
    private Socket mSocket;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;

//    public String  = null;

    public void start() {
        new ConnectThread().start();
    }

    private class recvThread extends Thread {
        @Override
        public void run() {
            super.run();

            while(mSocket != null){

                try{
                    is = mSocket.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);


                    // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                    String response = br.readLine();
                    if(response != null) {
                        Log.d("YueTing",response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }



    private class ConnectThread extends Thread {

        @Override
        public void run() {
            super.run();

            while(true) {
                try {
                    mServerSocket = new ServerSocket(8888);

                    while (true) {
                        Log.d("YueTing", "Waiting for connection");
                        mSocket = mServerSocket.accept();
                        Log.d("YueTing", "connected by : " + mSocket.getInetAddress().toString());
                        new recvThread().start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /** Send string(utf-8) to server */
    public void send(final String msg) {

        if(mSocket == null) { return; }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OutputStream os = mSocket.getOutputStream();
                    Log.d("YueTing", msg);
                    os.write((msg + "\n" ).getBytes("utf-8"));
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
