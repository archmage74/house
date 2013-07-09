package net.archmage.house;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

public class RestRequestTask extends AsyncTask<String, Void, String> {

	private String server;
	private int port;
	
	public RestRequestTask(String server, int port) {
		super();
		this.server = server;
		this.port = port;
	}

	@Override
	protected String doInBackground(String... params) {
		performRestRequest(params[0]);
		return null;
	}

	protected void performRestRequest(String request) {
		URL url;
		try {
			url = new URL("http", server, port, request);
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
