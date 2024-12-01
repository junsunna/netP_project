package practice17;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class ImgReceiver {
	private JFrame frame;
	
	private int port;
	private ServerSocket serverSocket;
//	private JTextArea t_display;
	private JTextPane t_display;
	private DefaultStyledDocument document;
	
	private Thread acceptThread = null;
	
	public ImgReceiver(int port) {
		this.port = port;
		frame = new JFrame("Img Receiver");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		printDisplay("서버가 시작되었습니다.");
//		startServer();
		acceptThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				startServer();
			}
		});
		acceptThread.start();

	}
	
	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		frame.add(createControlPanel(), BorderLayout.SOUTH);
		
	}
	private JPanel createDisplayPanel() {
		document = new DefaultStyledDocument();
		t_display = new JTextPane(document);
		// 편집 불가 상태
		t_display.setEditable(false);
		
		//패널 생성 및 구성
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(t_display), BorderLayout.CENTER);
		
		return panel;
	}
	private JPanel createControlPanel() {
		JButton button = new JButton("종료");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(button, BorderLayout.CENTER);
		
		// 버튼에 액션 리스너 추가하여 종료 구현
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 프로그램 종료
				System.exit(0);
			}
		});
		return panel;
	}
	
	public void startServer() {
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				clientSocket = serverSocket.accept();
				printDisplay("클라이언트가 연결되었습니다.");
				
				ClientHandler cHandler = new ClientHandler(clientSocket);
				cHandler.start();

//				receiveMessages(clientSocket);
			}
		}
		catch (IOException e) {
			System.err.println("서버 오류> " + e.getMessage());
			System.exit(-1);
		}
		finally {
			try {
				if (clientSocket != null) clientSocket.close();
			}
			catch (IOException e) {
				System.err.println("서버 닫기 오류> " + e.getMessage());
				System.exit(-1);
			}
		}
	}
	private class ClientHandler extends Thread {
		private Socket clientSocket;
		
		public ClientHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		
		private void receiveImage(Socket cs) {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
				while (true) {
					String fileName = in.readUTF();
					long size = (long)in.readLong();
					 
					File file = new File(fileName);
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					
					byte[] buffer = new byte[1024];
					int nRead;
//					while ((nRead = in.read(buffer)) != -1) {
					while (size > 0) {
//						bos.write(buffer, 0, nRead);
						nRead = in.read(buffer);
						size -= nRead;
						
						bos.write(buffer, 0, nRead);
					}
					bos.close();
					
					printDisplay("수신을 완료했습니다: " + file.getName());
//					printDisplay("클라이언트가 연결을 종료했습니다.");
					
					ImageIcon icon = new ImageIcon(file.getName());
					printDisplay(icon);
				}
			} catch (IOException e) {
				System.err.println("클라이언트 연결 문제> " + e.getMessage());
			}
			finally {
				try {
					cs.close();
				}
				catch (IOException e) {
					System.err.println("서버 닫기 오류> " + e.getMessage());
					System.exit(-1);
				}
			}
		}
		
		@Override
		public void run() {
			receiveImage(clientSocket);
		}
		
	}
	
	private void printDisplay(String msg) {
//		t_display.append(msg + "\n");
//		t_display.setCaretPosition(t_display.getDocument().getLength());
		int len = t_display.getDocument().getLength();
		
		try {
			document.insertString(len, msg + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		t_display.setCaretPosition(len);
	}
	
	private void printDisplay(ImageIcon icon) {
		t_display.setCaretPosition(t_display.getDocument().getLength());
		
		if (icon.getIconWidth() > 400) {
			Image img = icon.getImage();
			Image changeImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
			icon = new ImageIcon(changeImg);
		}
		t_display.insertIcon(icon);
		
		printDisplay("");
	}
	

	
	public static void main(String[] args) {
		int port = 54321;
		new ImgReceiver(port);
	}
}
