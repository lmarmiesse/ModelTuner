
public class Forbidden {

	private Parameter par1;
	private double val1;

	private Parameter par2;
	private double val2;

	public Forbidden(Parameter p1, double v1, Parameter p2, double v2) {
		this.par1=p1;
		this.val1=v1;
		
		this.par2=p2;
		this.val2=v2;
	}

	public Parameter getPar1() {
		return par1;
	}

	public double getVal1() {
		return val1;
	}

	public Parameter getPar2() {
		return par2;
	}

	public double getVal2() {
		return val2;
	}
	
	public String toString(){
		return par1.getName()+"\t"+val1+"\t"+par2.getName()+"\t"+val2;
	}

}
