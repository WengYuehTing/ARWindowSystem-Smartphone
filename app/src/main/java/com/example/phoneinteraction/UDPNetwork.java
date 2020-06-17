package com.example.phoneinteraction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPNetwork extends Thread {

    private static final int PORT = 8888;
    private static final String IP = "192.168.1.115";

    @Override
    public void run() {
        super.run();
        try {

            DatagramSocket server = new DatagramSocket(PORT);
            while (true) {

                byte[] recvBuf = new byte[100];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf,
                        recvBuf.length);
                server.receive(recvPacket);
                String recvStr = new String(recvPacket.getData(), 0,
                        recvPacket.getLength());
                System.out.println();
                System.out.println("收到的内容：" + recvStr);
                System.out.println("收到的ip：" + recvPacket.getAddress());
                // 发送UDP消息，JSON格式数据
                // {"name":"123","ID":"Robot007","BATTERY":"2.7f"}
                int port = recvPacket.getPort();
                InetAddress addr = recvPacket.getAddress();
                byte[] sendBuf = "Hello Hololens".getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuf,
                        sendBuf.length, addr, port);
                server.send(sendPacket);
//                server.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
