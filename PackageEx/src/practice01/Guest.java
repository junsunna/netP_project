package practice01;

public class Guest implements UserInterface{
	private String _role;
	public Guest(String role) {
		_role = role;
	}
	@Override
	public void greet() {
		System.out.println("Welcome, Guest.");
		
	}
	
}
