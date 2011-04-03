package Fractals.src.julia;

/**
 * Defines a complex number. This class is immutable.
 * @author ramos
 */
@SuppressWarnings("serial")
public class Complex extends Number {
	
	
	private double real;
	private double imaginary;
	
	public Complex(Number real, Number imaginary){
		this.real = real.doubleValue();
		this.imaginary = imaginary.doubleValue();
	}
	
	public Complex add(Complex c){
		double r = real + c.real;
		double i = imaginary + c.imaginary;
		return new Complex(r, i);
	}
	
	public Complex subtract(Complex c){
		double r = real - c.real;
		double i = imaginary - c.imaginary;
		return new Complex(r, i);
	}
	
	public Complex multiplication(Complex c){
		double r = real * c.real - imaginary * c.imaginary;
		double i = real * c.imaginary + imaginary * c.real;
		return new Complex(r, i);
	}
	
	public Complex division(Complex c){
		double d = (c.real + c.imaginary) * (c.real - c.imaginary);
		double r = (real * c.real + imaginary * c.imaginary) / d;
		double i = (imaginary * c.real - real * c.imaginary) / d;
		return new Complex(r, i);
	}
	
	public Complex square(){
		double r = (real * real - imaginary * imaginary);
		double i = 2 * real * imaginary;
		return new Complex(r, i);
	}
	
	public Complex cube(){
		double r = real * real * real - 3 * real * imaginary * imaginary;
		double i = (3 * real * real * imaginary - imaginary * imaginary * imaginary);
		return new Complex(r, i);
	}
	
	public double getReal(){
		return real;
	}
	
	public double getImaginary(){
		return imaginary;
	}
	
	public double getNorm(){
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	@Override
	public double doubleValue() {
		return real;
	}

	@Override
	public float floatValue() {
		return (float) real;
	}

	@Override
	public int intValue() {
		return (int) real;
	}

	@Override
	public long longValue() {
		return (long) real;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(imaginary);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Complex other = (Complex) obj;
		if (Double.doubleToLongBits(imaginary) != Double.doubleToLongBits(other.imaginary))
			return false;
		if (Double.doubleToLongBits(real) != Double.doubleToLongBits(other.real))
			return false;
		return true;
	}
	
	public String toString(){
		return new StringBuffer(format(real)).append(" + ").append(format(imaginary)).append('i').toString();
	}
	
	public String format(double d){
		if(d % 1 == 0)
			return String.valueOf((int)d);
		StringBuffer s = new StringBuffer(String.valueOf(d));
		while(s.charAt(s.length() - 1) == '0')
			s.deleteCharAt(s.length() - 1);
		return s.toString();
	}

}