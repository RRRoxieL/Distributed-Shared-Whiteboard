package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import util.HistoryTuple;

/**
 * User side remote interface offering remote control shared between user and
 * manager.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */

public interface IRemoteUser extends Remote {

	// Display the UI
	public void showUI() throws RemoteException;

	// Dispose the UI
	public void endApplication() throws RemoteException;

	// Synchronize the canvas accroding to the history list
	public void updateCanvas(List<HistoryTuple> history) throws RemoteException;

	// Update the painter area that shows the user who operates the canvas, and
	// display the operation of manager clearing the canvas
	public void updatePainterArea(boolean isDraw, String painterName) throws RemoteException;

	// Update the dialogue area accroding to the message and the sender
	public void updateDialog(String name, String message) throws RemoteException;

	// Inform the user that he/she has been kicked off the whiteboard
	public void informKickOut() throws RemoteException;

}
