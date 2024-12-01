package practice10;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ScoreServerGUI {
	private JFrame frame;
	
	private int port;
	private ServerSocket serverSocket;
	
	private JTextArea t_display;
	
	private ScoreManager sMgr = null;
	
	private BufferedReader in;
	private BufferedWriter out;
	
	
	public ScoreServerGUI(int port) {
		this.port = port;
		frame = new JFrame("ScoreServer GUI");
		
		buildGUI();
		
		frame.setSize(400, 300);
		frame.setLocation(500,300);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		sMgr = new ScoreManager("score.txt");
		
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
	
	public class ScoreManager {
		private HashMap<String, String> map = new HashMap<String, String>();
		
		ScoreManager(String fileName) {
			try {
				Scanner reader;
				reader = new Scanner(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));

				
				while (reader.hasNext()) {
					String name = reader.next();
					String grade = reader.next();
					
					map.put(name,  grade);
				}
				reader.close();
				
				printDisplay("데이터 파일 불러오기");
			} catch (FileNotFoundException e) {
				System.err.println("데이터 파일이 없습니다: " + e.getMessage());
				System.exit(-1);
			} catch (UnsupportedEncodingException e) {
				System.err.println("파일의 인코딩 형식을 확인하세요: " + e.getMessage());
				System.exit(-1);
			}
		}
		
		String get(String name) {
			return map.get(name);
		}
	}

	private void receiveMessages(Socket cs) {
		try {
//			in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
			in = new BufferedReader(new InputStreamReader(cs.getInputStream(), "UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(cs.getOutputStream(), "UTF-8"));
			
			String name;
			while ((name = in.readLine()) != null) {
				String grade = sMgr.get(name);
				if (grade == null) grade = "성적 정보가 없음";
				
				String message = name + ": " + grade + " (" + cs.getInetAddress().getHostAddress() + ")";
				printDisplay(message);
				
				((BufferedWriter)out).write(grade + "\n");
				out.flush();
			}
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
		new ScoreServerGUI(port);
	}
}
