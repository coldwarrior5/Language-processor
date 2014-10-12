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
	protected String OIB;
	public float getmHeight() {
		return mHeight;
	}

	public void setmHeight(float mHeight) {
		this.mHeight = mHeight;
	}

	public int getmAge() {
		return mAge;
	}

	public void setmAge(int mAge) {
		this.mAge = mAge;
	}

	public String getmName() {
		return mName;
	}

	public void setmName(String mName) {
		this.mName = mName;
	}

	public String getOIB() {
		return OIB;
	}

	public void setOIB(String oIB) {
		OIB = oIB;
	}
	
	
}
