package vnc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Map;

import screen.ImageSender;
import server_config.Constants;
import server_config.ServerListener;
import vnc_mouse.AutoBot;

public class RemoteDataServer implements Runnable
{
		// local settings
		private Object lock = new Object();
		private Socket public_client;
		private boolean get;
		private AutoBot bot;	//bot 是公用的, 反正一次只能有一個client 使用	
		private InetAddress root_addr;		//最高權限是誰
		Map<String, String> group_list;
		
		
		private int send_to_clientPort;		//client 之間私下傳訊息用
		private InetAddress send_to_Address;
		
//========================================================================	
		
		public RemoteDataServer(){	get = true;	}
		
		public void init(Socket socket)		//建立新thread 前必需要呼叫此函數來初始化
		{
			while(!(get))System.out.println("有thread 正在初始化...");
			synchronized(lock)  
	        { 	public_client = socket;   get = false; }
		}
		
public void run(){
			//初始化所有參數
//=====================================================================			
			boolean connected = false;
			BufferedReader in = null;
			PrintWriter out = null;
			int clientPort = 0;
			InetAddress listenerAddress = null;
			String message = "";
			ImageSender sender = null;
//=====================================================================	
			
			//把共享的值變為局部值
//=====================================================================
			synchronized(lock)  
	        { 
				try 
				{
					in = new BufferedReader(new InputStreamReader(public_client.getInputStream()));
					out = new PrintWriter(new OutputStreamWriter(public_client.getOutputStream()));
					connected = true;
					listenerAddress = public_client.getInetAddress();
					clientPort = public_client.getPort();
					
					sender = new ImageSender(public_client);	//傳送螢幕的object
					sender.setPort(clientPort);
					get = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
//=====================================================================			
			while(connected)
			{
				// get message from sender
				try
				{					
					System.out.println("Client:" + listenerAddress);
					// translate and use the message to automate the desktop
					message = in.readLine();

					System.out.println(message);
					
					process_msg(message, out, sender);		//可以的話把sender刪去					
				}catch(Exception e){
					System.out.println(e);
					connected = false;
				}
			}
}
//======================================以上是run=========================================	
		public void process_msg(String message, PrintWriter out, ImageSender sender)
		{
			if (message.equals("Connectivity"))//echo the message back
			{
				System.out.println("Trying to Connect");
				out.println(message); 
			}
			
			else if(message.equals("Connected"))//echo the message back
			{	out.println(message);	}	
			
			else if(message.equals("Close"))	//echo the message back
			{	System.out.println("Controller has Disconnected. Trying to reconnect.");	}	
			
			else if(message.charAt(0) == Constants.REQUESTIMAGE)
			{	
				String[] arr = message.split("" + Constants.DELIMITER);
				System.out.print("Request msg:" + arr[1]+" "+arr[2]+"\n");
				sendImage(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), sender);
			}
			
			else		//使用keyboard
			{
				System.out.print("Touch:" + message);
				bot.handleMessage(message);
			}
		}
		
		public void sendImage(int width, int height, ImageSender sender)
		{
			if(sender != null)
			{
				if(width == 0 && height == 0) 
				{	System.out.println("Receive 0 rectangle");	return;	}
				
				float scale = 0.5f;
				if(width > height) 
				{	scale = ImageSender.SIZETHRESHOLD/width;	}
				else
				{	scale = ImageSender.SIZETHRESHOLD/height;	}
				
				//sender.setImage(bot.getScreenCap((int)Math.round(width*scale), (int)Math.round(height * scale)) );
			
				sender.setImage(bot.getScreenCap(width, height));

				Thread send_image_thread = new Thread(sender);
				send_image_thread.start();
			}
			else
			{	System.out.println("sender 尚未初始化...");	return;	}
		}

		
	}