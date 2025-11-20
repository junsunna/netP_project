package practice01;

import java.util.Scanner;

public class UserGreetingService {
	private UserInterface user;
	
	public UserGreetingService(String role) {
		switch (role) {
		case "admin" : user = new Admin(role); break;
		case "user" : user = new User(role); break;
		case "guest" : user = new Guest(role); break;
		case "manager" : user = new Manager(role); break;
		case "vip" : user = new VIP(role); break;
		default: user = null;
		}
	}
	
	private void greet() {
		if (user == null)
			System.out.println("Unknown role.");
		else
			user.greet();
	}
	
	public static void main(String[] args) {
		Scanner sc= new Scanner(System.in);
		while (true) {
			System.out.print("input role (guest, user, manager, admin, vip): ");
			String input = sc.nextLine();
			if (input.equals("exit")) break;
			UserGreetingService user = new UserGreetingService(input);
			user.greet();
		}
	}
}
