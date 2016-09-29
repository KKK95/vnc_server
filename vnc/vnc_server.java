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

import vnc.RemoteDataServer;

//------------------------------http----------------------------------

import Browser.Water_Crab;

import org.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

public class vnc_server extends Thread{
	
//	private static Socket client = null;
//	private static Socket server = null;
	//=======================================================================================

//extends thread 的話如果要共享參數, 要加上static, 不過最好不要這樣用
//extends runnable 的話不用加static 都可以共享參數, 且runnable 可以有多個接口
	
	// local settings
			private int PORT;
			
			//remote settings
			private int clientPort;
			private InetAddress listenerAddress;
			
			private ServerSocket server;
			private Socket client;

			private RemoteDataServer vnc_thread;
			private Remote_controler remote_controler;
			
			private Water_Crab browser = null;
			private String url = null;
	//=======================================================================================
	
	public vnc_server(Water_Crab get_browser, int get_port, String get_ip)		//初次化vnc_server
	{
		PORT = get_port;
			vnc_thread = new RemoteDataServer();
			remote_controler = new Remote_controler();
			browser = get_browser;
			url = browser.get_now_url();
	}
	
	public void run() 
	{
	
		try(ServerSocket server = new ServerSocket(PORT)) 
		{
			System.out.println("vnc server running now...");
			for (int pid=0; true; )
		    {
		    	if (pid < 20)
		    	{
		    		client = server.accept();
		    		if (browser != null)		//更新網頁
		    		{
		    			using_browser();
		    			//利用client ip 來查 client name
		    		}
		    		//remote_controler.member_join(client, client_name, client_ip);
		    		vnc_thread.init(client);
		    		new Thread(vnc_thread).start();
		    		//再這加一個 vnc_thread.func, 每次有人連進來都要更新一下group_list
		    		
		    		
		    		
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
	}
	
	private void using_browser() throws ClientProtocolException, IOException
	{
		
		int choose = 0;
		System.out.println("-----------------------------------new web!-----------------------------------");
        System.out.println("");
        
		//===================================================================================	
														//網頁資料都送進來處理, 把資料過濾後	|
        browser.link(url);								//並讓使用者查看網頁內容				|
        												//使用超連結或提交表單後, 返回超連結	|
        //===================================================================================			        
//	        browser.show_json_data();
        browser.show_web_data();
		
/*			choose = browser.input();	//根據web 的界面做你想做的事, 其後會向cloud 發出請求, cloud 收到請求後會返會url
			
			if (choose > 0)
			{
				url = browser.chick_link( choose );
				if (url.indexOf("download") >= 0)
				{
					url = browser.download_file(url, null);
					System.out.println("download file");
				}

			}
			else
			{	url = browser.post_submit_form();	}
*/		
		System.out.println("");
        System.out.println("-----------------------------------end web!-----------------------------------");
        System.out.println("");
		return;
	}
}


