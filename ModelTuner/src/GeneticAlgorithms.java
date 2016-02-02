import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.audit.EvolutionMonitor;
import org.jgap.audit.IEvolutionMonitor;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.GABreeder;
import org.jgap.impl.IntegerGene;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import flexflux.thread.ResolveThread;

public class GeneticAlgorithms {

	public CmdLineParser parser;

	static Gene[] sampleGenes;
	static String[] geneIds;
	static String[] geneCategs;
	// static String paramFile =
	// "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/Modelling/Interaction/myb_regulatory_networks/parameters.txt";
	// static String paramFile =
	// "/run/user/91148/gvfs/sftp:host=147.100.166.10,user=lmarmiesse/home/users/GRP_DR/lmarmiesse/Documents/Modelling/Interaction/myb_regulatory_networks/parameters/2-parameters_myb_CER4.txt";

	static Configuration conf;

	@Option(name = "-n", usage = "number of threads", metaVar = "Integer")
	public int numThreads = 1;

	@Option(name = "-it", usage = "number of iterations", metaVar = "Integer")
	public int iterations = 100;

	@Option(name = "-nSave", usage = "number of saved results", metaVar = "Integer")
	public int nSave = 10;

	@Option(name = "-p", usage = "Parameters file path", metaVar = "File - in", required = true)
	public String paramFile = "";

	@Option(name = "-popSize", usage = "Size of the population to evolve", metaVar = "Integer")
	public int popSize = 100;

	@Option(name = "-reg", usage = "Regulatory network file path (SBML-qual format)", metaVar = "File - in", required = true)
	public String regFile = "";

	@Option(name = "-ref", usage = "Reference file path", metaVar = "File - in", required = true)
	public String refFile = "";

	@Option(name = "-outFolder", usage = "outputFolder", metaVar = "File - out", required = true)
	public String outFolder = "";

