package vnc;

import java.io.*;
import java.util.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;  

import client.connected_thread;


public class vnc_server_thread extends Thread{
	
	private static Socket client = null;
	private static Socket server = null;
	private static int pid = 0;
	
	public vnc_server_thread(Socket s, int pid) {  
        this.client = s;  
        this.pid = pid;
    }  
							//local server thread
//==========================================================================================	
	public void run() {  
        try {  
              
            //建立一個接收client 訊息的thread
        	connected_thread t = new connected_thread(client, pid);
            t.start();  
  
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
            PrintWriter pw = new PrintWriter(client.getOutputStream());  
            
            //這裏是負責送訊息給client 的迴圈
            while (true) {  				
                String s = br.readLine();  
                pw.println(s);  
                pw.flush();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
								//local server 
//==========================================================================================
}


