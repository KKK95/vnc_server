package HTTP_Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class Submit_Form {

	private static String basic_web_link = "";
	
	public Submit_Form(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
//填寫網頁上的表單並送出去, 送出後,網頁會自動跳轉至新網頁, 把新網頁的網址抓下來並return 回去
	public static String post( HttpClient conn_cloud, Map<String, String> form_data) 
		throws ClientProtocolException, IOException
		{
			String url = basic_web_link + form_data.get("post_link");
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

		    post.abort();//釋放post 請求?

		    if (res.getStatusLine().getStatusCode() == 302) 		  	//302 表示轉跳
		    {  	url = res.getLastHeader("Location").getValue();    }  
		    
		    return url;
		  }
}