package vnc;

import java.io.*;
import java.util.*;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;  

import client.connected_thread;
import screen.ImageSender;
import server_config.ServerListener;
import vnc.vnc_server_thread;
import vnc.RemoteDataServer;

//------------------------------http----------------------------------
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair; 

import org.json.JSONObject;
import org.json.JSONArray;

public class vnc_server extends Thread{
	
//	private static Socket client = null;
//	private static Socket server = null;
	//=======================================================================================

//extends thread 的話如果要共享參數, 要加上static, 不過最好不要這樣用
//extends runnable 的話不用加static 都可以共享參數, 且runnable 可以有多個接口
	
	// local settings
			private int PORT;
			private InetAddress ipAddress;
			private InetAddress ip;
			
			//remote settings
			private int clientPort;
			private InetAddress listenerAddress;
			
			private ServerSocket server;
			private Socket client;

	//=======================================================================================
	static List<vnc_server_thread> client_list = new ArrayList<vnc_server_thread>();
	
	public vnc_server(int get_port, String get_ip)		//初次化vnc_server
	{
		PORT = get_port;
		try {
			ip = InetAddress.getByName(get_ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() 
	{
		
//========================監聽在remotedata , 要拆了它放在這邊========================
		
//		serverMessages.setText("Waiting for connection on " + ip);
//==================================================================================
//		Lock lock = new ReentrantLock();
		
		boolean connected = false;
		try(ServerSocket server = new ServerSocket(PORT)) 
		{
			System.out.println("vnc server running now...");
			for (int pid=0; true; )
		    {
		    	if (pid < 20)
		    	{
		    		client = server.accept();
		    		RemoteDataServer vnc_thread = new RemoteDataServer(client);  //應加到list 來控制
		        	vnc_thread.start();
		        	++pid;
		        	System.out.println("IP " + "\n" + "Port " + PORT + 
		        					   " is already in use. Use a different Port");
		    	}
		    }
		}
		catch(BindException e){
			System.out.println("Port " + PORT + " is already in use. Use a different Port");
		}
		catch(Exception e){
			System.out.println("Unable to connect");
		}
		
		
		
		

//		ServerSocket server = new ServerSocket(SERVER_PORT)
	 /*   try
	    {
	  //  	System.out.println(InetAddress.getLocalHost().toString());
			 //Create a server socket on port 8998
			//read input from client while it is connected
		    for (int pid=0; true; )
		    {
		    	if (pid < 20)
		    	{
			        try{
				        	Socket client = server.accept();
				        	vnc_server_thread vnc_thread = new vnc_server_thread(client, pid);  
				        	vnc_thread.start();  
				            client_list.add(vnc_thread);
							pid++;	
			        		
						} catch (IOException e) {
							System.out.println(e);
						} catch (RuntimeException e) {
							System.out.println(e);
						}
		    	}
		     } 
		}catch (IOException e) {
			System.out.println("Error in opening Socket");
			System.exit(-1);
		}	*/
	}
}


