package practice01;

public class VIP implements UserInterface{
	private String _role;
	public VIP(String role) {
		_role = role;
	}
	@Override
	public void greet() {
		System.out.println("Welcome, VIP User.");
		
	}
	
}
