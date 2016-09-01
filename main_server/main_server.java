package main_server;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/*
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.HttpURLConnection;  
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vnc.RemoteDataServer;
*/
import vnc.vnc_server;

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
import org.json.JSONException;

@SuppressWarnings("deprecation")
public class main_server {
	private static String web_link = "";
	private static String basic_web_link = "";
	private static String web_data = "";
	private static HttpClient conn_cloud = null; 
	private static Scanner scanner;
	private static String command = "";
	private static vnc_server local_vnc_server_mouse;
	private static vnc_server local_vnc_server_screen;
	
	private static String local_server_ip = "192.168.137.1";	//你的會議主機ip, 自己設定
	
	private static final int SERVER_MOUSE_PORT = 6060;
	private static final int SERVER_SCREEN_PORT = 6080;
/*
	private LinkedHashMap map;
	
	public void json_web_data() {    
        this.map = new LinkedHashMap();  //new HashMap();    
}  
*/
	public static void main(String[] args) 
	{
		conn_cloud = new DefaultHttpClient();			//初始化此function 的http 連接

		web_link = "http://localhost:8080/meeting_cloud/device/index.php";	//一開始預設是去登錄頁面
		basic_web_link = "http://localhost:8080/meeting_cloud/device/";		//雲端網頁放在這邊
		int json_index;
		
		scanner = new Scanner(System.in);

		try {
			while (true)
			{
		        System.out.println("-----------------------------------new web!-----------------------------------");
		        System.out.println("");
		        
				web_data = request(web_link);	
				//連結到該網頁, 並拿到網頁中的資料(request() 是不會輸出或作任何處理)
				
				System.out.println(web_data);
				json_index = web_data.indexOf('{');

				JSONObject json_web_data = new JSONObject(web_data.substring(json_index));
				
				
				
				//=====================================================================================		
				web_link = active(basic_web_link, json_web_data);//網頁資料都送進來處理, 把資料過濾後		|
																//並讓使用者查看網頁內容					|
																//使用超連結或提交表單後, 返回超連結		|
				//=====================================================================================			
				
				
				command_process ();	//讓手機連到主機的func, 有甚麼需要就自己設定吧
				//command_process 主要是根據command 來執行不同的工作
				
				command = "";		
				//而command 是透過active 接收雲端送過來的訊息, 當command_process處理完後便會清除 
				
				System.out.println("");
		        System.out.println("-----------------------------------end web!-----------------------------------");
		        System.out.println("");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

			//透過url 網址連到雲端,只做連接和接收雲端送來的data, 不會對data 做任何處理
//==========================================================================================
	
	//連到url, 並取得該網頁的資料 (http)
	public static String request(String url) 
			throws ClientProtocolException, IOException 
	{
	    HttpPost post = new HttpPost(url);
	    HttpResponse res = conn_cloud.execute(post);
	    post.abort();
	    while (res.getStatusLine().getStatusCode() == 302) 
	    {   	
	    	url = basic_web_link + res.getLastHeader("Location").getValue();  
	    	System.out.println(url);
	    	post = new HttpPost(url);
		    res = conn_cloud.execute(post);
	    	System.out.println(url);
	    	post.abort();
	    }  
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "utf-8"));
	    post.abort();
	    String data = "";
	    String line = "";
	    while ((line = br.readLine()) != null) 
	    {   data = data + line + '\n';    }
	    
	    return data;
	  }
	
	//把網頁的資料(json) 送到這邊, 讓使用者可以使用網頁的 超連結 或 提交表單
	public static String active(String url, JSONObject json_web_data) 
			throws ClientProtocolException, IOException 
	{
	    Map<String, String> link = new LinkedHashMap();
	    Map<String, String> form_data = new LinkedHashMap();
	    Map<String, String> fill_in_the_form_data = new LinkedHashMap();
	    int choose = 0;
	    
	    String new_url = "";
//==================================================================================
	    //過濾網頁內容並取出所有連接
	    link = get_link(json_web_data);
	    
	    
	    //過濾網頁內容, 並取得表單內的所有欄位和送表單的link
	    form_data = get_form(json_web_data);
	    
	    
	    //然後看要點選連接還是提交表單
	    if ( !(link.isEmpty()) && !(form_data.isEmpty()))
	    {
	    	System.out.println("請選擇要提交表單還是按連接 (按0 後再選擇表單)"); 
	    	choose = scanner.nextInt();
	    }
	    else if (link.isEmpty())    {	choose = 0;    }		//沒有link
	    else if (form_data.isEmpty())							//沒有form
	    {
	    	System.out.println("輸入數字選擇連結 : "); 
	    	choose = scanner.nextInt();
	    	scanner.nextLine();
	    }
	    
	    if (choose > 0)			//選link
	    { 	new_url = chick_link(link, choose);	    }
	    else					//選表單
	    {
	    	if ( form_data.get("2") != null )
	    	{
	    		System.out.println("請選擇要填寫的表單 : "); 
		    	choose = scanner.nextInt();
		    	scanner.nextLine();
	    	}
	    	else
	    		choose = 0;
	    	System.out.println("請填寫表單 : "); 
	    	
	    	fill_in_the_form_data = fill_in_the_form(form_data, choose);	//選好表單後填寫表單
	    	new_url = submit_form_post(fill_in_the_form_data);	//submit_form_post() 是提交表單的func, 
	    											//提交表單後雲端會自動轉跳到一個 新頁面
	    											//而此時就是要取得 新頁面的連接
	    }
	    if (new_url == url)
	    {	System.out.println("沒有取得任何網址 ! "); }
	    return new_url;
	}
	
	//把網頁的資料過濾, 把超連結都抓出來放進map 內, 然後return map 
	public static Map<String, String> get_link(JSONObject json_data)
	{ 
        Map<String, String> links = new LinkedHashMap();

		String reg_link_name = "";
		String reg_link = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
        System.out.println("-----------------------link-----------------------");
        if (!(json_data.isNull("link")))
        {
        	JSONObject linkArray = json_data.getJSONObject("link");
        	JSONObject link_set;
        	JSONArray link_set_array;
			Iterator link_key = linkArray.keys();
			Iterator link_set_key;
			
			while(link_key.hasNext())
			{  
				reg_link_name = link_key.next().toString();
				if ( (reg_link_name.indexOf("obj", 0)) != -1)			//有obj, obj 裏含array
				{
					link_set = linkArray.getJSONObject(reg_link_name);
					link_set_key = link_set.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0)
					{		
						while(link_set_key.hasNext())
						{
							reg_link_name = link_set_key.next().toString();			//取得array 名字
							link_set_array = link_set.getJSONArray(reg_link_name);	//透過名字 取得 array
							reg_link = link_set_array.getString(j);					//從array 中取得元素
							if (array_start == 0)
							{
								System.out.print(reg_link);
								System.out.print("------------------------------------");
							}
							else
							{
								System.out.print("  " + map_links_ptr + "." + reg_link_name); 
								links.put(Integer.toString(map_links_ptr), basic_web_link + reg_link); 
								map_links_ptr++ ;
							}
							if ( j == 0 )	array_length = link_set_array.length();
							array_start = 1;
						}
						link_set_key = link_set.keys();
						System.out.println(" ");
					}
				}
				else
				{
					System.out.print("  " + map_links_ptr + "." + reg_link_name); 
					reg_link = linkArray.getString(reg_link_name);
					links.put(Integer.toString(map_links_ptr), basic_web_link + reg_link); 
					map_links_ptr++ ;
				}
				
	        }
        }
        else	System.out.println("此版面沒有超連結");
        System.out.println(" ");
        System.out.println("---------------------link-end!--------------------");
        return links; 
    } 
	
