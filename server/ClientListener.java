package sysats.server;

//protocol timestamp:username:message
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

public class ClientListener extends Thread {
	private ServerDispatcher serverDispatcher;
	private ClientInfo clientInfo;
	private BufferedReader in;

	public ClientListener(ClientInfo clientInfo,
			ServerDispatcher serverDispatcher) throws IOException {
		this.clientInfo = clientInfo;
		this.serverDispatcher = serverDispatcher;
		Socket socket = clientInfo.clientSocket;
		this.in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
	}

	public void run() {
		System.out.println("Logged in.");
		try {
			Date date = new Date();
			while (!isInterrupted()) {
				//pridedam timestampa, kai ateina is serverio zinute
				String text = in.readLine();
				if (text == null)
					break;
				String message = new Timestamp(date.getTime()) + "?" + text;
				serverDispatcher.dispatchMessage(clientInfo, message);
			}
		} catch (IOException e) {
		}
		clientInfo.clientSender.interrupt();
		serverDispatcher.removeClient(clientInfo);
		System.out.println("Logged out.");
	}

}
