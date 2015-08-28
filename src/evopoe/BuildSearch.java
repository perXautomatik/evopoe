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
	
	public Build cross(Build a, Build b) {
		int count = a.getDataSize();
		int[] data = new int[count];
		
		int split = random.nextInt(count - 1);
		
		for (int i=0; i < split; i++) {
			data[i] = a.getDataValue(i);
		}
		
		if (random.nextDouble() <= mutationRate) {
			for (int i=split; i < count; i++) {
				data[i] = random.nextInt();
			}
		} else {
			for (int i=split; i < count; i++) {
				data[i] = b.getDataValue(i);
			}
		}
		
		if (target.getStartingClass() >= 0) {
			data[0] = target.getStartingClass();
		}
		
		return new Build(tree, data);
	}
	
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
		
		if (r > 1.0) {
			r = 0.0;
		} else {
			r = 1.0 - r;
		}
		
		mutationRate = 0.5 * r;
		
		for (int i=count / 2; i < count; i++) {
			population[i] = cross();
		}
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