	//把網頁的資料過濾, 把表單的欄位都抓出來放進map 內, 然後return map 
	public static Map<String, String> get_form(JSONObject json_data)
	{
		Map<String, String> forms = new LinkedHashMap();
		
		String form_func = "";
		String form_send_to = "";
		String key = "";
        String reg_form_name = "";
		
        if (!(json_data.isNull("form")))
        {
        	JSONObject formarray = json_data.getJSONObject("form");
        	JSONObject form;
        	JSONObject form_textbox;
			Iterator form_key = formarray.keys();
			
			while(form_key.hasNext())
			{  
				int i = 0;
				reg_form_name = form_key.next().toString();
		        System.out.println("");
		        System.out.println("-----------------------form " + i + "-----------------------");
		        System.out.println("");
				form = formarray.getJSONObject(reg_form_name);

				form_func = form.getString("func");
				System.out.println(form_func);
				form_send_to = form.getString("addr");
				forms.put( Integer.toString(i), form_func);
				forms.put("post_link" + i, basic_web_link + form_send_to);

				form_textbox = form.getJSONObject("form");

				Iterator form_data_key = form_textbox.keys();
				while(form_data_key.hasNext())
				{  
					key = form_data_key.next().toString();
					
					if (form_textbox.getString(key).equals("none"))
						System.out.println(key + " : ");
					else
						System.out.println(key + " : " + form_textbox.getString(key));
					forms.put(key + i, form_textbox.getString(key));  
		        }  
		        System.out.println(" ");
		        System.out.println("---------------------form-end!--------------------");
		        i++;
	        }
        }
        else	
        {
        	System.out.println("此版面沒有表單");
        	System.out.println("---------------------form-end!--------------------");
        }
        
		return forms;
	}
	
