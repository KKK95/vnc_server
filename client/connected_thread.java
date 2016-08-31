package client;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.net.Socket;

import vnc_mouse.Mouse;
import vnc_mouse.AutoBot;

public class connected_thread extends Thread 
{
//	private static AutoBot mouse;
	private static Mouse mouse;
	private static int pid = 0;
	private Socket client;
	Lock lock = null;  
	public connected_thread(Socket socket, int i)
	{
	//	mouse = new AutoBot();
		mouse = new Mouse();
		this.client = socket;   
//		this.lock = lock;  
		pid = i;
	}
	
	public void run()
	{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			String line;
			while(true)
			{
//					lock.lock();  
					System.out.println(pid + "號機");
					System.out.println(this.client.getInetAddress().toString()+"read");
					System.out.println(pid + "號機");
					line = in.readLine(); //read input from client
					System.out.println(line); //print whatever we get from client
//					lock.unlock();  
					
					//Exit if user ends the connection
					if(line.equalsIgnoreCase("exit"))
					{
						//Close server and client socket
						client.close();
					}
					else if (line != null && line.trim().length() != 0)
					{
						mouse.mouse_control(client, line);
			//			AutoBot.handleMessage(line);
					}
			}
		} catch (IOException e) {
			try {	client.close();	} 
			catch (IOException e1) {	e1.printStackTrace();	}
			System.out.println("Read failed");
			
			System.exit(-1);
        }
	}
	
}