	public void parseArguments(String[] args) {

		parser = new CmdLineParser(this);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println(this.getClass().getSimpleName());
			parser.printUsage(System.err);
			System.exit(0);
		}

	}

	public static void main(String[] args) throws Exception {

		GeneticAlgorithms f = new GeneticAlgorithms();

		f.parseArguments(args);

		String paramFile = f.paramFile;

		String regFile = f.regFile;
		String refFile = f.refFile;

		String outFolder = f.outFolder + "/";

		File theDir = new File(outFolder);

		if (!theDir.exists()) {
			System.out.println("creating directory: " + outFolder);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}

		int numThreads = f.numThreads;
		final int numEvolutions = f.iterations;
		final int nSave = f.nSave;

		// Threads
		List<Thread> threads = new ArrayList<Thread>();
		final List<IChromosome> bestResultsPerThread = new ArrayList<IChromosome>();

		final List<IChromosome> bestResultsOverAll = new ArrayList<IChromosome>();

		for (int threadNumber = 0; threadNumber < numThreads; threadNumber++) {
			
			System.out.println("Thread "+threadNumber);

			final int j = threadNumber;

			conf = new DefaultConfiguration(threadNumber + "", "no name");
			conf.setPreservFittestIndividual(threadNumber % 2 == 0);
			conf.setKeepPopulationSizeConstant(threadNumber % 2 != 0);

			readParameters(paramFile);

			final EvaluateSimulationScore myFunc = new EvaluateSimulationScore(geneIds, geneCategs, regFile, refFile);

			conf.setFitnessFunction(myFunc);

			IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);

			conf.setSampleChromosome(sampleChromosome);

			conf.setPopulationSize(f.popSize);

			final Genotype population = Genotype.randomInitialGenotype(conf);

			final double maxScore = myFunc.getMaxScore();

			final Thread t1 = new Thread(population);
			threads.add(t1);
			final IEvolutionMonitor monitor = new EvolutionMonitor();

			conf.getEventManager().addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
				public void geneticEventFired(GeneticEvent a_firedEvent) {
					GABreeder genotype = (GABreeder) a_firedEvent.getSource();
					int evno = genotype.getLastConfiguration().getGenerationNr();

					if (evno % 20 == 0) {
						System.out.println(evno);
						double bestFitness = genotype.getLastPopulation().determineFittestChromosome()
								.getFitnessValue();
						System.out.println(t1.getName() + ": Evolving generation " + evno + ", best fitness: "
								+ bestFitness + "/" + maxScore);

						// double score = 0;
						//
						// for (IChromosome chr :
						// genotype.getLastPopulation().getChromosomes()){
						// score+=chr.getFitnessValue();
						// }
						//
						// score/=genotype.getLastPopulation().getChromosomes().size();
						//
						// System.out.println(t1.getName() + ": Evolving
						// generation " + evno + ", mean fitness: "
						// + score + "/" + maxScore);

					}
					if (evno > numEvolutions) {

						IChromosome bestChr = genotype.getLastPopulation().determineFittestChromosome();

						bestResultsPerThread.add(bestChr);

						for (IChromosome chr : genotype.getLastPopulation().getChromosomes()) {

							addToBestOverall(bestResultsOverAll, chr, nSave);

						}

						t1.stop();
						// monitor.getPopulations();

					} else {
						try {
							t1.sleep((j + 1) * 3);
						} catch (InterruptedException iex) {
							iex.printStackTrace();
							System.exit(1);
						}
					}
				}
			});

			t1.setPriority((threadNumber % 2) + 1);
			t1.start();

		}

		for (Thread thread : threads) {
			// permits to wait for the threads to end
			try {
				thread.join();
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
		}

		IChromosome bestCr = null;
		double bestScore = 0;

		System.out.println("Best scores per thread: ");
		for (IChromosome result : bestResultsPerThread) {
			double score = result.getFitnessValue();
			System.out.println(score);
			if (score > bestScore) {
				bestScore = score;
				bestCr = result;
			}
		}

		System.out.println("Best score = " + bestScore);

		EvaluateSimulationScore myFunc = new EvaluateSimulationScore(geneIds, geneCategs, regFile, refFile);

		myFunc.seeSolutionOutput(bestCr, outFolder);

		System.out.println(nSave + " best scores.");

		int index = 1;
		for (IChromosome chr : bestResultsOverAll) {

			System.out.println(chr.getFitnessValue());

			File theDir2 = new File(outFolder + "bestResults/" + index + "/");

			if (!theDir2.exists()) {
				// System.out.println("creating directory: " + outFolder +
				// "bestResults/" + index + "/");
				boolean result = false;

				try {
					theDir2.mkdirs();
					result = true;
				} catch (SecurityException se) {
					// handle it
				}
				if (result) {
					// System.out.println("DIR created");
				}
			}

			myFunc.seeSolutionOutput(chr, outFolder + "bestResults/" + index + "/");

			index += 1;
		}

	}

	private static void readParameters(String path) throws InvalidConfigurationException {

		
		System.out.println("Param file : "+path);

		List<String> params = new ArrayList<String>();

		try {

			BufferedReader in = new BufferedReader(new FileReader(path));

			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#") || line.equals("")) {
					continue;
				}
				params.add(line);
			}

			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sampleGenes = new Gene[params.size()];
		geneIds = new String[params.size()];
		geneCategs = new String[params.size()];

		int index = 0;
		for (String par : params) {

//			System.out.println(par);

			String id = par.split("\t")[0];
			String categ = par.split("\t")[1];
			String type = par.split("\t")[2];
			String lb = par.split("\t")[3];
			String ub = par.split("\t")[4];

			if (type.equals("double")) {
				sampleGenes[index] = new DoubleGene(conf, Double.parseDouble(lb), Double.parseDouble(ub));
			} else if (type.equals("int")) {
				sampleGenes[index] = new IntegerGene(conf, Integer.parseInt(lb), Integer.parseInt(ub));
			}

			geneIds[index] = id;
			geneCategs[index] = categ;

			index++;
		}
		
		System.out.println(params.size()+" parameters.");

	}

	public static void addToBestOverall(List<IChromosome> bestResultsOverAll, IChromosome candidat, int nSave) {

		// if empty we add it
		if (bestResultsOverAll.isEmpty()) {
			bestResultsOverAll.add(candidat);
		}
		// if < max size, we add it ad the right spot
		else if (bestResultsOverAll.size() < nSave) {
			int index = 0;

			boolean isAdded = false;
			for (IChromosome chr : bestResultsOverAll) {
				if (candidat.getFitnessValue() < chr.getFitnessValue()) {
					bestResultsOverAll.add(index, candidat);
					isAdded = true;
					break;
				}
				index++;
			}
			if (!isAdded) {
				bestResultsOverAll.add(index, candidat);
			}
		}
		// if = max size, we put it if it is > to the first element
		else {

			if (candidat.getFitnessValue() > bestResultsOverAll.get(0).getFitnessValue()) {
				int index = 0;
				
				boolean isAdded = false;
				for (IChromosome chr : bestResultsOverAll) {
					if (candidat.getFitnessValue() < chr.getFitnessValue()) {
						bestResultsOverAll.add(index, candidat);
						isAdded = true;
						break;
					}
					index++;
				}
				if (!isAdded) {
					bestResultsOverAll.add(index, candidat);
				}

				bestResultsOverAll.remove(0);
			}

		}

	}

}
