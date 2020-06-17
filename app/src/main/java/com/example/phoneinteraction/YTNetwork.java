package com.example.phoneinteraction;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

interface NetworkInterface {
    void onSendString(String message);
    void onReceiveString(String message);
}


public class YTNetwork {

    private static YTNetwork instance = new YTNetwork();
    private YTNetwork() {}
    public static YTNetwork getInstance() { return instance;}

    public NetworkInterface delegate;

    /*********** Client ***********/
    private List<YTClient> clients = new ArrayList<>();
    public void createClient(String IP, int port, String name) { clients.add(new YTClient(IP,port,name));}
    public YTClient getClientByIndex(int index) {

        if(index < 0 || index >= getClientsCount()) {
            Log.d("YTNetwork", "Not found any client, make sure your index is legal");
            return null;
        }

        return clients.get(index);
    }

    public YTClient getClientByName(String name) {

        for(int i=0;i<getClientsCount();i++) {
            if(name == clients.get(i).name) {
                return clients.get(i);
            }
        }

        Log.d("YTNetwork", "Not found any client whose name equal to  " + name);
        return null;
    }

    public YTClient getFirstClient() {
        if(!isEmpty()) {
            return getClientByIndex(0);
        }

        Log.d("YTNetwork", "Not found client, there is no client currently");
        return null;
    }

    public int getClientsCount() {
        return clients.size();
    }

    public boolean isEmpty() {
        return clients.size() == 0;
    }

    /*********** Server ***********/

    private YTServer server;

    public void startServer(int port) {
        server = new YTServer(port);
    }

    public YTServer getServer() {
        return server;
    }




    /*********** Custom Class ***********/


    public class YTClient {

        private YTSocket socket;
        private String connectedIP;
        private int connectedPort;
        private String name;

        public YTClient(String serverIP, int serverPort, String name) {

            this.connectedIP = serverIP;
            this.connectedPort = serverPort;
            this.name = name;


            new Thread(new ConnectRunnable()).start();
        }


        public YTSocket getSocket() {
            return socket;
        }

        public boolean isValid() {
            return socket != null;
        }

        public String getIP() {
            return connectedIP;
        }

        public int getPort() {
            return connectedPort;
        }

        public String getName() {
            return name;
        }

        private class ConnectRunnable implements  Runnable {

            @Override
            public void run() {

                try{
                    socket = new YTSocket(connectedIP, connectedPort);
                    socket.recv();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.d("YTNetwork", "a network client had been created");
            }
        }

    }

    private class YTServer {

        private YTServerSocket serverSocket;
        private YTSocket socket;
        private int listenedPort;

        public YTServer(int listenedPort) {
            this.listenedPort = listenedPort;

            new Thread(new ListenRunnable()).start();


        }

        public int getListenedPort() {
            return listenedPort;
        }

        public YTSocket getSocket() {
            return socket;
        }

        public String getConnectedIP() {
            if(socket != null) {
                return socket.getIP();
            }

            return "Server doesn't connect";
        }




        private class ListenRunnable implements  Runnable {

            @Override
            public void run() {

                while(true) {
                    try {
                        Log.d("YTNetwork","Server listened on " + String.valueOf(listenedPort));
                        serverSocket = new YTServerSocket(listenedPort);
                        socket = serverSocket.accepted();
                        socket.recv();
                        Log.d("YTNetwork","a client connected!, which ip = " + socket.getIP());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }


    private class YTServerSocket extends ServerSocket {


        public YTServerSocket(int port) throws IOException {
            super(port);

        }

        public YTSocket accepted() {
            try{
                Socket socket = super.accept();
                YTSocket ytSocket = new YTSocket(socket.getLocalAddress().getHostAddress(),socket.getPort());
                return ytSocket;
            }catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("YTNetwork", "Cannot accept a YTSocket");
            return null;
        }
    }

    public class YTSocket extends Socket {

        private DataOutputStream os;
        private BufferedReader is;
        private String receivedMessage;

        public YTSocket(String ip, int port) throws IOException {
            super(ip,port);

            os = new DataOutputStream(getOutputStream());
            is = new BufferedReader(new InputStreamReader(getInputStream()));


        }

        public void recv() {
            new Thread(new ReceiveStringRunnable()).start();
        }

        public void sendBytes(byte[] data, int size) {
            Thread thread = new Thread(new WriteBytesRunnable(os, data, size));
            thread.start();
            try {
                thread.join();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        public void sendByte(byte num) {
            byte[] data = new byte[1];
            data[0] = num;
            sendBytes(data, 1);
        }

        public void sendShort(short num) {
            byte[] data = new byte[2];
            data[0] = (byte)(num & 0xff);
            data[1] = (byte)((num >> 8) & 0xff);
            sendBytes(data, 2);
        }

        public void sendInt(int num) {
            byte[] data = new byte[4];
            data[0] = (byte)(num & 0xff);
            data[1] = (byte)((num >> 8) & 0xff);
            data[2] = (byte)((num >> 16) & 0xff);
            data[3] = (byte)((num >> 24) & 0xff);
            sendBytes(data, 4);
        }

        public void sendDouble(double num) {
            byte[] data = new byte[8];
            long value = Double.doubleToRawLongBits(num);
            for(int i = 0; i < 8; i++) {
                data[i] = (byte)((value >> (i << 3)) & 0xff);
            }
            sendBytes(data, 8);
        }

        public void sendString(String str, int size) {
            sendBytes(str.getBytes(), str.length());
            for(int i = 0; i < size - str.length(); i++) {
                sendByte((byte)0);
            }
        }

        public void sendString(String str) {



            final String str1 = str + "\n";
            try {
                sendBytes(str1.getBytes("utf-8"), str1.length());
                if(delegate!=null){
                    delegate.onSendString(str);
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }



        private class WriteBytesRunnable implements Runnable {
            DataOutputStream os;
            private byte[] data;
            private int size;
            WriteBytesRunnable(DataOutputStream os, byte[] data, int size) {
                this.os = os;
                this.data = data;
                this.size = size;
            }

            public void run() {
                try {
                    os.write(data, 0, size);
                }
                catch(Exception e) {
                    System.out.println("Error: send message failed.");
                    e.printStackTrace();
                }
            }
        }

        public JSONObject receiveJSON() throws IOException {
            InputStream in = getInputStream();
            ObjectInputStream i = new ObjectInputStream(in);
            JSONObject line = null;
            try {
                line = (JSONObject) i.readObject();

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }


            return line;

        }

        private class ReceiveStringRunnable implements Runnable {

            @Override
            public void run() {
                while(true) {
                    if(isClosed() || !isConnected()) { continue; }

//                    try {
//                        JSONObject json = receiveJSON();
//                        Log.d("YueTing","test1");
////                        Log.d("YueTing", json.toString());
//                    } catch(IOException e) {
//                        Log.d("YueTing","test2");
//                        e.printStackTrace();
//                    }
                    try {
                        setReceivedMessage(is.readLine());

                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        private void setReceivedMessage(String message) {
            receivedMessage = message;
            try {
                JSONObject json = new JSONObject(message);
                Log.d("YueTing", json.toString());
                Log.d("YueTing", json.getString("result"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(delegate != null)
                delegate.onReceiveString(message);
        }

        public String getIP() {
            return getInetAddress().getHostAddress();
        }

    }


}