package julia;

public class ComplexNumberTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Complex f = new Complex(15.0, 3.9);
		System.out.println(f.toString());
		System.out.println(f.cube());
		System.out.println(f.division(f));
		System.out.println(f.square());
		System.out.println(f.multiplication(f));
	}

}
