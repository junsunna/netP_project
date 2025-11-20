package practice01;

public class Manager implements UserInterface{
	private String _role;
	public Manager(String role) {
		_role = role;
	}
	@Override
	public void greet() {
		System.out.println("Greetings, Manager.");
		
	}
	
}
