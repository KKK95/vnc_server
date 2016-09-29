package Browser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Get_Contents {

	public static Map<String, String> get_member_list(JSONObject json_data, String member_name, String ip)
	{
		Map<String, String> member_list = new HashMap();
		
		String content = "";
		String content_array_name = "";
		String reg_content = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
	    if (!(json_data.isNull("content")))
	    {

	    	JSONObject ContentObject = json_data.getJSONObject("content");			//整個大 object
	    	JSONObject content_object;												//大object 裏面的 小object
	    	JSONArray content_object_array;											//小object 裏面的 array
			Iterator Content_Key = ContentObject.keys();
			Iterator content_object_key;
			
			while(Content_Key.hasNext())
			{   
				content = Content_Key.next().toString();
				if ( (content.indexOf("obj_meeting_member_list", 0)) != -1)			//有obj, obj 裏含array
				{
					content_object = ContentObject.getJSONObject(content);
					content_object_key = content_object.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0)
					{		
						while(content_object_key.hasNext())
						{
							content_array_name = content_object_key.next().toString();			//取得array 名字
							content_object_array = content_object.getJSONArray(content_array_name);	//透過名字 取得 array
							reg_content = content_object_array.getString(j);					//從array 中取得元素
							
							if (array_start == 0)
							{	member_name = reg_content;	}
							else if (reg_content.equals(ip))
							{
								member_list.put(member_name, reg_content);
							}
							if ( j == 0 )	array_length = content_object_array.length();
							array_start = 1;
						}
						content_object_key = content_object.keys();
					}
				}
	        }
	    }
	    
		return member_list;
	}
	public static void show(JSONObject json_data)
	{ 
	    Map<String, String> contents = new LinkedHashMap();
	
		String content = "";
		String content_array_name = "";
		String reg_content = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
	    if (!(json_data.isNull("content")))
	    {
	    	System.out.println("-----------------------Contents-----------------------");
	    	JSONObject ContentObject = json_data.getJSONObject("content");
	    	JSONObject content_set;
	    	JSONArray content_set_array;
			Iterator content_key = ContentObject.keys();
			Iterator content_set_key;
			
			while(content_key.hasNext())
			{  
				content = content_key.next().toString();
				if ( (content.indexOf("obj_", 0)) != -1)			//有obj, obj 裏含array
				{
					content_set = ContentObject.getJSONObject(content);
					content_set_key = content_set.keys();
					for (int j = 0, array_start = 0; j < array_length; j++, array_start = 0)
					{		
						while(content_set_key.hasNext())
						{
							content_array_name = content_set_key.next().toString();			//取得array 名字
							content_set_array = content_set.getJSONArray(content_array_name);	//透過名字 取得 array
							reg_content = content_set_array.getString(j);					//從array 中取得元素
							if (array_start == 0)
							{
								System.out.print(reg_content);
								System.out.print("------------------------------------");
							}
							else
							{
								System.out.println("  " + reg_content); 
							}
							if ( j == 0 )	array_length = content_set_array.length();
							array_start = 1;
						}
						content_set_key = content_set.keys();
						System.out.println(" ");
					}
				}
	        }
			System.out.println("-----------------------Contents End-----------------------");
	    }
	    return ; 
	} 
}
