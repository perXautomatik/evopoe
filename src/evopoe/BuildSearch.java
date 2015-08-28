package evopoe;

import java.util.*;

public class BuildSearch implements Comparator<Build> {
	protected SkillTree tree;
	protected Random random;
	protected double mutationRate;
	protected Build[] population;
	protected int maxSkillPoints;
	protected BuildTarget target;
	
	public BuildSearch(SkillTree tree, BuildTarget target, int popSize, int maxSkillPoints) {
		this.random = new Random();
		this.tree = tree;
		this.target = target;
		this.maxSkillPoints = maxSkillPoints;
		this.mutationRate = 1.0;
		
		setPopulation(popSize);
	}
	
	public void setPopulation(int size) {
		population = new Build[size];
		
		for (int i=0; i < size; i++) {
			population[i] = generateBuild();
		}
	}
	
	protected Build generateBuild() {
		int[] data = new int[1 + maxSkillPoints];
		
		for (int i=0; i < data.length; i++) {
			data[i] = random.nextInt();
		}
		
		if (target.getStartingClass() >= 0) {
			data[0] = target.getStartingClass();
		}
		
		return new Build(tree, data);
	}
	
	public double getMutationRate() {
		return mutationRate;
	}
	
	//public void setMutationRate(double r) {
	//	if (r <= 0) {
	//		throw new IllegalArgumentException("Mutation rate cannot be zero or negative");
	//	}
	//	
	//	this.mutationRate = r;
	//}
	
	public Build cross(Build a, Build b) {
		int count = a.getDataSize();
		int[] data = new int[count];
		/*
		int i;
		
		for (i=0; i < count; i++) {
			if (random.nextFloat() >= 0.5) {
				data[i] = a.getDataValue(i);
			} else {
				data[i] = b.getDataValue(i);
			}
		}
		*/
		
		int split = random.nextInt(count - 1);
		
		for (int i=0; i < split; i++) {
			data[i] = a.getDataValue(i);
		}
		
		for (int i=split; i < count; i++) {
			data[i] = b.getDataValue(i);
		}
		
		if (random.nextDouble() <= mutationRate) {
			//int p = 1;// + 5 * random.nextInt(1 + (int)(55 * mutationRate));
			
			//for (int i=0; i < p; i++) {
				//int x = random.nextInt(1 + split);
				int x = random.nextInt(count);
				data[x]++;// ^= random.nextInt();
			//}
		}
		
		if (target.getStartingClass() >= 0) {
			data[0] = target.getStartingClass();
		}
		
		return new Build(tree, data);
	}
	
	//protected boolean checkMutate() {
	//	return random.nextFloat() <= mutationRate;
	//}
	
	//protected void mutate(int[] data) {
	//	int count = 5 * random.nextInt(5);
	//	
	//	for (int x=0; x < count; x++) {
	//		int i = random.nextInt(data.length);
	//		data[i] ^= random.nextInt();
	//	}
	//}
	
	public Build cross() {
		int a, b;
		int max = population.length / 2;
		
		do {
			a = random.nextInt(max);
			b = random.nextInt(max);
		} while (a == b);
		
		return cross(population[a], population[b]);
	}
	
	public void step() {
		int count = population.length;
		Arrays.sort(population, this);
		
		double bestScore = target.score(getBest());
		double worstScore = target.score(getWorst());
		double span = (bestScore - worstScore);
		double r = span / bestScore;
		//r = r * r;
		if (r > 1.0) r = 1.0;
		r = 1.0 - r;
		//r = r * 0.05;
		mutationRate = 0.75 * r;
		
		//mutationRate = 0.05;
		//System.err.printf("rate=%f\n", mutationRate);
		
		int limit = count - 1;
		
		population[count / 2 - 2] = generateBuild();
		//
		for (int i=count / 2; i < limit; i++) {
			population[i] = cross();
		}
		
		population[limit] = cross(population[0], generateBuild());
		//population[limit] = generateBuild();
	}
	
	public Build getBest() {
		return population[0];
	}
	
	public Build getWorst() {
		return population[population.length / 2 - 1];
	}
	
	@Override
	public int compare(Build a, Build b) {
		double x = target.score(b) - target.score(a);
		return (int)(x * 100);
	}
}
