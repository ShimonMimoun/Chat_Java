package MyChat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;


/**
 * Server Chat 
 * This is a Server of a Chat 
 * @author Shimon Mimoun
 */
public class ServerChat extends JFrame 
{
	/**
	 * SerialVersionUid = 1L
	 */
	private static final long serialVersionUID = 1L;

	///////////////////////////////////////////////////////////////
	////////////////// Variable of Class //////////////////////////
	//////////////////////////////////////////////////////////////

	public ArrayList<PrintWriter> Output_Streams_Client;
	public  ArrayList<String> Users_list;
	PrintWriter Print_Writer;
	ServerSocket serverSock;
	
	private javax.swing.JButton b_send;
	private javax.swing.JTextField b_sendText;
	private javax.swing.JButton b_clear;
	private javax.swing.JButton b_end;
	private javax.swing.JPanel port_name;
	private javax.swing.JButton b_start;
	private javax.swing.JButton b_users;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel lb_name;
	private javax.swing.JTextArea Chat;
	

	public Iterator<String> iteretor() 
	{
		return  this.Users_list.iterator();
	}

	public class Handler_ClientChat implements Runnable	
	{
		BufferedReader buff_reader;
		PrintWriter PrintWriter_client;
		Socket socket_1;


		@Override
		/**
		 * for each message sent by the client if it does not empty the function trying to send 
		 * the message to other clients, if it succeeds, the message will appear in the
		 * client discussion window,else it will send an error message.
		 */
		public void run() 
		{
			String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";
			String[] data_list;
			try 
			{
				while ((stream = buff_reader.readLine()) != null) 
				{
					data_list = stream.split(":");

					Chat.append("Received :  " + stream + "\n");
					data_list = stream.split(":");
					for (String token:data_list) 
					{
						Chat.append(token + " \n");
					}
					if (data_list[2].equals(connect)) 
					{
						tellEveryone((data_list[0] + ":"+chat+ " : " + data_list[1] ));
						userAdd(data_list[0]);
					} 
					else if (data_list[2].equals(disconnect)) 
					{
						tellEveryone((data_list[0] + " : has disconnected " + ":" + chat));

						userRemove(data_list[0]);
					} 
					else if (data_list[2].equals(chat)) 
					{
						tellEveryone(stream);
					}  
					else 
					{
						Chat.append("No condition has been fulfilled \n");
					}
				} 
			} 
			catch (Exception ex) 
			{
				Chat.append("*********Lost connection****** \n");
				ex.printStackTrace();
				Output_Streams_Client.remove(PrintWriter_client);
			} 
		}



		/**
		 * Open a chat for a new user
		 * @param clientSocket Socket of client 
		 * @param user User Writer
		 */
		public Handler_ClientChat(Socket clientSocket, PrintWriter user) 
		{
			PrintWriter_client = user;
			try 
			{
				socket_1 = clientSocket;
				
				InputStreamReader isReader = new InputStreamReader(socket_1.getInputStream());
				buff_reader = new BufferedReader(isReader);
			}
			catch (Exception ex) 
			{
				Chat.append("Unlooked-for Error... \n");
			}
		}
	}

	/**
	 * when the server press the send button this function will try to sent the 
	 * message to all the clients if its succeeded the message will appear on the clients chat window
	 *else it will send an error message.
	 * @param evt
	 */

	private void b_sendActionPerformed(java.awt.event.ActionEvent evt) {        
		String nothing = "";
		if ((b_sendText.getText()).equals(nothing)) {
			b_sendText.setText("");
			b_sendText.requestFocus();
		}
		else {
			try {
				tellEveryone("Server" + ":" + b_sendText.getText() + ":" + "Chat");
				Print_Writer.flush(); // flushes the buffer
			} catch (Exception ex) {
			}
			b_sendText.setText("");
			b_sendText.requestFocus();
		}

		b_sendText.setText("");
		b_sendText.requestFocus();

	}  
	private void b_sendTextActionPerformed(java.awt.event.ActionEvent evt) {                                             
	}  
	private void b_endActionPerformed(java.awt.event.ActionEvent evt) {
		tellEveryone("Server : is stopping and all users will be disconnected. \n:Chat");
		Chat.append("********Server stopping******* \n");

try {
	serverSock.close();
	Chat.setText("");
} catch (IOException e) {
	e.printStackTrace();

}

	}
	private void b_startActionPerformed(java.awt.event.ActionEvent evt) {
		Thread starter = new Thread(new ServerStart());
		starter.start();
		Chat.append("***********Server started******** \n");
	}
	
