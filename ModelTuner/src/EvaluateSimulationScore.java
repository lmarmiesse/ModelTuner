import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import flexflux.analyses.TDRNAAnalysis;
import flexflux.analyses.result.TDRNAAnalysisResult;
import flexflux.input.SBMLQualReader;
import flexflux.interaction.FFTransition;
import flexflux.interaction.Interaction;
import flexflux.interaction.InteractionNetwork;
import flexflux.interaction.RelationFactory;
import flexflux.output.SBMLQualWriter;
import parsebionet.biodata.BioEntity;

public class EvaluateSimulationScore extends FitnessFunction {

	public Map<BioEntity, List<Double>> reference_Col = new HashMap<BioEntity, List<Double>>();
	public Map<BioEntity, List<Double>> reference_30ko = new HashMap<BioEntity, List<Double>>();
	public Map<BioEntity, List<Double>> reference_96ko = new HashMap<BioEntity, List<Double>>();

	public List<Double> refTimes = new ArrayList<Double>();

	public Map<BioEntity, List<Double>> result_Col = new HashMap<BioEntity, List<Double>>();
	public Map<BioEntity, List<Double>> result_30ko = new HashMap<BioEntity, List<Double>>();
	public Map<BioEntity, List<Double>> result_96ko = new HashMap<BioEntity, List<Double>>();

	public InteractionNetwork intNet_Col;

	public double deltaT = 0.2;
	public int nSim = 30;

	public int nRefValues;

	public Parameter[] parameters;
	public List<Forbidden> forbiddenCombinations = new ArrayList<Forbidden>();

	public Set<BioEntity> varsResValueCanChange = new HashSet<BioEntity>();

	// public String outFolder = "";

	public Map<String, Interaction> geneIdToInteraction = new HashMap<String, Interaction>();

	public EvaluateSimulationScore(String regFile, String refFile) {

		intNet_Col = SBMLQualReader.loadSbmlQual(regFile, new InteractionNetwork(), new RelationFactory());

		readReferenceFile(refFile, reference_Col);
		
		parameters = new Parameter[0];

	}

	public EvaluateSimulationScore(Parameter[] parameters, List<Forbidden> forbiddenCombinations, String regFile,
			String refFile) {

		intNet_Col = SBMLQualReader.loadSbmlQual(regFile, new InteractionNetwork(), new RelationFactory());

		readReferenceFile(refFile, reference_Col);
		// readReferenceFile(refFile_30ko, reference_30ko);
		// readReferenceFile(refFile_96ko, reference_96ko);

		this.parameters = parameters;
		this.forbiddenCombinations = forbiddenCombinations;

		// /// initialize gene to interactions
		for (int i = 0; i < parameters.length; i++) {
			for (BioEntity b : intNet_Col.getTargetToInteractions().keySet()) {

				FFTransition fft = intNet_Col.getTargetToInteractions().get(b);

				// for (Interaction inter : fft.getConditionalInteractions()) {
				// if (inter.getName().equals(geneIds[i])) {
				// geneIdToInteraction.put(geneIds[i], inter);
				// }
				// }
				// if (fft.getdefaultInteraction().getName().equals(geneIds[i]))
				// {
				// geneIdToInteraction.put(geneIds[i],
				// fft.getdefaultInteraction());
				// }

				for (Interaction inter : fft.getConditionalInteractions()) {
					if (inter.getName().equals(parameters[i].getName())) {
						geneIdToInteraction.put(parameters[i].getName(), inter);

						if (parameters[i].getCategory().equals("resultValue")) {
							varsResValueCanChange.add(b);
						}
					}
				}
				if (fft.getdefaultInteraction().getName().equals(parameters[i].getName())) {
					geneIdToInteraction.put(parameters[i].getName(), fft.getdefaultInteraction());

					if (parameters[i].getCategory().equals("resultValue")) {
						varsResValueCanChange.add(b);
					}
				}

			}
		}
		// ///
	}

