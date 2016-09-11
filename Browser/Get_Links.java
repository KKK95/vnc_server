package Browser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import HTTP_Request.Submit_Form;

public class Get_Links {

	private static String basic_web_link = "";
	
	public Get_Links(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
	public static Map<String, String> get(JSONObject json_data)
	{ 
	    Map<String, String> links = new LinkedHashMap();
	
		String reg_link_name = "";
		String reg_link = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
	    if (!(json_data.isNull("link")))
	    {
	    	JSONObject LinkObject = json_data.getJSONObject("link");
	    	JSONObject link_set;
	    	JSONArray link_set_array;
			Iterator link_key = LinkObject.keys();
			Iterator link_set_key;
			
			while(link_key.hasNext())
			{  
				reg_link_name = link_key.next().toString();
				if ( (reg_link_name.indexOf("obj_", 0)) != -1)			//有obj, obj 裏含array
				{
					link_set = LinkObject.getJSONObject(reg_link_name);
					link_set_key = link_set.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0)
					{		
						while(link_set_key.hasNext())
						{
							reg_link_name = link_set_key.next().toString();			//取得array 名字
							link_set_array = link_set.getJSONArray(reg_link_name);	//透過名字 取得 array
							reg_link = link_set_array.getString(j);					//從array 中取得元素
							if (array_start != 0)
							{
								links.put(Integer.toString(map_links_ptr), reg_link); 
								map_links_ptr++ ;
							}
							if ( j == 0 )	
							{	array_length = link_set_array.length();	}			//得知object 裏面一條array 有多長
							
							array_start = 1;
						}
						link_set_key = link_set.keys();
					}
				}
				else
				{
					reg_link = LinkObject.getString(reg_link_name);
					links.put(Integer.toString(map_links_ptr), reg_link); 
					map_links_ptr++ ;
				}
				
	        }
	    }
	    return links; 
	} 
	
	public static void show(JSONObject json_data)
	{ 
		String reg_link_name = "";
		String reg_link = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
	    System.out.println("-----------------------link-----------------------");
	    if (!(json_data.isNull("link")))
	    {
	    	JSONObject linkObject = json_data.getJSONObject("link");
	    	JSONObject link_set;
	    	JSONArray link_set_array;
			Iterator link_key = linkObject.keys();
			Iterator link_set_key;
			
			while(link_key.hasNext())
			{  
				reg_link_name = link_key.next().toString();
				if ( (reg_link_name.indexOf("obj_", 0)) != -1)			//有obj, obj 裏含array
				{
					link_set = linkObject.getJSONObject(reg_link_name);
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
					System.out.println("  " + map_links_ptr + "." + reg_link_name); 
					reg_link = linkObject.getString(reg_link_name);
					map_links_ptr++ ;
				}
				
	        }
	    }
	    else	System.out.println("此版面沒有超連結");
	    System.out.println(" ");
	    System.out.println("---------------------link-end!--------------------");
	    return ; 
	} 
}