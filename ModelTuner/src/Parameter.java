
public class Parameter {
	
	
	
	private String name;
	private String category;
	private String type;
	private double lb;
	private double ub;
	
	
	
	public Parameter(String name, String category, String type, double lb, double ub){
		
		this.name = name;
		this.category = category;
		this.type = type;
		this.lb = lb;
		this.ub = ub;
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getCategory(){
		return this.category;
	}
	

}
