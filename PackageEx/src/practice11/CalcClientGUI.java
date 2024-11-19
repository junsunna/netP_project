// 1911249 나준선

package practice11;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CalcClientGUI {
		private JFrame frame;
		
//		private JTextArea t_display;
		private JTextField t_left, t_op, t_right, t_result;
		
		private JLabel l_result;
		private JButton b_connect;
		private JButton b_disconnect;
		private JButton b_exit;
		private JButton b_send;
		
		private String serverAddress;
		private int serverPort;
		
	    private DataInputStream in;
		private ObjectOutputStream out;
		private Socket socket;
		
		public CalcClientGUI(String serverAddress, int serverPort) {
			this.serverAddress = serverAddress;
			this.serverPort = serverPort;
			
			frame = new JFrame("CalcClient GUI");
			
			buildGUI();
			
			frame.setSize(400, 300);
			frame.setLocation(100,300);
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}


		private void buildGUI() {
			frame.add(createInputPanel());
			frame.add(createControlPanel(), BorderLayout.SOUTH);
			
		}
		
//		private JPanel createDisplayPanel() {
//			t_display = new JTextArea(10, 30);
//			
//			// 편집 불가 상태
//			t_display.setEditable(false);
//			// 자동 줄바꿈
//			t_display.setLineWrap(true);
//			t_display.setWrapStyleWord(true);
//			
//			// JScrollPane 스크롤 연결
//			JScrollPane scrollPane = new JScrollPane(t_display);
//			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//			
//			//패널 생성 및 구성
//			JPanel panel = new JPanel();
//			panel.setLayout(new BorderLayout());
//			panel.add(scrollPane, BorderLayout.CENTER);
//			
//			return panel;
//		}
		
		private JPanel createInputPanel() {
			JPanel panel = new JPanel();
//			panel.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			t_left = new JTextField(5);
			t_op = new JTextField(3);
			t_right = new JTextField(5);
			t_result = new JTextField(5);
			l_result = new JLabel("=");
			b_send = new JButton("계산");
			// Enter 키를 눌렀을 때 액션 리스너 추가하여 메시지 처리
			t_right.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sendMessage();
					receiveMessage();
				}
			});
			// 보내기 버튼에 액션 리스너 추가하여 메시지 처리
			b_send.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sendMessage();
					receiveMessage();
				}
			});
			
			// 텍스트 필드 오른쪽 정렬
			t_left.setHorizontalAlignment(JTextField.RIGHT);
			t_op.setHorizontalAlignment(JTextField.CENTER);
			t_right.setHorizontalAlignment(JTextField.RIGHT);
			t_result.setHorizontalAlignment(JTextField.RIGHT);
			
//			t_left.setEnabled(true);
//			t_right.setEnabled(true);
//			t_op.setEnabled(true);
			
			// 읽기만 가능하도록 설정
			t_result.setEditable(false);
			
			// 추가
			panel.add(t_left);
			panel.add(t_op);
			panel.add(t_right);
			panel.add(l_result);
			panel.add(t_result);
			panel.add(b_send);
			
			return panel;
		}

		private void sendMessage() {
			// 메시지 가져오기
			double left, right;
			char op;
			
			String t_l = t_left.getText().trim();
			String t_r = t_right.getText().trim();
			String op_msg = t_op.getText().trim();
			
			try {
				left = Double.parseDouble(t_l);
				right = Double.parseDouble(t_r);
			} catch (NumberFormatException e) {
				System.err.println("클라이언트 자료형 입력 오류> " + e.getMessage());
				return;
			}
			if (op_msg.length() == 1)
				op = op_msg.charAt(0);
			else return;
			
			CalcExpr calObj = new CalcExpr(left, op, right);
			
			
			try {
				out.writeObject(calObj);
				out.flush();
			} catch (IOException e) {
				System.err.println("클라이언트 일반 전송 오류> " + e.getMessage());
				System.exit(-1);
			}
		}
		
		private void receiveMessage() {
			try {
				Double msg = in.readDouble();
				msg = Math.round(msg * 100.0) / 100.0;
				String result = String.valueOf(msg);
				t_result.setText(result);
			} catch (IOException e) {
				System.err.println("클라이언트 일반 수신 오류> "+ e.getMessage());
			}
		}
		
		private JPanel createControlPanel() {
			
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(0, 3));
			
			b_connect = new JButton("접속하기");
			// 버튼에 액션 리스너 추가하여 접속하기 구현
			b_connect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					connectToServer();
					
					b_connect.setEnabled(false);
					b_disconnect.setEnabled(true);
					
					t_right.setEnabled(true);
					b_exit.setEnabled(false);
					
				}
			});
			
			b_disconnect = new JButton("접속끊기");
			// 버튼에 액션 리스너 추가하여 접속끊기 구현
			b_disconnect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					disconnect();
					
					b_connect.setEnabled(true);
					b_disconnect.setEnabled(false);
					
					t_right.setEnabled(false);
//					b_send.setEnabled(false);
					b_exit.setEnabled(true);
				}
			});
			
			b_exit = new JButton("종료하기");
			// 버튼에 액션 리스너 추가하여 종료 구현
			b_exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 프로그램 종료
					disconnect();
					System.exit(0);
				}
			});
			
			panel.add(b_connect);
			panel.add(b_disconnect);
			panel.add(b_exit);
			b_connect.setEnabled(false);
			b_exit.setEnabled(false);
			connectToServer();

			return panel;
		}
		
		private void connectToServer() {
			try {
				socket = new Socket(serverAddress, serverPort);
				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
			} catch (UnknownHostException e) {
				System.err.println("알 수 없는 서버> " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("클라이언트 접속 오류> " + e.getMessage());
			}

		}
		
		private void disconnect() {
			try {
				out.close();
				socket.close();
			} catch (IOException e1) {
				System.err.println("클라이언트 닫기 오류> " + e1.getMessage());
				System.exit(-1);
			}

		}
		
		public static void main(String[] args) {
			String serverAddress = "localhost";
			int serverPort = 54321;
			new CalcClientGUI(serverAddress, serverPort);
		}
	}

