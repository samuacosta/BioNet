package uk.ac.manchester.bioinfo.BioNet;

/**
 * BioNet
 * 
 * Class representing a node of the network (a protein).
 * 
 * @author Samuel Acosta <samuel.acostamelgarejo@postgrad.manchester.ac.uk>
 * @version 1.0
 * @since 1.0
 */
public class Node {

	private String name;

	public Node() {
	}

	public Node(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
