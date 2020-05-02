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
		

		
		a = 30;
        z = true && false;
        b = 10;
		
		simple = new Simple();
		c = simple.add(a,b);

	}

	public int add(int a, int b){
		return a+b;
	}
}