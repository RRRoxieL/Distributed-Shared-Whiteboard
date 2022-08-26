package manager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import util.Canvas;
import util.Tool;

/**
 * UI for the whiteboard manager.
 * 
 * @author Name: Peiyao Li, studentID: 1225766
 *
 */
public class ManagerUI {

	private JFrame frame;
	private Canvas panel1;
	private RemoteManager managerStub;
	private JTextArea dialogueArea;
	private JTextField chatField;
	private String name;
	private JList<String> userList;

	public JFrame getFrame() {
		return this.frame;
	}

	public Canvas getCanvas() {
		return this.panel1;
	}

	// Notify that update is needed in dialogue area
	public void invokeDialogUpdate(String message) throws RemoteException {
		this.managerStub.updateDialog(this.name + "(manager)", message);
	}

	// Notify that some user needs to be kicked out
	public void invokeKickOut(String userName) throws Exception {
		this.managerStub.kickOutUser(userName);
	}

	// Remove all the patterns on the whiteboard.
	public void invokeCanvasClear() throws RemoteException {
		this.managerStub.clearCanvas();
	}

	public void invokeSystemTermination() throws RemoteException {
		this.managerStub.endApplication();
	}

	public void updateDialogue(String userName, String chatlog) {
		dialogueArea.append(userName + ": " + chatlog + "\n");
		dialogueArea.updateUI();
		dialogueArea.setCaretPosition(dialogueArea.getText().length());
	}

	// Display the join request on the manager's UI and get the decision result
	public int showJoinRequest(String userName) {
		return JOptionPane.showConfirmDialog(null,
				"User \'" + userName + "\' wants to join your white board, do you permit?");
	}

	// Update the userlist which displays all the joined user
	public void updateUserList(Object[] userNames) {
		DefaultListModel model = new DefaultListModel<String>();
		for (int i = 0; i < userNames.length; i++) {
			model.addElement(userNames[i]);
		}
		this.userList.setModel(model);
	}

	// Create the whiteboard UI
	public ManagerUI(RemoteManager stubM, String userName) {
		this.managerStub = stubM;
		this.name = userName;
		initialize();

	}

	// Initialize the contents of the frame
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					invokeSystemTermination();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.setTitle("Manager White Board");
		frame.setSize(1200, 800);
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
		panel2.setLayout(new GridLayout(7, 1, 5, 5));

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

		// For manager to remove all patterns on the board
		JButton removeBtn = new JButton(new ImageIcon(ManagerUI.class.getResource("/image/eraser.png")));
		removeBtn.setPreferredSize(new Dimension(30, 30));
		removeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					invokeCanvasClear();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				panel1.repaint();
			}
		});
		panel2.add(removeBtn);

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

		// *************** Chat Panel **********************
		JPanel chatPanel = new JPanel();
		chatPanel.setPreferredSize(new Dimension(250, 700));
		frame.getContentPane().add(chatPanel, BorderLayout.EAST);
		chatPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 29, 250, 675);
		panel.setBackground(Color.PINK);
		chatPanel.add(panel);
		panel.setLayout(null);

		// ------------------------ Manage Area --------------------
		// Manage panel for the manager to kick out users
		JPanel userManagePanel = new JPanel();
		userManagePanel.setBackground(Color.YELLOW);
		userManagePanel.setBounds(6, 6, 238, 161);
		panel.add(userManagePanel);
		userManagePanel.setLayout(null);

		JScrollPane scrollUserPane = new JScrollPane();
		scrollUserPane.setBounds(6, 5, 226, 150);
		scrollUserPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		userManagePanel.add(scrollUserPane);

		this.userList = new JList<String>();
		userList.setBackground(new Color(255, 255, 255));

		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(userList.getSelectedValue());
				int result = JOptionPane.showConfirmDialog(frame,
						"Do you want to kick \'" + userList.getSelectedValue() + "\' out ?");
				if (result == 0) {
					try {
						invokeKickOut(userList.getSelectedValue());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setBounds(6, 6, 226, 150);
		scrollUserPane.setViewportView(userList);

		// ------------------------ Dialogue Area --------------------
		// Displaying the conversation record between users
		JPanel dialoguePanel = new JPanel();
		dialoguePanel.setBackground(new Color(175, 238, 238));
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
		chatBox.setBackground(new Color(245, 245, 220));
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

		JPanel chatboxLabelPanel = new JPanel();
		chatboxLabelPanel.setBounds(0, 172, 250, 26);
		panel.add(chatboxLabelPanel);

		JLabel chatboxLabel = new JLabel("CHAT WINDOW");
		chatboxLabelPanel.add(chatboxLabel);
		chatboxLabel.setBackground(SystemColor.window);
		chatboxLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel userListLabel = new JLabel("USER LIST FOR KICK-OUT");
		userListLabel.setBounds(0, 0, 250, 24);
		chatPanel.add(userListLabel);
		userListLabel.setHorizontalAlignment(SwingConstants.CENTER);

	}
}
