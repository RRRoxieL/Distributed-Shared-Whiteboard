package peer;

import java.net.MalformedURLException;
import java.rmi.Naming;

import manager.IRemoteManager;

public class JoinWhiteBoard {

	public static void main(String[] args) {

		int status = 0;
		try {

			String ipAddress = args[0];
			int portNumber = Integer.parseInt(args[1]);
			if (portNumber <= 1024 || portNumber >= 49151) {
				throw new Exception("[Invalid Port Number] Port number should be between 1024 and 49151.");
			}
			String userName = args[2];

			// Get the proxy of the manager
			IRemoteManager stubM = (IRemoteManager) Naming.lookup("rmi:/" + ipAddress + ":" + portNumber + "/board");

			// Ask to join the whiteboard
			stubM.requestConnection(userName);

			System.out.println("----------------------JOIN WHITE BOARD----------------------");

			// Register the user-side proxy
			IRemoteUser stubU = new RemoteUser(stubM, userName);
			Naming.rebind("rmi:/" + ipAddress + ":" + portNumber + "/board/" + userName, stubU);

			// Add itself to the userlist
			stubM.addUser(userName, stubU);

			// Make the UI visible
			stubU.showUI();

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
					"[Lack of Parameters] Please run like \"java -jar JoinWhiteBoard.jar <server-address> <server-port> <user-name>\"");
		} catch (NumberFormatException e) {
			System.out.println("[Invalid Port Number Format] Please input integer as port number.");
		} catch (java.rmi.ConnectException e) {
			status = 1;
			System.out.println("[Connection Refused] Please make sure the address and port are available.");
		} catch (MalformedURLException e) {
			status = 1;
			System.out.println("[Malformed URL] URL unvailable.");
		} catch (Exception e) {
			status = 1;
			System.out.println(e.getMessage());
		}

		if (status != 0) {
			System.exit(status);
		}
	}

}
