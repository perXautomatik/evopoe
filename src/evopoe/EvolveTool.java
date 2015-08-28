package evopoe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A program that takes options over the command line for various paths in a
 * Path of Exile skill tree and produces the most optimal build for those
 * parameters.
 */
public class EvolveTool {
	public static void main(String[] args) throws Exception {
		EvolveTool tool = new EvolveTool(args);
		tool.run();
	}
	
	protected SkillTree tree;
	protected BuildTarget target;
	protected BuildSearch search;
	
	public EvolveTool(String[] args) throws IOException {
		tree = SkillTree.fromFile("passive-skill-tree.json");
		System.err.printf("Loaded skill tree with %d nodes\n", tree.getNodeCount());
		
		target = new BuildTarget(parseStartingClass(args));
		
		for (String text : args) {
			if (text.equals("")) {
				continue;
			}
			
			String[] parts = text.split("=");
			String modText = parts[0].trim();
			double weight = (parts.length == 1) ? 1.0 : Double.parseDouble(parts[1].trim());
			
			if (text.contains("*")) {
				addMultiMod(modText, weight);
			} else {
				addSingleMod(modText, weight);
			}
		}
		
		search = new BuildSearch(tree, target, 10, 85);
	}
	
	private static Map<String, Integer> startingClasses = new HashMap<String, Integer>();
	
	static {
		startingClasses.put("ranger", 0);
		startingClasses.put("marauder", 1);
		startingClasses.put("duelist", 2);
		startingClasses.put("templar", 3);
		startingClasses.put("witch", 4);
		startingClasses.put("shadow", 5);
		startingClasses.put("scion", 6);
	}
	
	protected static int parseStartingClass(String[] args) {
		for (int i=0; i < args.length; i++) {
			Integer id = startingClasses.get(args[i].toLowerCase());
			
			if (id == null) {
				continue;
			}
			
			args[i] = "";
			return id;
		}
		
		return -1;
	}
	
	protected void addSingleMod(String text, double weight) {
		String mod = target.addGoal(text, weight);
		
		if (weight > 0) {
			System.err.printf("Max for %s is %f\n", mod, tree.getMax(mod));
		}
	}
	
	protected void addMultiMod(String originalText, double weight) {
		int matched = 0;
		
		String text = originalText.replace("*", ".*");
		Pattern pattern = Pattern.compile(text);
		
		for (String mod : tree.getAllMods()) {
			if (pattern.matcher(mod).matches()) {
				addSingleMod(mod, weight);
				matched++;
			}
		}
		
		if (matched <= 0) {
			System.err.printf("WARNING: '%s' doesn't match anything\n", originalText);
		}
	}
	
	public double getBestScore() {
		return target.score(search.getBest());
	}
	
	public double getWorstScore() {
		return target.score(search.getWorst());
	}
	
	public boolean isTargetMet() {
		return getBestScore() >= 3000;
	}
	
	public void run() throws IOException {
		//for (int i=0; i < 5000; i++) {
		//	search.step();
		//	System.err.println();
		//}
		
		int printCounter = 0;
		int saveCounter = 0;
		
		do {
			search.step();
			
			printCounter++;
			
			if (printCounter > 1000) {
				System.err.printf(
					"best=%d  worst=%d  r=%d\n",
					(int)getBestScore(),
					(int)getWorstScore(),
					(int)(search.getMutationRate() * 100)
				);
				
				printCounter = 0;
			}
			
			saveCounter++;
			
			if (saveCounter > 10000) {
				saveBest();
				saveCounter = 0;
			}
		} while (!isTargetMet());
		
		System.err.println("---");
		System.err.println(search.getBest().dump(tree));
	}
	
	protected void saveBest() throws IOException {
		Files.write(Paths.get("best.txt"), search.getBest().dump(tree).getBytes());
	}
}
