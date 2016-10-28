import java.util.List;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;

public class GetScore {

	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/only_time.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/only_time_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/only_time_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";	
	
	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/out.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/out_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/out_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
	
	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all_94.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all_94_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/FINAL NETWORKS/all_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";

	
	
	

	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_onlyTime/sbml_qual_generated.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_onlyTime/sbml_qual_generated_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_onlyTime/sbml_qual_generated_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out/sbml_qual_generated.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out/sbml_qual_generated_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out/sbml_qual_generated_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all/sbml_qual_generated.sbml";
	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all/sbml_qual_generated_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all/sbml_qual_generated_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all_myb/sbml_qual_generated.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_Col.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all_myb/sbml_qual_generated_96ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_96ko.txt";
//	public static String regFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/out_all_myb/sbml_qual_generated_30ko.sbml";
//	public static String refFile = "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/GenetAlg/References_boolean_30ko.txt";
	
	
	
	
	static Configuration conf = new DefaultConfiguration();

	static Gene[] sampleGenes;
	static Parameter[] parameters;
	static List<Forbidden> forbiddenCombinations;

	public static void main(String[] args) throws InvalidConfigurationException {

		EvaluateSimulationScore myFunc = new EvaluateSimulationScore(regFile, refFile);

		System.out.println(myFunc.evaluate(null));

	}

}
