package evopoe;

import java.util.*;

public class Build {
	protected int[] data;
	protected Map<String, Double> stats;

	public Build(SkillTree tree, int[] data) {
		this.data = data;
		this.stats = new HashMap<String, Double>();
		
		walk(tree, new Visitor() {
			public void visit(SkillTreeNode node) {
				addNode(node);
			}
		});
	}
	
	public static void relativeToAbsolute() {
		
	}
	
	public static interface Visitor {
		public void visit(SkillTreeNode node);
	}
	
	public void walk(SkillTree tree, Visitor visitor) {
		// Nodes that are still available for selection. Once selected
		// the node is removed and put into the closed set
		List<SkillTreeNode> openNodes = new ArrayList<SkillTreeNode>(128);
		Set<SkillTreeNode> visited = new HashSet<SkillTreeNode>(1024);

		// The first element of the data array always denotes what character
		// class to select, which populates the initial open node list
		openNodes.add(tree.pickEntryNode(data[0]));

		// Since we used the first element the search starts at 1 instead of 0
		int pos = 1;

		// The walk continues as long as we don't run out of choices
		while (!openNodes.isEmpty() && pos < data.length) {
			// Grab a node from the open list
			int x = Math.abs(data[pos] % openNodes.size());
			SkillTreeNode node = openNodes.remove(x);
			pos++;

			if (node == null) {
				throw new NullPointerException();
			}
			
			visited.add(node);
			
			for (SkillTreeNode sibling : node.getSiblings()) {
				if (!visited.contains(sibling)) {
					openNodes.add(sibling);
					visited.add(sibling);
				}
			}
			
			visitor.visit(node);
		}
	}
	
	protected void addNode(SkillTreeNode node) {
		for (Map.Entry<String, Double> entry : node.getMods().entrySet()) {
			addMod(entry.getKey(), entry.getValue());
		}
	}
	
	protected void addMod(String mod, Double x) {
		Double y = stats.get(mod);
		
		if (x == null) {
			x = 1.0;
		}
		
		if (y == null) {
			stats.put(mod, x);
			return;
		}
		
		stats.put(mod, x + y);
	}
	
	public int getDataSize() {
		return data.length;
	}
	
	public int getDataValue(int i) {
		return data[i];
	}
	
	public Map<String, Double> getStats() {
		return stats;
	}

	public double getMod(String mod) {
		Double x = stats.get(mod);
		return (x == null) ? 0.0 : x;
	}
	
	public String dump(SkillTree tree) {
		StringBuilder s = new StringBuilder();
		
		s.append("---------------------\n");
		
		for (Map.Entry<String, Double> entry : stats.entrySet()) {
			String mod = entry.getKey();
			Double value = entry.getValue();
			s.append(value).append(" ").append(mod).append("\n");
		}
		
		s.append("---------------------\n");
		
		walk(tree, new Visitor() {
			public void visit(SkillTreeNode node) {
				s.append(node.name).append("\n");
			}
		});
		
		s.append("---------------------\n");
		
		return s.toString();
	}
}
