import java.io.*; 

class Simple {

	int y = 2;

	public static void main(String[] args) {
		int a;
		int b;
		Simple simple;
		int c;
		boolean t;
		boolean z;

		t=true;
		z = t && false;

		
		a = 30;
		b = 10;
		
		simple = new Simple();
		c = simple.add(a,b);

	}

	public int add(int a, int b){
		return a+b;
	}
}