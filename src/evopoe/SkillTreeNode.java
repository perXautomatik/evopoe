package evopoe;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SkillTreeNode {
	public final int id;
	public final String name;
	protected List<SkillTreeNode> links;
	protected Map<String, Double> stats;
	protected List<String> mods;
	
	public SkillTreeNode(int id, String name) {
		if (id <= 0) {
			throw new IllegalArgumentException("Invalid skill ID");
		}
		
		this.id = id;
		this.name = name;
		this.stats = new HashMap<String, Double>();
		this.mods = new ArrayList<String>();
		this.links = new ArrayList<SkillTreeNode>();
	}
	
	public Map<String, Double> getMods() {
		return stats;
	}
	
	public void addMod(String text) {
		Matcher matcher = numberPattern.matcher(text);
		Double x;
		
		if (matcher.find()) {
			x = Double.parseDouble(matcher.group(0));
			text = matcher.replaceAll("");
		} else {
			x = null;
		}
		
		text = text.replace("-", "");
		text = text.replace("+", "");
		text = text.replace(" ", "");
		text = text.replace("%", "");
		text = text.replace(".", "");
		text = text.replace(",", "");
		text = text.replace("\n", "");
		text = text.replace("\r", "");
		text = text.trim();
		text = text.toLowerCase();
		
		//System.err.printf("%f %s\n", x, text);
		
		if (stats.containsKey(text)) {
			if (x == null) {
				return;
			}
			
			Double y = stats.get(text);
			
			if (y == null) {
				stats.put(text, x);
			} else {
				stats.put(text, x + y);
			}
		} else {
			stats.put(text, x);
		}
	}
	
	public void connect(SkillTreeNode other) {
		if (this == other) {
			return;
		}
		
		if (other == null) {
			throw new NullPointerException();
		}
		
		if (links.contains(other)) {
			return;
		}
		
		links.add(other);
	}

	public Iterable<SkillTreeNode> getSiblings() {
		return links;
	}
	
	private static Pattern numberPattern = Pattern.compile("[0-9]+[0-9\\.]*");
}
