package HTTP_Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class http_request {
	
	private static Submit_Form 		submit_form; 
	private static Upload 			upload;
	private static Download			download;
	
	private static int json_index = 0;
	private static String basic_web_link = "";
	static JSONObject json_web_data;
	
	public http_request(String link) 
	{
		json_web_data = new JSONObject();
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
		
		upload = new Upload(basic_web_link);
		submit_form = new Submit_Form(basic_web_link);
		download = new Download(basic_web_link);
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
	public static JSONObject link( HttpClient conn_cloud, String url) 
	throws ClientProtocolException, IOException 
	{
		url = basic_web_link + url;
		System.out.println("link : " + url);
	    HttpPost post = new HttpPost(url);
	    HttpResponse res = conn_cloud.execute(post);
	    post.abort();
	    while (res.getStatusLine().getStatusCode() == 302) 
	    {   	
	    	url = basic_web_link + res.getLastHeader("Location").getValue();  
	    	post = new HttpPost(url);
		    res = conn_cloud.execute(post);
	    	post.abort();
	    }  
	    BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "utf-8"));
	    post.abort();
	    String data = "";
	    String line = "";
	    while ((line = br.readLine()) != null) 
	    {   data = data + line + '\n';    }
	    System.out.println(data);
		json_index = data.indexOf('{');
		json_web_data = new JSONObject(data.substring(json_index));
	    
	    return json_web_data;
	  }
	
	
	public static String post_submit_form( HttpClient conn_cloud, Map<String, String> form_data) 
	throws ClientProtocolException, IOException 
	{	return submit_form.post(conn_cloud, form_data);	}
	
	public static void upload_file(HttpClient conn_cloud, String url, String file_path) 
	throws Exception
	{	
		upload.upload_file(conn_cloud, url, file_path);
		return;
	}
	
	public static String download_file(HttpClient conn_cloud, String url, String download_file_path) 
	throws Exception
	{	
		url = download.download_file(conn_cloud, url, download_file_path);
		return url;
	}
}