	public double evaluate(IChromosome solution) {

		int nbForbidden = 0;

		for (Forbidden forbidden : forbiddenCombinations) {

			Parameter p1 = forbidden.getPar1();
			double v1 = forbidden.getVal1();
			Parameter p2 = forbidden.getPar2();
			double v2 = forbidden.getVal2();

			boolean firstTrue = false;
			boolean secondTrue = false;

			for (int i = 0; i < parameters.length; i++) {

				if (parameters[i].getName().equals(p1.getName())) {
					if (parameters[i].getCategory().equals("init")
							|| parameters[i].getCategory().equals("resultValue")) {
						if ((Integer) solution.getGene(i).getAllele() == v1) {
							firstTrue = true;
						}
					} else {
						if ((Double) solution.getGene(i).getAllele() == v1) {
							firstTrue = true;
						}
					}
				}
				if (parameters[i].getName().equals(p2.getName())) {
					if (parameters[i].getCategory().equals("init")
							|| parameters[i].getCategory().equals("resultValue")) {
						if ((Integer) solution.getGene(i).getAllele() == v2) {
							secondTrue = true;
						}
					} else {
						if ((Double) solution.getGene(i).getAllele() == v2) {
							secondTrue = true;
						}
					}
				}

			}

			if (firstTrue && secondTrue) {

				nbForbidden++;
			}

		}

		double score = 0;
		double score2 = 1;

		// ///////////////////////////////// We set the parameters
		for (int i = 0; i < parameters.length; i++) {
			// if the param concerns an entity
			if (intNet_Col.getEntity(parameters[i].getName()) != null) {
				if (parameters[i].getCategory().equals("init")) {
					intNet_Col.addInitialState(intNet_Col.getEntity(parameters[i].getName()),
							(Integer) solution.getGene(i).getAllele());

				}
			}
			// else it concerns an interaction
			else {
				Interaction concernedInteraction = geneIdToInteraction.get(parameters[i].getName());

				if (parameters[i].getCategory().equals("delay")) {

					concernedInteraction.setTimeInfos(new double[] { (Double) solution.getGene(i).getAllele(),
							concernedInteraction.getTimeInfos()[1] });

				} else if (parameters[i].getCategory().equals("lasts")) {

					concernedInteraction.setTimeInfos(new double[] { concernedInteraction.getTimeInfos()[0],
							(Double) solution.getGene(i).getAllele() });

				} else if (parameters[i].getCategory().equals("resultValue")) {

					concernedInteraction.getConsequence().setValue((Integer) solution.getGene(i).getAllele());

				} else {

					System.out.println("ERROR : " + parameters[i].getCategory() + " is unknown");
				}

				// System.out.println(geneIds[i]);

			}

		}

		int nbInteractions = countNumberOfRequiredInteraction(solution);

		// System.out.println(nbInteractions);

		// Init simulation values should be steady state values//

		// ///////////////////////////////// Simulation Col
		TDRNAAnalysis analysis_Col = new TDRNAAnalysis(intNet_Col, nSim, deltaT);
		TDRNAAnalysisResult res_Col = analysis_Col.runAnalysis();
		List<Map<BioEntity, Integer>> statesList_Col = res_Col.getStatesList();
		//

		for (BioEntity refEntity : reference_Col.keySet()) {
			List<Double> resArray_Col = new ArrayList<Double>();

			double time = 0.0;
			for (Map<BioEntity, Integer> resMap : statesList_Col) {
				for (double refTime : refTimes) {

					if (Math.abs(time - refTime) < 0.00001) {

						resArray_Col.add(resMap.get(refEntity).doubleValue());

						// System.out.println(time);

					}
				}
				time += deltaT;
			}
			result_Col.put(refEntity, resArray_Col);

		}

		double[] refArray_Col = new double[reference_Col.size() * nRefValues];
		double[] resArray_Col = new double[result_Col.size() * nRefValues];
		int index = 0;

		for (BioEntity refEntity : reference_Col.keySet()) {

			List<Double> refList_Col = reference_Col.get(refEntity);
			List<Double> resList_Col = result_Col.get(refEntity);

			for (int i = 0; i < refList_Col.size(); i++) {
				refArray_Col[index] = refList_Col.get(i);
				resArray_Col[index] = resList_Col.get(i);
				index++;
			}

		}

		for (int i = 0; i < refArray_Col.length; i++) {
			if (refArray_Col[i] == resArray_Col[i]) {
				score += 1;
				score2 *= 1.1;
			}
		}

		// System.out.println(nbInteractions);
		score -= nbInteractions * 0.2;

		// -10% score per forbidden combination of parameter values
		double newScore = score;
		for (int i = 0; i < nbForbidden; i++) {
			newScore -= newScore / 10;
		}

		
		return newScore;
		// return score2;
	}

	private int countNumberOfRequiredInteraction(IChromosome solution) {

		
		int nbRequiredInters = 0;

		for (BioEntity b : varsResValueCanChange) {
			
			if (!b.getId().contains("_")) {

				FFTransition fft = intNet_Col.getTargetToInteractions().get(b);

				int minNbInter = 10;
				// determine the number of required inputs

				// get the min number of 1s
				for (Interaction inter : fft.getConditionalInteractions()) {
					if (inter.getConsequence().getValue() == 1.0) {
						int nb = StringUtils.countMatches(inter.getCondition().toString(), "= 1");
						if (nb < minNbInter) {
							minNbInter = nb;
						}
					}
				}

				Set<String> inputs = new HashSet<String>();

				// count the number of activators
				for (Interaction inter : fft.getConditionalInteractions()) {
					if (inter.getConsequence().getValue() == 1.0) {

						int nb = StringUtils.countMatches(inter.getCondition().toString(), "= 1");
						if (nb == minNbInter) {

							Pattern r = Pattern.compile("(\\w+) = 1.0");
							Matcher m = r.matcher(inter.getCondition().toString());
							while (m.find()) {
								inputs.add(m.group(1));
							}

						}
					}
				}

				nbRequiredInters += inputs.size();
			}

		}

		return nbRequiredInters;
	}

