package manager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import peer.IRemoteUser;
import util.HistoryTuple;

/**
 * Manager side implementation of the remote interface.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class RemoteManager extends UnicastRemoteObject implements IRemoteManager {

	String name;// name of the manager who create the stub
	int port; // port number of the RMI
	String ip;// ip address of the RMI
	ManagerUI managerUI; // UI for the manager's whiteboard
	Map<String, IRemoteUser> userList = new HashMap<String, IRemoteUser>(); // user list that stores the name and stub
																			// of joined users
	List<HistoryTuple> historyList = new ArrayList(); // list of draw history for synchronizing the whiteboard

	protected RemoteManager(String ipAddress, int portNumber, String userName) throws RemoteException {
		super();
		this.port = portNumber;
		this.ip = ipAddress;
		this.name = userName;
	}

	@Override
	public void addUser(String userName, IRemoteUser userStub) throws RemoteException {
		this.userList.put(userName, userStub);
		this.managerUI.updateUserList(this.userList.keySet().toArray());
	}

	@Override
	public void removeUser(String userName) throws RemoteException {
		this.userList.remove(userName);
		this.managerUI.updateUserList(this.userList.keySet().toArray());
	}

	@Override
	public List getHistory() throws RemoteException {
		return this.managerUI.getCanvas().getHistory();
	}

	@Override
	public void updateCanvas(String drawerName, HistoryTuple tuple) throws RemoteException {
		this.historyList.add(tuple);
		Iterator<String> iterator = userList.keySet().iterator();
		while (iterator.hasNext()) {
			String userName = iterator.next();
			userList.get(userName).updateCanvas(this.historyList);
			userList.get(userName).updatePainterArea(true, drawerName);
		}
		this.managerUI.getCanvas().loadHistory(this.historyList);
		this.managerUI.getCanvas().updateUI();

	}

	@Override
	public void updateCanvas(String drawerName, List<HistoryTuple> tuples) throws RemoteException {
		this.historyList.addAll(tuples);
		Iterator<String> iterator = userList.keySet().iterator();
		while (iterator.hasNext()) {
			String userName = iterator.next();
			userList.get(userName).updateCanvas(this.historyList);
			userList.get(userName).updatePainterArea(true, drawerName);
		}

		this.managerUI.getCanvas().loadHistory(this.historyList);
		this.managerUI.getCanvas().updateUI();
	}

	@Override
	public void clearCanvas() throws RemoteException {
		this.historyList.clear();
		Iterator<String> iterator = userList.keySet().iterator();
		while (iterator.hasNext()) {
			String userName = iterator.next();
			userList.get(userName).updateCanvas(this.historyList);
			userList.get(userName).updatePainterArea(false, this.name);
		}
		this.managerUI.getCanvas().loadHistory(this.historyList);
		this.managerUI.getCanvas().updateUI();
	}

	@Override
	public ManagerUI getUI() throws RemoteException {
		this.managerUI = new ManagerUI(this, this.name);
		return managerUI;
	}

	@Override
	public void endApplication() throws RemoteException {
		Iterator<String> iterator = userList.keySet().iterator();
		while (iterator.hasNext()) {
			String userName = iterator.next();
			userList.get(userName).endApplication();
		}
	}

	@Override
	public void updateDialog(String name, String message) throws RemoteException {
		Iterator<String> iterator = userList.keySet().iterator();
		while (iterator.hasNext()) {
			String userName = iterator.next();
			userList.get(userName).updateDialog(name, message);
		}
		this.managerUI.updateDialogue(name, message);

	}

	@Override
	public void requestConnection(String userName) throws Exception {
		if (userList.containsKey(userName) || userName.equals(this.name)) {
			throw new Exception("[Repeated Name] This name already in used, please change another name.");
		} else {
			int result = this.managerUI.showJoinRequest(userName);
			if (result == 1) {
				throw new Exception("[Request Rejected] The manager rejected your join request.");
			} else if (result == 2) {
				throw new Exception("[Unresponsed] The manager didn't response to your join request.");
			}
		}
	}

	@Override
	public void kickOutUser(String userName) throws RemoteException, java.io.EOFException {
		userList.get(userName).informKickOut();
		userList.get(userName).endApplication();
		removeUser(userName);
	}

}
