package Browser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import HTTP_Request.http_request;
import Browser.Get_Links;
import Browser.Get_Forms;


public class Water_Crab 
{
	private static HttpClient 	conn_cloud 	= 		null; 
	private static http_request request 	= 		null;
	private static Get_Links 	links 		= 		null;
	private static Get_Forms 	forms 		= 		null;
	private static Get_Commands commands 	= 		null;
	private static Get_Contents contents 	= 		null;

	static JSONObject json_web_data;
	private static Scanner scanner;
	
	private static String local_server_ip = "";
	private static String basic_web_link = "";
	private static String default_download_file_path = "";
	
	private static Map<String, String> link = new LinkedHashMap();
    private static Map<String, String> form_data = new LinkedHashMap();
    private static Map<String, String> fill_in_the_form_data = new LinkedHashMap();
    private static Map<String, String> command = new LinkedHashMap();
    
	public Water_Crab(String link, String download_path, String ip) 
	{
		conn_cloud = new DefaultHttpClient();			//初始化此function 的http 連接
		
		json_web_data = new JSONObject();
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
		
		local_server_ip = ip;
		
		if (download_path == null)
			default_download_file_path = "D:\\";
		else 
			default_download_file_path = download_path;
		
		scanner = new Scanner(System.in);
		http_request request = new http_request(basic_web_link);
		Get_Links links = new Get_Links(basic_web_link);
		Get_Forms forms = new Get_Forms(basic_web_link);
		Get_Forms commands = new Get_Forms(basic_web_link);
		Get_Contents contents = new Get_Contents();
	}
	
private static void update_web_data (JSONObject json)
{
    //過濾網頁內容並取出所有連接
    link = links.get(json);
    
    //過濾網頁內容, 並取得表單內的所有欄位和送表單的link
    form_data = forms.get(json);

    //過濾網頁內容, 並取得所有 state
    command = commands.get(json);
    
	return;
}

public static void link(String url)
{
	try {
		json_web_data = request.link( conn_cloud, url);
		update_web_data(json_web_data);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	return;
}

public static void show_json_data()
{
	System.out.println(json_web_data);
	return;
}

public static void show_web_data()
{
    //顯示網頁中所有連接
    links.show(json_web_data);
    
    //顯示網頁中的內容
    contents.show(json_web_data);
       
    //顯示網頁中的所有表單內的所有欄位
    forms.show(json_web_data);
	return;
}

public static int input() 
throws ClientProtocolException, IOException 
{
    int choose = 0;
    String new_url = "";
//==================================================================================
       
    //然後看要點選連接還是提交表單
    if ( !(link.isEmpty()) && !(form_data.isEmpty()))
    {
    	System.out.println("請選擇要提交表單還是按連接 (按0 後再選擇表單)"); 
    	choose = scanner.nextInt();
    	scanner.nextLine();
    }
    else if (link.isEmpty())    {	choose = 0;    }		//沒有link
    else if (form_data.isEmpty())							//沒有form
    {
    	System.out.println("輸入數字選擇連結 : "); 
    	choose = scanner.nextInt();
    	scanner.nextLine();
    }
    
    if (choose > 0)			//選link
    { 	return choose;	    }
    else					//選表單
    {
    	if ( form_data.get("1") != null )
    	{
    		System.out.println("請選擇要填寫的表單 : "); 
	    	choose = scanner.nextInt();
	    	scanner.nextLine();
    	}
    	else
    	{	choose = 0;	}
    	
    	System.out.println("請填寫表單 : "); 
    	
    	fill_in_the_form_data = fill_in_the_form(form_data, choose);	//選好表單後填寫表單
    	
    	return 0;
    }
}

public static String chick_link(int link_num)
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
    Map<String, String> fill_data = new LinkedHashMap();
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
		        {	fill_data.put(key, input);    }
    		}
    		else if (start >= 3  && key.equals("ip") && local_server_ip != "")//這個是跟雲端要求開始會議時
    		{	fill_data.put(key, local_server_ip);	}	//向雲端提供local server的ip
    }
	fill_data.put("post_link", form_data.get("post_link" + choose));
	return fill_data;
}

public static String post_submit_form() 
throws ClientProtocolException, IOException
{
	String new_url = "";
	new_url = request.post_submit_form(conn_cloud, fill_in_the_form_data);	
	//post_submit_form() 是提交表單的func, 
	//提交表單後雲端會自動轉跳到一個 新頁面
	//而此時就是要取得 新頁面的連接
	return new_url;
}

public static Map<String, String> get_commond()
{	return command;	}

public static void upload_file(String url, String file_path) 
throws Exception
{
	request.upload_file(conn_cloud, url, file_path);
	return;
}

public static String download_file(String url, String download_file_path)
throws Exception
{
	if (download_file_path == null)
		download_file_path = default_download_file_path;
	return request.download_file(conn_cloud, url, download_file_path);
}

}