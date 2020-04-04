package uk.ac.manchester.bioinfo.BioNet;

/**
 * BioNet
 * 
 * Class representing an edge of the network (a link between two proteins).
 * 
 * @author Samuel Acosta <samuel.acostamelgarejo@postgrad.manchester.ac.uk>
 * @version 1.0
 * @since 1.0
 */
public class Edge {

	private Node node1;
	private Node node2;

	public Edge(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node1 == null) ? 0 : node1.hashCode());
		result = prime * result + ((node2 == null) ? 0 : node2.hashCode());
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
		Edge other = (Edge) obj;
		// Compare both ways
		return compareNodes(other.node1, other.node2) || compareNodes(other.node2, other.node1);
	}

	private boolean compareNodes(Node firstNode, Node secondNode) {
		if (node1 == null) {
			if (firstNode != null)
				return false;
		} else if (!node1.equals(firstNode))
			return false;
		if (node2 == null) {
			if (secondNode != null)
				return false;
		} else if (!node2.equals(secondNode))
			return false;
		return true;
	}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

}
