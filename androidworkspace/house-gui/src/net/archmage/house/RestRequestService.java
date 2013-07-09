package net.archmage.house;

public class RestRequestService {

	private String server;
	
	private int port;

	public RestRequestService(String server, int port) {
		super();
		this.server = server;
		this.port = port;
	}

	public void rest(String request) {
		RestRequestTask task = new RestRequestTask(server, port);
		task.execute(request);
	}
	
}