	public void seeSolutionOutput(IChromosome solution, String outFolder)
			throws FileNotFoundException, UnsupportedEncodingException {

		// ///////////////////////////////// We set the parameters
		for (int i = 0; i < parameters.length; i++) {
			// if the param concerns an entity
			if (intNet_Col.getEntity(parameters[i].getName()) != null) {
				if (parameters[i].getCategory().equals("init")) {
					intNet_Col.addInitialState(intNet_Col.getEntity(parameters[i].getName()),
							(Integer) solution.getGene(i).getAllele());

				}
			}
			// else it concerns an interaction
			else {
				Interaction concernedInteraction = geneIdToInteraction.get(parameters[i].getName());

				if (parameters[i].getCategory().equals("delay")) {

					concernedInteraction.setTimeInfos(new double[] { (Double) solution.getGene(i).getAllele(),
							concernedInteraction.getTimeInfos()[1] });

				} else if (parameters[i].getCategory().equals("lasts")) {

					concernedInteraction.setTimeInfos(new double[] { concernedInteraction.getTimeInfos()[0],
							(Double) solution.getGene(i).getAllele() });

				} else if (parameters[i].getCategory().equals("resultValue")) {

					concernedInteraction.getConsequence().setValue((Integer) solution.getGene(i).getAllele());

				} else {

					System.out.println("ERROR : " + parameters[i].getCategory() + " is unknown");
				}

				// System.out.println(geneIds[i]);

			}

		}

		// Simulation
		TDRNAAnalysis analysis = new TDRNAAnalysis(intNet_Col, nSim, deltaT);
		TDRNAAnalysisResult res = analysis.runAnalysis();
		res.writeToFile(outFolder + "/TDRNA_Col.txt");

		// //////////////////////////// 30KO

		InteractionNetwork intNet_30ko = intNet_Col.copy();

		FFTransition myb30_trans = intNet_30ko.getTargetToInteractions().get(intNet_30ko.getEntity("MYB30"));

		// /////WE SET 30 TO 0

		intNet_30ko.addInitialState(intNet_30ko.getEntity("MYB30"), 0);
		intNet_30ko.setTargetToInteractions(intNet_30ko.getEntity("MYB30"), new FFTransition());
		intNet_30ko.getTargetToInteractions().get(intNet_30ko.getEntity("MYB30"))
				.setdefaultInteraction(myb30_trans.getdefaultInteraction());

		// Simulation
		analysis = new TDRNAAnalysis(intNet_30ko, nSim, deltaT);
		res = analysis.runAnalysis();
		res.writeToFile(outFolder + "/TDRNA_30ko.txt");

		intNet_30ko.setTargetToInteractions(intNet_30ko.getEntity("MYB30"), myb30_trans);

		// //////////////////////////// 96KO

		InteractionNetwork intNet_96ko = intNet_Col.copy();

		FFTransition myb96_trans = intNet_96ko.getTargetToInteractions().get(intNet_30ko.getEntity("MYB96"));

		// /////WE SET 96 TO 0
		intNet_96ko.addInitialState(intNet_96ko.getEntity("MYB96"), 0);
		intNet_96ko.setTargetToInteractions(intNet_96ko.getEntity("MYB96"), new FFTransition());
		intNet_96ko.getTargetToInteractions().get(intNet_96ko.getEntity("MYB96"))
				.setdefaultInteraction(myb96_trans.getdefaultInteraction());
				// /////

		// Simulation
		analysis = new TDRNAAnalysis(intNet_96ko, nSim, deltaT);
		res = analysis.runAnalysis();
		res.writeToFile(outFolder + "/TDRNA_96ko.txt");
		intNet_96ko.setTargetToInteractions(intNet_96ko.getEntity("MYB96"), myb96_trans);

		// Export SBMLqual
		SBMLQualWriter.writeSbmlQual(outFolder + "/sbml_qual_generated.sbml", intNet_Col);

		// export score
		double score = this.evaluate(solution);
		PrintWriter writer = new PrintWriter(outFolder + "/score.txt", "UTF-8");
		writer.println(score);
		writer.close();

	}

	private void readReferenceFile(String path, Map<BioEntity, List<Double>> ref) {

		try {

			BufferedReader in = new BufferedReader(new FileReader(path));

			String line = in.readLine();

			String[] parts = line.split("\t");
			nRefValues = parts.length - 1;

			refTimes = new ArrayList<Double>();

			for (int i = 0; i < nRefValues; i++) {
				refTimes.add(Double.parseDouble(parts[i + 1]));
			}

			while ((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				parts = line.split("\t");
				String entityName = parts[0];

				if (intNet_Col.getEntity(entityName) == null) {

					System.out.println("Error : unknown entity " + entityName);

				}
				List<Double> refValues = new ArrayList<Double>();

				for (int i = 0; i < nRefValues; i++) {
					refValues.add(Double.parseDouble(parts[i + 1]));
				}

				ref.put(intNet_Col.getEntity(entityName), refValues);

			}

			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public double getMaxScore() {
		// double max = reference_Col.size() * 5 + reference_30ko.size() * 5
		// + reference_96ko.size() * 5;

		double max = reference_Col.size() * nRefValues;

		return max;

	}

}
