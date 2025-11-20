package practice01;

public class User implements UserInterface {
	private String _role;
	public User(String role) {
		_role = role;
	}
	
	@Override
	public void greet() {
		System.out.println("Hello, User.");
	}

}
