package evopoe;

import java.io.IOException;
import java.util.*;


public class SkillTree {
	protected SkillTreeNode[] nodes;
	protected SkillTreeNode[] entries;
	protected Map<String, Double> totals;
	
	public SkillTree(SkillTreeNode[] nodes, SkillTreeNode[] entries) {
		if (nodes == null || entries == null) {
			throw new NullPointerException("Array cannot be null");
		}
		
		checkArray(nodes);
		checkArray(entries);
		
		this.nodes = nodes;
		this.entries = entries;
		this.totals = new HashMap<String, Double>();
		
		for (SkillTreeNode node : nodes) {
			mergeStats(totals, node.getMods());
		}
	}
	
	public static void mergeStats(Map<String, Double> a, Map<String, Double> b) {
		for (Map.Entry<String, Double> entry : b.entrySet()) {
			String key = entry.getKey();
			Double x = a.get(key);
			Double y = entry.getValue();
			
			if (x == null) {
				x = 1.0;
			}
			
			if (y == null) {
				y = 1.0;
			}
			
			a.put(key, x + y);
		}
	}
	
	public SkillTree(Collection<SkillTreeNode> nodes, Collection<SkillTreeNode> entries) {
		this(toArray(nodes), toArray(entries));
	}
	
	public int getNodeCount() {
		return nodes.length;
	}

	public SkillTreeNode pickEntryNode(int x) {
		x = Math.abs(x) % entries.length;
		return entries[x];
	}
	
	private static SkillTreeNode[] toArray(Collection<SkillTreeNode> nodes) {
		return nodes.toArray(new SkillTreeNode[nodes.size()]);
	}
	
	public static void checkArray(SkillTreeNode[] nodes) {
		for (SkillTreeNode node : nodes) {
			if (node == null) {
				throw new NullPointerException("Array cannot contain null nodes");
			}
		}
	}

	public static SkillTree fromFile(String strPath) throws IOException {
		return new SkillTreeReader().readFile(strPath);
	}

	public double getMax(String mod) {
		Double x = totals.get(mod);
		return (x == null) ? 0.0 : x;
	}

	public Iterable<String> getAllMods() {
		return totals.keySet();
	}
}
