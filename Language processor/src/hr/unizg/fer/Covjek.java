package hr.unizg.fer;

public class Covjek {
	
	public Covjek(float height, int age, String name){
		
		this.mHeight = height;
		this.mAge = age;
		this.mName = name;
		
		System.out.println(name + " je registriran.");
	}
	
	public Covjek(String name){
		
		this.mHeight = -1.0f;
		this.mAge = -1;
		this.mName = name;
		
		System.out.println(name + " je registriran. Ali nezna se koliko ima godina i koliko je visok.");
	}
	
	public String VratiIme(){
		return this.mName;
	}

	private float mHeight;
	private int mAge;
	public String mName;
	public String mSurname;
	protected String OIB;
	
}