	//填寫網頁上的表單並送出去, 送出後,網頁會自動跳轉至新網頁, 把新網頁的網址抓下來並return 回去
	public static String submit_form_post(Map<String, String> form_data) 
		throws ClientProtocolException, IOException
		{
			String url = form_data.get("post_link");
		    HttpPost post = new HttpPost(url);
		    
		    ArrayList<NameValuePair> post_form = new ArrayList<NameValuePair>();
		    for(Map.Entry<String, String> entry:form_data.entrySet())		//用map 記錄表單所有資料, 並把所有資料拿出來
		    {   
			    	if (entry.getKey() != "post_link")
			    	post_form.add( new BasicNameValuePair( entry.getKey(), entry.getValue() ));	
			    	System.out.println(entry.getKey()+"--->" + entry.getValue());
		    }   
		    post.setEntity( new UrlEncodedFormEntity(post_form, "UTF-8"));

		    HttpResponse res = conn_cloud.execute(post);
		    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		    String line = "";
		    while ((line = br.readLine()) != null) 
		    {	command = command + line;    }
		    
		    post.abort();//釋放post 請求?
		    
		    System.out.println("receive command : " + command);

		    if (res.getStatusLine().getStatusCode() == 302) 		  	//302 表示轉跳
		    {  
		    	url = res.getLastHeader("Location").getValue();  
		    	System.out.println(basic_web_link + url);
		    }  
		    
		    return basic_web_link + url;
		  }
	
	public static String chick_link(Map<String, String> link, int link_num)
	{
		int i = 1;
		String new_url = "";
    	for(Map.Entry<String, String> entry:link.entrySet())		//用map 記錄所有超連結, 
	    {   														//並按輸入的號碼選擇超連結
	        if (link_num == i)
	        {	
	        	System.out.println(entry.getKey()+"--->" + entry.getValue());  
	        	new_url = entry.getValue() ;	
	        	break;	
	        }
	        else	i++;
	    }
		return new_url;
	}
	
	public static Map<String, String> fill_in_the_form(Map<String, String> form_data, int choose)
	{
	    Map<String, String> fill_in_the_form_data = new LinkedHashMap();
		String input = "";
		int start = 0;
		String key = "";

		for(Map.Entry<String, String> entry:form_data.entrySet())		//用map 記錄表單所有資料, 
	    {   
    		if (entry.getKey().equals(Integer.toString(choose)) || start > 0 )	//並讓使用者輸入所有資料
    		{	start = start + 1;	}
    		else if (entry.getKey().equals(Integer.toString(choose + 1)))
    		{	break; 	}
    			key = entry.getKey().substring(0,entry.getKey().length() - 1);;
    			
	    		if (start >= 3 && !(key.equals("ip")))
	    		{
			        System.out.println(key + " : "); 
			        input = scanner.nextLine();
			        if ( !(input.isEmpty()) )
			        {	fill_in_the_form_data.put(key, input);    }
	    		}
	    		else if (start >= 3  && key.equals("ip") && local_server_ip != "")//這個是跟雲端要求開始會議時
	    		{	fill_in_the_form_data.put(key, local_server_ip);	}	//向雲端提供local server的ip
	    }
		fill_in_the_form_data.put("post_link", form_data.get("post_link" + choose));
		return fill_in_the_form_data;
	}
	
	public static boolean command_process ()
	{
		boolean success = true;
		if (command == "")	return success;
		if ( (command.indexOf("vnc_start", 0)) != -1)
		{
//			vnc_server local_vnc_server = new vnc_server();	//登錄雲端後才可以始動vnc server
			
			local_vnc_server_mouse = new vnc_server(SERVER_MOUSE_PORT, local_server_ip);
			local_vnc_server_mouse.start();
			
			local_vnc_server_screen = new vnc_server(SERVER_SCREEN_PORT, local_server_ip);
			local_vnc_server_screen.start();
	//		local_vnc_server.start();
		}
		command = "";
		return success;
	}
	
}

