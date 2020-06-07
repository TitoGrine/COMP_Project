package jasmin;


import static org.junit.Assert.*;

import org.junit.Test;

public class JasminTest {
	

	@Test
    public void testFindMaximum() {
		JasminUtils.testJmm("fixtures/public/FindMaximum.jmm", "Result: 28");
    }

	@Test
    public void testHelloWorld() {
		JasminUtils.testJmm("fixtures/public/HelloWorld.jmm", "Hello, World!");
    }

	@Test
    public void testMonteCarloPi() {
		JasminUtils.testJmm("fixtures/public/MonteCarloPi.jmm", "Insert number: Result: 0", "-1\n");
    }

	@Test
    public void testQuickSort() {
		JasminUtils.testJmm("fixtures/public/QuickSort.jmm", JasminUtils.getResource("fixtures/public/QuickSort.txt"));
    }

	@Test
    public void testSimple() {
		JasminUtils.testJmm("fixtures/public/Simple.jmm", "30");
    }
	

	@Test
    public void testTicTacToe() {
		JasminUtils.testJmm("fixtures/public/TicTacToe.jmm", JasminUtils.getResource("fixtures/public/TicTacToe.txt"), JasminUtils.getResource("fixtures/public/TicTacToe.input"));
	}

	@Test
    public void testWhileAndIF() {
		JasminUtils.testJmm("fixtures/public/WhileAndIF.jmm", JasminUtils.getResource("fixtures/public/WhileAndIF.txt"));
    }

	@Test
	public void testWeirdArrayAccess() {
		JasminUtils.testJmm("fixtures/public/custom/WeirdArrayAccess.jmm", "Result: 374");
	}

	@Test
	public void testArrayLoop() {
		JasminUtils.testJmm("fixtures/public/custom/ArrayLoop.jmm", JasminUtils.getResource("fixtures/public/custom/ArrayLoop.txt"));
	}

	@Test
	public void testLocalsOverflow() {
		JasminUtils.testJmm("fixtures/public/custom/LocalsOverFlow.jmm", "-19\n121493");
	}

	@Test
	public void testMethodOverload() {
		JasminUtils.testJmm("fixtures/public/custom/MethodOverload.jmm", "-17\n20\n3");
	}

	@Test
	public void testOptimizations() {
		JasminUtils.testJmm("fixtures/public/custom/OptimizationsTest.jmm", "504");
	}

	@Test
	public void testChainCalls() {
		JasminUtils.testJmm("fixtures/public/custom/ChainCalls.jmm", "22");
	}

	@Test
	public void testFunctionCalls() {
		JasminUtils.testJmm("fixtures/public/custom/FunctionCalls.jmm", "40");
	}

	@Test
	public void testJasminKeywords() {
		JasminUtils.testJmm("fixtures/public/custom/JasminKeywords.jmm", "22");
	}

	@Test
	public void testLiveness() {
		JasminUtils.testJmm("fixtures/public/custom/Liveness.jmm", "2");
	}

	@Test
	public void testNumericOperations() {
		JasminUtils.testJmm("fixtures/public/custom/NumericOp.jmm", "6343");
	}

	@Test
	public void testFactorial() {
		JasminUtils.testJmm("fixtures/public/custom/Factorial.jmm", "3628800");
	}

	@Test
	public void testFibonacci() {
		JasminUtils.testJmm("fixtures/public/custom/Fibonacci.jmm", "55");
	}
}
