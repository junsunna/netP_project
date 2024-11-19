// 1911249 나준선

package practice11;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import practice07.TestMsg;

public class CalcServerGUI {
	private JFrame frame;
	
	private int port;
	private ServerSocket serverSocket;
	private JTextArea t_display;
	
	private ObjectInputStream in;
	private DataOutputStream out;
	
	public CalcServerGUI(int port) {
		this.port = port;
		frame = new JFrame("CalcServer GUI");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		printDisplay("서버가 시작되었습니다.");
		startServer();

	}
	
	private void buildGUI() {
		frame.add(createDisplayPanel(), BorderLayout.CENTER);
		
		frame.add(createControlPanel(), BorderLayout.SOUTH);
		
	}
	private JPanel createDisplayPanel() {
		t_display = new JTextArea(10, 30);
		// 편집 불가 상태
		t_display.setEditable(false);
		// 자동 줄바꿈
		t_display.setLineWrap(true);
		t_display.setWrapStyleWord(true);
		
		// JScrollPane 스크롤 연결
		JScrollPane scrollPane = new JScrollPane(t_display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		//패널 생성 및 구성
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		
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
		
		@Override
		public void run() {
			receiveMessages(clientSocket);
		}
	}
	
	private void receiveMessages(Socket cs) {
		try {
			in = new ObjectInputStream(new BufferedInputStream(cs.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(cs.getOutputStream()));

			while (true) {
				CalcExpr msg = (CalcExpr)in.readObject();
				double left = msg.operand1;
				char op = msg.operator;
				double right = msg.operand2;
				double result;
				switch (op) {
				case '+' :
					result = left + right;
					break;
				case '-' :
					result = left - right;
					break;
				case '*' :
					result = left * right;
					break;
				case '/' :
					result = left / right;
					break;
				default :
					System.err.println("잘못된 연산자입니다");
					return;
				}
				out.writeDouble(result);
				out.flush();
				printDisplay(left + " " + op + " " + right + " = " + result);
			}
		} catch (ClassNotFoundException e) {
			System.err.println("클라이언트로부터 잘못된 객체를 전달받았습니다 " + e.getMessage());
		} catch (IOException e) {
			System.err.println("클라이언트 연결 문제> " + e.getMessage());
		}
		finally {
			printDisplay("클라이언트가 연결을 종료했습니다.");
			System.out.println("클라이언트가 연결을 종료했습니다.");
			try {
				cs.close();
			}
			catch (IOException e) {
				System.err.println("서버 닫기 오류> " + e.getMessage());
				System.exit(-1);
			}
		}
	}
	
	private void printDisplay(String msg) {
		t_display.append(msg + "\n");
		t_display.setCaretPosition(t_display.getDocument().getLength());
	}

	
	public static void main(String[] args) {
		int port = 54321;
		new CalcServerGUI
		(port);
	}
}
