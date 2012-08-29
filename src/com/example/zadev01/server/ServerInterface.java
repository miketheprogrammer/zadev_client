package com.example.zadev01.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.provider.Settings.Secure;

public class ServerInterface {

	public ServerInterface(){
		
	}
	//
	public void CreateUser() throws ClientProtocolException, IOException{
	    HttpClient httpclient = new DefaultHttpClient();
	    
	    HttpResponse response = this.getHttpClient().execute(this.getPost("http://ec2-107-20-31-89.compute-1.amazonaws.com:8888/user/create", null));

	}
	public HttpClient getHttpClient(){
		return new DefaultHttpClient();
	}
	public HttpPost getPost(String url, List<NameValuePair> data){
		HttpPost post = new HttpPost(url);
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("username", "12345"));
	        nameValuePairs.add(new BasicNameValuePair("device_uid", UUID.randomUUID().toString()));
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        return post;
	        
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	return null;
	    }
	}
}
