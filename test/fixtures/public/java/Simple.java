import java.io.*; 

class Simple {

	public static void main(String[] args) {
		boolean t;
		int a;
		int b;
		Simple simple;
		int c;
		
		a = 10;
		b = 2;
		simple = new Simple();
		
		c = simple.add(a,b);
		t=true;


	}

	public int add(int a, int b) {


		Simple simple;
		
		simple = new Simple();


		return simple.cenas(a);
	}

	public int cenas(int c) {
		int r;
		r=10;
		return r + c;
	}
}