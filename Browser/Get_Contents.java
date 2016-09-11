package Browser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Get_Contents {

	
	public static void show(JSONObject json_data)
	{ 
	    Map<String, String> commands = new LinkedHashMap();
	
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
