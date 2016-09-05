package Browser;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

public class Get_Forms 
{

	private static String basic_web_link = "";
	
	public Get_Forms(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
	public static Map<String, String> get(JSONObject json_data)
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
				form = formarray.getJSONObject(reg_form_name);
	
				form_func = form.getString("func");
				form_send_to = form.getString("addr");
				forms.put( Integer.toString(i), form_func);
				forms.put("post_link" + i, form_send_to);
	
				form_textbox = form.getJSONObject("form");
	
				Iterator form_data_key = form_textbox.keys();
				while(form_data_key.hasNext())
				{  
					key = form_data_key.next().toString();
					forms.put(key + i, form_textbox.getString(key));  
		        }  
		        i++;
	        }
	    }
	    
		return forms;
	}
	
	public static void show(JSONObject json_data)
	{	
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
	
				form_textbox = form.getJSONObject("form");
	
				Iterator form_data_key = form_textbox.keys();
				while(form_data_key.hasNext())
				{  
					key = form_data_key.next().toString();
					
					if (form_textbox.getString(key).equals("none"))
						System.out.println(key + " : ");
					else
						System.out.println(key + " : " + form_textbox.getString(key));
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
	    
		return ;
	}
}