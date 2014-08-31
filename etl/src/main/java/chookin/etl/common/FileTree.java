package chookin.etl.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.helper.Validate;

/**
 *
 */
public class FileTree {
	public class FileNode {
		/**
		 * for the file not root, its name is only its file name, without
		 * director path
		 */
		private String name;
		private FileNode parent = null;
		private Map<String, FileNode> children = new ConcurrentHashMap<String, FileNode>();
		private int level;
		public FileNode(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			return (this.parent == null ? 0 : this.parent.hashCode()) * 10000
					+ this.name.hashCode();
		}

		@Override
		public String toString() {
			return (this.parent == null) ? this.name : this.parent.toString()
					+ "/" + this.name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof FileNode) {
				FileNode node = (FileNode) obj;
				return this.name == node.name && this.parent == node.parent;
			}
			return false;
		}

		public String getName() {
			return this.name;
		}

		public FileNode getParent() {
			return this.parent;
		}

		/**
		 * add child
		 * 
		 * @param childName
		 *            the child name
		 * @return the child added
		 */
		public FileNode addChild(String childName) {
			if (this.children.containsKey(childName)) {
				return this.children.get(childName);
			}
			FileNode node = new FileNode(childName);
			node.parent = this;
			node.level = this.level + 1;
			this.children.put(childName, node);
			return node;
		}

		/**
		 * get the child by its name
		 * 
		 * @param childName
		 * @return
		 */
		public FileNode getChild(String childName) {
			return this.children.get(childName);
		}
	}

	private FileNode root;

	public FileTree(String rootFile) {
		Validate.notNull(rootFile);
		String[] names = rootFile.split(fileNameSplitRegex);
		FileNode node = this.root;
		for (String name : names) {
			if (name.isEmpty()) {
				continue;
			}
			if (this.root == null) {
				this.root = new FileNode(name);
				this.root.level = 0;
				node = this.root;
			} else {
				node = node.addChild(name);
			}
		}
	}

	public FileNode getRoot() {
		return this.root;
	}
	
	public FileNode getNode(String path) {
		Validate.notNull(path);
		String[] names = path.split(fileNameSplitRegex);
		FileNode current = this.root;
		for (String name : names) {
			if (name.isEmpty()) {
				continue;
			}
			current = current.getChild(name);
			if (current == null) {
				return null;
			}
		}
		return current;
	}
	@Override 
	public String toString(){
		if(this.root == null){
			return "";
		}
		return toString(this.root);
	}
	private String toString(FileNode node){
		StringBuilder strb = new StringBuilder().append('{');
		strb.append(node.name);
		if(node.children.isEmpty()){
		}else {
			strb.append(", ");
			for(FileNode item: node.children.values()){
				strb.append(toString(item)).append(",");
			}
			strb.deleteCharAt(strb.length() -1);
		}
		strb.append("}");
		return strb.toString();
	}
	private static final String fileNameSplitRegex = "[/\\\\]";

	/**
	 * add a file node of the assigned path to this tree
	 * 
	 * @param path
	 * @return the node last added, that is the leaf node; if the node of this
	 *         path already exists, still return the leaf node
	 */
	public FileNode addNode(String path) {
		Validate.notNull(path);
		String[] names = path.split(fileNameSplitRegex);
		FileNode current = this.root;
		for (String name : names) {
			if (name.isEmpty()) {
				continue;
			}
			if (name.equals(current.name)) {
				continue;
			}
			FileNode myNode = current.getChild(name);
			if (myNode == null) {
				myNode = current.addChild(name);
			}
			current = myNode;
		}
		return current;
	}

	/**
	 * get the relative path of a node base on another node
	 * 
	 * @param baseNode
	 * @param linkedNode
	 * @return
	 */
	public String getRelativePath(FileNode baseNode, FileNode linkedNode) {
		Validate.notNull(baseNode);
		Validate.notNull(linkedNode);
		return ResourceHelper.getRelativePath(baseNode.toString(), linkedNode.toString());
	}
}
