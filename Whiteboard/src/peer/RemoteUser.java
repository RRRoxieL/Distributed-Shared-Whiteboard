package peer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import manager.IRemoteManager;
import util.HistoryTuple;

/**
 * User side implementation of the remote interface.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class RemoteUser extends UnicastRemoteObject implements IRemoteUser {

	String name; // name of the user who create the stub
	IRemoteManager managerStub; // manager stub which the user wants to use
	PeerUI peerUI; // UI of the user's whiteboard
	List history = new ArrayList(); // list of draw history for synchronizing the whiteboard

	protected RemoteUser(IRemoteManager stubM, String userName) throws RemoteException {
		super();
		this.managerStub = stubM;
		this.name = userName;
		this.peerUI = new PeerUI(this.managerStub, userName);
	}

	@Override
	public void showUI() throws RemoteException {
		this.peerUI.getFrame().setVisible(true);
	}

	@Override
	public void endApplication() throws RemoteException {
		this.peerUI.closeWindow();
	}

	@Override
	public void updateDialog(String name, String chatlog) throws RemoteException {
		this.peerUI.updateDialogue(name, chatlog);
	}

	/**
	 * Offering two approaches to show kick-out information and remain the first one
	 * for neater display.
	 */
	@Override
	public void informKickOut() throws RemoteException {
		System.out.println("You've been kicked out from the whiteboard.");
//		this.peerUI.showKickedInfo();
	}

	@Override
	public void updatePainterArea(boolean isDraw, String drawerName) throws RemoteException {
		this.peerUI.updatePainterArea(isDraw, drawerName);
	}

	@Override
	public void updateCanvas(List<HistoryTuple> history) throws RemoteException {
		this.history = history;
		this.peerUI.getCanvas().loadHistory(history);
		this.peerUI.getCanvas().updateUI();
	}

}
