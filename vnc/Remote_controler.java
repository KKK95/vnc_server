package vnc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Remote_controler implements Runnable{
	
	private Object lock = new Object();
	private boolean sat_receiver = false;
	private boolean sat_sender = false;
	
	private int PORT;
	private InetAddress IP;
	
	private Socket root_client;
	private Socket new_client;
	private Socket client;
	private boolean the_line_is_busy;
	private boolean member_joining;

	BufferedReader receive_client_msg = null;
	PrintWriter send_client_msg = null;
	private static Map<String, String> client_list;
	
	public Remote_controler()
	{	
		Map<String, String> client_list = new HashMap();
	}
	
	//建立新thread 前必需要呼叫此函數來初始化
	public void member_join(Socket socket, String client_name, String client_ip)
	{
		member_joining = true;
		while(the_line_is_busy)System.out.println("線路繁忙中...");
		synchronized(lock)  		//新增client 
        {
			
			System.out.println("client 正在加入會議中...");
			client_list.put(client_name, client_ip);
			
			member_joining = false;
        }						
		broadcast(client_list);		//新增client 後廣播
	}
	
	private void broadcast(Map<String, String> contents)
	{
		
		while(member_joining)System.out.println("client 正在加入會議中...");
		
		synchronized(lock)  		//廣播中
        { 
			the_line_is_busy = true;
			
			the_line_is_busy = false;
        }
	}
	
	private void set_root_client(Socket socket)
	{
		root_client = socket;
		try {
			receive_client_msg = new BufferedReader(new InputStreamReader(root_client.getInputStream()));
			send_client_msg = new PrintWriter(new OutputStreamWriter(root_client.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void receive()			//這裏會開thread 來接受資料, 接收到的資料會做檢查
	{
		
	}
	
	private void send()				//這裏會開thread 去發送資料, 可控制發送的資料/對象
	{
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean receiver_thread = false;
		boolean sender_thread = false;
		
		synchronized(lock) 
        { 
			if (sat_sender == false && sat_receiver == false)
			{
				receiver_thread = true;
				sat_receiver = true;
			}
			else
			{
				sender_thread = true;
				sat_sender = true;
			}
        }
		if (receiver_thread)
		{	receive();	}
		else if (sender_thread)
		{	send();	}
	}

	
	
}
