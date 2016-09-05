package Browser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Get_Commands {

	private static String basic_web_link = "";
	
	public Get_Commands(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
	public static Map<String, String> get(JSONObject json_data)
	{ 
	    Map<String, String> commands = new LinkedHashMap();
	
		String command = "";
		String reg_link = "";
		
		int map_links_ptr = 1;
		int array_length = 1;
	    if (!(json_data.isNull("state")))
	    {
	    	JSONObject StateObject = json_data.getJSONObject("state");
			Iterator state_key = StateObject.keys();
			
			while(state_key.hasNext())
			{  
				command = state_key.next().toString();
				commands.put(command, StateObject.getString(command));  
	        }
	    }
	    return commands; 
	} 
}