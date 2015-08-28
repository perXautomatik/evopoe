package evopoe;

import java.util.*;

public class SkillTreeBuilder {
	protected Map<Integer, SkillTreeNode> map;
	protected Set<Long> links;
	protected Set<Integer> linked;
	protected List<SkillTreeNode> entryNodes;
	
	public SkillTreeBuilder() {
		this.map = new TreeMap<Integer, SkillTreeNode>();
		this.links = new HashSet<Long>();
		this.linked = new HashSet<Integer>();
		this.entryNodes = new ArrayList<SkillTreeNode>();
	}
	
	public void addNode(SkillTreeNode node) {
		if (map.containsKey(node.id)) {
			throw new RuntimeException("Node already exists");
		}
		
		map.put(node.id, node);
	}
	
	public void setEntryNode(int id) {
		setEntryNode(map.get(id));
	}
	
	public void setEntryNode(SkillTreeNode node) {
		if (node == null) {
			throw new IllegalArgumentException("Invalid node ID");
		}
		
		if (entryNodes.contains(node)) {
			return;
		}
		
		entryNodes.add(node);
	}
	
	public void connect(int a, int b) {
		if (a <= 0 || b <= 0) {
			throw new IllegalArgumentException("Both node IDs must be valid");
		}
		
		links.add(combineKey(a, b));
		linked.add(a);
		linked.add(b);
	}
	
	private static long combineKey(long a, long b) {
		long c;
		
		a &= 0xFFFFFFFF;
		b &= 0xFFFFFFFF;
		
		if (b > a) {
			c = a;
			a = b;
			b = c;
		}
		
		return a | (b << 32);
	}
	
	private static int firstKey(long key) {
		return (int)(key & 0xFFFFFFFF);
	}
	
	private static int secondKey(long key) {
		return (int)((key >> 32) & 0xFFFFFFFF);
	}
	
	public SkillTree build() {
		prune();
		
		for (long key : links) {
			SkillTreeNode a = map.get(firstKey(key));
			SkillTreeNode b = map.get(secondKey(key));
			
			a.connect(b);
			b.connect(a);
		}
		
		System.err.printf("%d total edges\n", links.size());
		System.err.printf("%d total entries\n", entryNodes.size());
		
		return new SkillTree(map.values(), entryNodes);
	}
	
	protected void prune() {
		List<Integer> unused = new LinkedList<Integer>();
		
		for (int id : map.keySet()) {
			if (linked.contains(id)) {
				continue;
			}
			
			unused.add(id);
		}
		
		if (!unused.isEmpty()) {
			System.err.printf("Pruning %d nodes\n", unused.size());
		}
		
		for (int id : unused) {
			//System.err.printf("Pruning %d\n", id);
			map.remove(id);
		}
	}
}
