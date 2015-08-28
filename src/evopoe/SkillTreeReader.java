package evopoe;

import java.io.IOException;
import java.nio.file.*;

import org.json.JSONObject;

public class SkillTreeReader {
	public SkillTreeReader() {
		
	}
	
	public SkillTree readFile(String path) throws IOException {
		return read(Paths.get(path));
	}
	
	public SkillTree read(Path path) throws IOException {
		return read(new String(Files.readAllBytes(path)));
	}
	
	public SkillTree read(String source) {
		JSONObject root = new JSONObject(source);
		SkillTreeBuilder builder = new SkillTreeBuilder();

		for (Object dataObj : root.getJSONArray("nodes")) {
			JSONObject data = (JSONObject)dataObj;
			SkillTreeNode node = createNode(data);
			builder.addNode(node);
			
			for (Object otherID : data.getJSONArray("out")) {
				builder.connect(node.id, (int)otherID);
			}
			
			if (data.has("sd")) {
				for (Object mod : data.getJSONArray("sd")) {
					node.addMod((String)mod);
				}
			}
		}
		
		for (Object id : root.getJSONObject("root").getJSONArray("out")) {
			builder.setEntryNode((int)id);
		}
		
		return builder.build();
	}
	
	protected SkillTreeNode createNode(Object data) {
		return createNode((JSONObject)data);
	}
	
	protected SkillTreeNode createNode(JSONObject data) {
		int id = data.getInt("id");
		String name = data.getString("dn");
		
		return new SkillTreeNode(id, name);
	}
}
