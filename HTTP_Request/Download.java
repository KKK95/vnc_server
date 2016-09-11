package HTTP_Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class Download {

	private static String basic_web_link = "";
	
	private static final int cache = 10 * 1024;
	public Download(String link) 
	{
		if (link == null)
			basic_web_link = "http://localhost:8080/meeting_cloud/device/";
		else 
			basic_web_link = link;
	}
	
	public static String download_file(HttpClient conn_cloud, String url, String download_file_path) 
	throws UnsupportedOperationException, IOException
	{
		url = basic_web_link + url;
//		HttpGet httpget = new HttpGet(url);
		HttpPost httppost = new HttpPost(url);
		System.out.println(url);
	    HttpResponse response = conn_cloud.execute(httppost);  
	    
	    HttpEntity entity = response.getEntity();  				
	    InputStream is = entity.getContent();  					
	  
	    download_file_path = download_file_path + get_file_name(response);  //取得下載檔案的路徑 + 檔案名稱
	    File file = new File(download_file_path);  
	    file.getParentFile().mkdirs();  
	    FileOutputStream file_output = new FileOutputStream(file);  
        
	    byte[] buffer=new byte[cache];  
	    int ch = 0;  
	    while ((ch = is.read(buffer)) != -1) {  							//讀取檔案
	        file_output.write(buffer,0,ch);  
	    }  
	    is.close();  
	    file_output.flush();  
	    file_output.close(); 
        
	    if (response.getStatusLine().getStatusCode() == 302) 		  	//302 表示轉跳
	    {  	url = response.getLastHeader("Location").getValue();    }  
	    System.out.println("url : " + url);
	    
	    return url;
	}
	
	private static String get_file_name(HttpResponse response) {  
        Header contentHeader = response.getFirstHeader("Content-Disposition");  
        String filename = null;  
        if (contentHeader != null) {  
            HeaderElement[] values = contentHeader.getElements();  
            if (values.length == 1) {  
                NameValuePair param = values[0].getParameterByName("filename");  
                if (param != null) {  
                    try {  
                        //filename = new String(param.getValue().toString().getBytes(), "utf-8");  
                        //filename=URLDecoder.decode(param.getValue(),"utf-8");  
                        filename = param.getValue();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
        return filename;  
    }  
}
