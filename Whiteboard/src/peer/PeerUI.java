package peer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import manager.IRemoteManager;
import manager.ManagerUI;
import util.Canvas;
import util.HistoryTuple;
import util.Tool;

/**
 * UI for the normal whiteboard user.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class PeerUI {

	private JFrame frame;
	private Canvas panel1;
	private IRemoteManager managerStub;
	private JTextArea dialogueArea;
	private JTextArea painterArea;
	private JTextField chatField;
	private String name;

	public JFrame getFrame() {
		return this.frame;
	}

	public Canvas getCanvas() {
		return this.panel1;
	}

	// Invoke the manager to remove the user from the system
	public void invokeUserRemoval() throws Exception {
		this.managerStub.removeUser(name);
	}

	// Invoke the manager to update to update display afterward
	public void invokeDialogUpdate(String message) throws RemoteException {
		this.managerStub.updateDialog(this.name, message);
	}

	public void closeWindow() {
		frame.dispose();
	}

	public void updateDialogue(String userName, String message) {
		dialogueArea.append(userName + ": " + message + "\n");
		dialogueArea.updateUI();
		dialogueArea.setCaretPosition(dialogueArea.getText().length());
	}

	public void updatePainterArea(boolean isDraw, String userName) {
		if (isDraw) {
			painterArea.append(userName + " is drawing...\n");
		} else {
			painterArea.append("* " + userName + " clears the canvas *\n");
		}
//		painterArea.append(userName + " is drawing...\n");
		painterArea.updateUI();
		painterArea.setCaretPosition(painterArea.getText().length());
	}

	public void showKickedInfo() throws RemoteException {
		JOptionPane.showInternalMessageDialog(null, "You have been kicked out by the manager", "Message", 3);
	}

	// Create whiteboard UI
	public PeerUI(IRemoteManager stubM, String userName) {
		this.managerStub = stubM;
		this.name = userName;
		initialize();

		List<HistoryTuple> drawLog;
		try {
			drawLog = managerStub.getHistory();
			if (drawLog != null) {
				panel1.loadHistory(drawLog);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Initialize the contents of the frame
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					invokeUserRemoval();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.setTitle("White Board");
		frame.setSize(1070, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		// ********************** Draw Panel **********************
		JPanel drawPanel = new JPanel();
		drawPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		drawPanel.setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(drawPanel, BorderLayout.CENTER);

		// ------------------------ Canvas -------------------------
		panel1 = new Canvas(this.name);
		panel1.setDM(managerStub);
		drawPanel.add(panel1);

		// ********************** Tools Panel **********************

		JPanel toolsPanel = new JPanel();
		toolsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.getContentPane().add(toolsPanel, BorderLayout.WEST);

		// ------------------------ Tool Buttons --------------------
		// For users to pick shape/type they want to draw
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(6, 1, 5, 5));

		String[] tools = Tool.getTools();
		for (int i = 0; i < tools.length; i++) {
			JButton button = new JButton(new ImageIcon(ManagerUI.class.getResource(tools[i])));
			button.setActionCommand(tools[i].substring(7, tools[i].lastIndexOf(".")));
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel1.setTool(Tool.valueOf(e.getActionCommand().toUpperCase()));
				}
			});
			panel2.add(button);
		}
		toolsPanel.add(panel2);

		// ********************** Color Panel **********************
		Panel colorPanel = new Panel();
		colorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.getContentPane().add(colorPanel, BorderLayout.SOUTH);

		JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayout(2, 8, 0, 0));

		// --------------------- Color Buttons ---------------------
		// For users to pick color
		Color[] palette = Tool.getPalette();
		for (int i = 0; i < palette.length; i++) {
			final JButton btn = new JButton();
			btn.setPreferredSize(new Dimension(30, 30));
			btn.setContentAreaFilled(false);
			btn.setBackground(palette[i]);
			btn.setBorderPainted(false);
			btn.setOpaque(true);
			btn.setActionCommand("" + i);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						panel1.setColor(palette[Integer.parseInt(e.getActionCommand())]);
					} catch (NumberFormatException e1) {
						e1.printStackTrace();
					}
				}
			});
			panel3.add(btn);
		}
		colorPanel.add(panel3);

		// *********************** Chat Panel **************************
		JPanel chatPanel = new JPanel();
		chatPanel.setPreferredSize(new Dimension(250, 700));
		frame.getContentPane().add(chatPanel, BorderLayout.EAST);
		chatPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 29, 250, 675);
		panel.setBackground(Color.PINK);
		chatPanel.add(panel);
		panel.setLayout(null);

		// ------------------------ Painter Area --------------------
		// Real-time display of the user's drawing on the whiteboard and the manager's
		// clearing of the whiteboard
		JPanel painterPanel = new JPanel();
		painterPanel.setBackground(Color.YELLOW);
		painterPanel.setBounds(6, 6, 238, 161);
		panel.add(painterPanel);
		painterPanel.setLayout(null);

		JScrollPane scrollPainterPane = new JScrollPane();
		scrollPainterPane.setBounds(6, 5, 226, 150);
		scrollPainterPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		painterPanel.add(scrollPainterPane);

		painterArea = new JTextArea();
		painterArea.setBounds(6, 5, 226, 150);
		painterArea.append("");
		painterArea.setBackground(new Color(255, 255, 255));
		painterArea.setLineWrap(true);
		painterArea.setWrapStyleWord(true);

		scrollPainterPane.setAutoscrolls(true);
		painterArea.setAutoscrolls(true);
		scrollPainterPane.setViewportView(painterArea);

		// ------------------------ Dialogue Area --------------------
		// Displaying the conversation record between users
		JPanel dialoguePanel = new JPanel();
		dialoguePanel.setBackground(new Color(144, 238, 144));
		dialoguePanel.setBounds(6, 203, 238, 400);
		panel.add(dialoguePanel);
		dialoguePanel.setLayout(null);

		JScrollPane scrollDialoguePane = new JScrollPane();
		scrollDialoguePane.setBounds(6, 6, 226, 388);
		scrollDialoguePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		dialoguePanel.add(scrollDialoguePane);

		dialogueArea = new JTextArea();
		dialogueArea.setBounds(6, 6, 226, 388);
		dialogueArea.append("");
		dialogueArea.setBackground(new Color(255, 255, 255));
		dialogueArea.setLineWrap(true);
		dialogueArea.setWrapStyleWord(true);

		scrollDialoguePane.setAutoscrolls(true);
		dialogueArea.setAutoscrolls(true);
		scrollDialoguePane.setViewportView(dialogueArea);

		// ------------------------ Chat Box --------------------
		// For users to typein and send text
		JPanel chatBox = new JPanel();
		chatBox.setBackground(new Color(230, 230, 250));
		chatBox.setBounds(6, 609, 238, 58);
		panel.add(chatBox);
		chatBox.setLayout(null);

		chatField = new JTextField();
		chatField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					if (chatField.getText() != null) {
						try {
							invokeDialogUpdate(chatField.getText());
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
					chatField.setText("");

				}
			}
		});
		chatField.setBounds(6, 6, 226, 26);
		chatBox.add(chatField);
		chatField.setColumns(10);

		JButton sendBtn = new JButton("Send");
		sendBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("press SEND");
				if (chatField.getText() != null) {
					try {
						invokeDialogUpdate(chatField.getText());
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					chatField.setText("");
				}
			}
		});
		sendBtn.setBounds(65, 28, 110, 29);
		chatBox.add(sendBtn);

		JLabel chatboxLabel = new JLabel("CHAT WINDOW");
		chatboxLabel.setHorizontalAlignment(SwingConstants.CENTER);
		chatboxLabel.setBounds(0, 172, 250, 26);
		panel.add(chatboxLabel);

		JPanel chatboxLabelPanel = new JPanel();
		chatboxLabelPanel.setBounds(0, 172, 250, 26);
		panel.add(chatboxLabelPanel);

		JLabel painterLable = new JLabel("CURRENT PAINTER ");
		painterLable.setBounds(0, 0, 250, 24);
		chatPanel.add(painterLable);
		painterLable.setHorizontalAlignment(SwingConstants.CENTER);

	}
}
