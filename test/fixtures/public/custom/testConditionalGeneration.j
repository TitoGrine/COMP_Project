import static io.println(int);
class ConditionalGen {

    int m;
	int[] jk;

	public static void main(String[] args) {
		int a;
		int b;
		boolean e;

		jk = new int[5];
		a = 10;
		b = jk[3];
		e = jk[1] < jk[b - 3];

		if(a < b){
        	m = b;

        	if(!e){
        	    m = m + b;
        	} else {
        	    m = m + a;
        	}

        	e = true;
        } else if(a + 1 < b){
            m = b;
        } else if(e && jk[1] < a + b){
            m = a + b / jk[0];
        } else {
            m = 0 - 1;
        }

        jk[jk.length] = m;
	}
}