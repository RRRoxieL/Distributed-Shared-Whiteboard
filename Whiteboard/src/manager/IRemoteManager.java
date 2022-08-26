package manager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import peer.IRemoteUser;
import util.HistoryTuple;

/**
 * Manager side remote interface offering remote control shared between user and
 * manager.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public interface IRemoteManager extends Remote {

	// Get the UI
	public ManagerUI getUI() throws RemoteException;

	// Dispose all users' UI and terminate the system
	public void endApplication() throws RemoteException;

	// Used when a user requests to join the whiteboard, displays a message asking
	// for permission on the manager's UI and obtains the result
	public void requestConnection(String userName) throws Exception;

	// Add a user into the userList, storing the name and stub of the user, and
	// update user-manage-area in manager's UI afterward
	public void addUser(String userName, IRemoteUser drawU) throws RemoteException;

	// Remove the user from the userList and update manager's UI afterward
	public void removeUser(String userName) throws RemoteException;

	// Update the whiteboard, after drawing shapes except triangle
	public void updateCanvas(String drawerName, HistoryTuple tuple) throws RemoteException;

	// Update the whiteboard, after drawing triangle
	public void updateCanvas(String drawerName, List<HistoryTuple> tuples) throws RemoteException;

	// Update the dialogue area of all the user and the manager
	public void updateDialog(String name, String chatlog) throws RemoteException;

	// Kick a user off and inform the user that he/she has been kickedout
	public void kickOutUser(String userName) throws Exception;

	// Get the draw history list stored by the manager
	public List getHistory() throws RemoteException;

	// Remove all the patterns on the whiteboard and sync all user's whietboard
	public void clearCanvas() throws RemoteException;

}