	public void b_usersActionPerformed(java.awt.event.ActionEvent evt) {
		Chat.append("\n On"
				+ "line users : \n");
		Iterator<String> It = this.iteretor();
		
		while(It.hasNext()) {
			Chat.append(It.next());
			Chat.append("\n");
		}
	}

	private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {
		Chat.setText("");
	}
	public static void main(String args[]) 
	{
		java.awt.EventQueue.invokeLater(new Runnable() 
		{
			@Override
			public void run() {
				new ServerChat().setVisible(true);
			}
		});
	}
	public class ServerStart implements Runnable 
	{
		/**
		 * when a new client want to cunnect we create a new server socket with the port "3234"
		 * if the connection is successful the message "Got a connection." will appear,
		 * else is will send the message "Error making a connection."
		 */
		@Override
		public void run() 
		{
			Output_Streams_Client = new ArrayList();
			Users_list = new ArrayList();  
			try 
			{
				serverSock = new ServerSocket(3234);
				while (true) 
				{
					Socket clientSock = serverSock.accept();
					PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
					Output_Streams_Client.add(writer);

					Thread listener = new Thread(new Handler_ClientChat(clientSock, writer));
					listener.start();
					Chat.append("You have a connection \n");
				}
			}
			catch (Exception ex)
			{
				Chat.append("Port Number may be Busy \n");
				Chat.append("Error making a connection \n");
			}
		}
	}
	public void userAdd (String data) 
	{
		String message, add = "::Connect", done = "Server::Done", name = data;
		Chat.append("Before " + name + " added \n");
		Users_list.add(name);
		Chat.append("After " + name + " added \n");
		String[] tempList = new String[(Users_list.size())];
		Users_list.toArray(tempList);
		for (String token:tempList) 
		{
			message = (token + add);
			tellEveryone(message);
		}
		tellEveryone(done);
	}  
	public void userRemove (String data) 
	{
		String message, add = "::Connect", done = "Server::Done", name = data;
		Users_list.remove(name);
		String[] tempList = new String[(Users_list.size())];
		Users_list.toArray(tempList);
		for (String token:tempList) 
		{
			message = (token + add);
			tellEveryone(message);
		}
		tellEveryone(done);
	} 
	/**
	 * When a client wants to send a message to all connected client and not to a specific client.
	 * @param message
	 */
	public void tellEveryone(String message) 
	{
		Iterator it = Output_Streams_Client.iterator();
		while (it.hasNext()) 
		{
			try 
			{
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				Chat.append("Sending : " + message + "\n");
				writer.flush();
				Chat.setCaretPosition(Chat.getDocument().getLength());

			} 
			catch (Exception ex) 
			{
				Chat.append("Mistake to tell everyone \n");
			}
		} 
	}
	public ServerChat() 
	{
		initComponents();
	}
	/**
	 * GUI
	 */
	@SuppressWarnings("unchecked")
	private void initComponents() {
		b_clear = new javax.swing.JButton();
		b_sendText = new javax.swing.JTextField();
		jScrollPane1 = new javax.swing.JScrollPane();
		Chat = new javax.swing.JTextArea();
		b_end = new javax.swing.JButton();
		b_users = new javax.swing.JButton();
		b_send = new javax.swing.JButton();
		b_start = new javax.swing.JButton();
		lb_name = new javax.swing.JLabel();
		port_name = new javax.swing.JPanel();
		Chat.setFont(new Font("Arial Black", Font.PLAIN, 30));
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.gray);
		setTitle("Server Chat V2.1 ");
		Chat.setColumns(30);
		Chat.setRows(5);
		jScrollPane1.setViewportView(Chat);
		JLabel label = new JLabel("PORT : 3234");
port_name.add(label);

		
		b_send.setText("Send ");
		b_send.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_sendActionPerformed(evt);
			}
		});

		b_end.setText("Disconnect");
		b_end.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_endActionPerformed(evt);
			}
		});
		b_start.setText("Connect");
		b_start.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_startActionPerformed(evt);
			}
		});

		b_users.setText("Online User");
		b_users.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_usersActionPerformed(evt);
			}
		});


		b_sendText.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_sendTextActionPerformed(evt);
			}
		});

		b_clear.setText("Clear");
		b_clear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				b_clearActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(b_sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(b_send, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(b_users, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(b_start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(port_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				
				.addComponent(b_end, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				
				.addGroup(layout.createSequentialGroup()
				.addComponent(b_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(0, 0, Short.MAX_VALUE)))
				.addContainerGap())
				);
		
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createSequentialGroup()
				.addGap(84, 84, 84)
				.addComponent(port_name, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(b_start, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addComponent(b_end, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(19, 19, 19)
				.addComponent(b_users, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	
						.addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(b_sendText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addComponent(b_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addContainerGap())
				);

		pack();
	}

}