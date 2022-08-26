package manager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class CreateWhiteBoard {

	public static void main(String[] args) {

		int status = 0;

		try {
			String ipAddress = args[0];
			int portNumber = Integer.parseInt(args[1]);
			if (portNumber <= 1024 || portNumber >= 49151) {
				throw new Exception("[Invalid Port Number] Port number should be between 1024 and 49151.");
			}
			String userName = args[2];

			// Register the manager-side proxy
			IRemoteManager stubM = new RemoteManager(ipAddress, portNumber, userName);
			LocateRegistry.createRegistry(portNumber);
			Naming.rebind("rmi:/" + ipAddress + ":" + portNumber + "/board", stubM);

			// Make the UI visible
			ManagerUI managerUI = stubM.getUI();
			managerUI.getFrame().setVisible(true);

			System.out.println("----------------------CREATE WHITE BOARD----------------------");

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
					"[Lack of Parameters] Please run like \"java -jar CreateWhiteBoard.jar <server-address> <server-port> <user-name>\"");
		} catch (NumberFormatException e) {
			System.out.println("[Invalid Port Number Format] Please input integer as port number.");
		} catch (java.rmi.ConnectException e) {
			status = 1;
			System.out.println("[Connection Failed] Please make sure you start rmiregistry.");
		} catch (ExportException e) {
			status = 1;
			System.out.println("[Connection Refused] Port and address already in use.");
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
