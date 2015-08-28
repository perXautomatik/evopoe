package evopoe;

import java.util.*;

public class BuildTarget {
	protected int startingClass;
	protected Map<String, Double> goals;
	protected WeakHashMap<Build, Double> scoreCache;
	
	public BuildTarget(int startingClass) {
		this.startingClass = startingClass;
		this.goals = new HashMap<String, Double>();
		this.scoreCache = new WeakHashMap<Build, Double>();
	}
	
	public int getStartingClass() {
		return startingClass;
	}
	
	public double getWeight(String goal) {
		Double x = goals.get(goal);
		return (x == null) ? 0.0 : x;
	}
	
	public Iterable<String> getGoals() {
		return goals.keySet();
	}
	
	public String addGoal(String mod, double weight) {
		mod = mod.replaceAll("[0-9.% ,\r\n]", "");
		mod = mod.toLowerCase();
		
		//if (goals.containsKey(mod)) {
		//	return mod;
		//}
		
		goals.put(mod, weight);
		
		System.err.printf("Added goal %s at weight %f\n", mod, weight);
		return mod;
	}
	
	public double score(Build build) {
		double sum = 0.0;
		
		Double cached = scoreCache.get(build);
		
		if (cached != null) {
			return cached;
		}
		
		for (Map.Entry<String, Double> entry : goals.entrySet()) {
			String mod = entry.getKey();
			double weight = entry.getValue();
			
			sum += build.getMod(mod) * weight;
		}
		
		scoreCache.put(build, sum);
		
		return sum;
	}
}
