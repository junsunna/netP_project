package practice01;

public class Admin implements UserInterface{
	private String _role;
	public Admin(String role) {
		_role = role;
	}
	@Override
	public void greet() {
		System.out.println("Welcome, Administrator.");
	}
	
}
