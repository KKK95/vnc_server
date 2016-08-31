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

import screen.ImageSender;
import server_config.Constants;
import server_config.ServerListener;
import vnc_mouse.AutoBot;

public class RemoteDataServer extends Thread{
	
		// local settings
		private Socket public_client;
		private byte[] buf;
		private BufferedReader public_in;
		private PrintWriter public_out;
		private int clientPort;
		private InetAddress public_listenerAddress;
		
		private boolean public_connected;
		
		private String message;
		private AutoBot bot;
		private ImageSender sender;

		public RemoteDataServer(Socket socket){
			buf = new byte[1000];
			bot = new AutoBot();
			public_client = socket;
			public_connected = false;
			try {
				public_in = new BufferedReader(new InputStreamReader(public_client.getInputStream()));
				public_out = new PrintWriter(new OutputStreamWriter(public_client.getOutputStream()));
				public_connected = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void run(){
			
			Socket public_client;
			byte[] buf;
			BufferedReader public_in;
			PrintWriter public_out;
			int clientPort;
			InetAddress public_listenerAddress;
			
			boolean public_connected;
			
			while(connected){
				// get message from sender
				try{
				
					// store the packets address for sending images out
					listenerAddress = client.getInetAddress();
					clientPort = client.getPort();
					System.out.println("Client:" + listenerAddress);
					// translate and use the message to automate the desktop
					message = in.readLine();
					
					System.out.println(message);
					
					if (message.equals("Connectivity"))
					{
						System.out.println("Trying to Connect");
						out.println(message); //echo the message back
					}
					
					else if(message.equals("Connected"))
					{
						out.println(message); //echo the message back
					}
					
					else if(message.equals("Close"))
					{
						System.out.println("Controller has Disconnected. Trying to reconnect."); //echo the message back
					}
					
					else if(message.charAt(0) == Constants.REQUESTIMAGE)	//手機要求主機發送瑩幕
					{
						String[] arr = message.split("" + Constants.DELIMITER);
						System.out.print("Request msg:" + arr[1]+" "+arr[2]+"\n");
						sendImage(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
					}
					
					else
					{
						System.out.print("Touch:" + message);
						bot.handleMessage(message);
					}
				}catch(Exception e){
					System.out.println(e);
					connected = false;
				}
			}
		}
		
		public void sendImage(int width, int height){
			if(sender == null)
			{	sender = new ImageSender(client);	}
			
			if(sender != null)
			{
				if(width == 0 && height == 0) {
					System.out.println("Receive 0 rectangle");
					return;
				}
				
				float scale = 0.5f;
				if(width > height) {
					scale = ImageSender.SIZETHRESHOLD/width;
				}else{
					scale = ImageSender.SIZETHRESHOLD/height;
				}
				
				sender.setPort(clientPort);
				//sender.setImage(bot.getScreenCap((int)Math.round(width*scale), (int)Math.round(height * scale)) );
			
				sender.setImage(bot.getScreenCap(width, height));

				Thread send_image_thread = new Thread(sender);
				send_image_thread.start();
			}
		}

		
	}