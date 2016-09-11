package HTTP_Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class Upload {
	
	private static String basic_web_link = "";
	
	public Upload(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	//	basic_web_link = "http://localhost:8080/meeting_cloud/device/";
	}
	
	public static String upload_file (HttpClient conn_cloud, String url, String file_path) 
	throws Exception 
	{
		url = basic_web_link + url;
		System.out.println(url);
		HttpPost post = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create(); 
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		File file = new File(file_path);			//透過路徑找出該檔案
		
		if(file.exists())
		{	
			builder.addBinaryBody("fileToUpload", file, ContentType.DEFAULT_BINARY, file_path);
			builder.addTextBody("method", "post");
			builder.addTextBody("fileTypes", "pdf"); 
			HttpEntity entity = builder.build();  
			post.setEntity(entity);
			HttpResponse response = conn_cloud.execute(post);
		
		// 輸出訊息
			BufferedReader reader = new BufferedReader(new InputStreamReader(
	                response.getEntity().getContent(), "UTF-8"));
	        String sResponse;
	        String s = "";
	
	        while ((sResponse = reader.readLine()) != null) {
	            s = s + sResponse + '\n';
	        }
	        return s;
		}
		else
		{	return "file not found!";	}
		
	 //   return "";
	 }
}
