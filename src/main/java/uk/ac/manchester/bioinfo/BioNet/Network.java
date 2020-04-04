package uk.ac.manchester.bioinfo.BioNet;

import java.util.ArrayList;
import java.util.List;

/**
 * BioNet
 * 
 * Class representing a complete network.
 * 
 * @author Samuel Acosta <samuel.acostamelgarejo@postgrad.manchester.ac.uk>
 * @version 1.0
 * @since 1.0
 */
public class Network {

	private List<Node> nodeList;
	private List<Edge> edgeList;

	public Network() {
		this.nodeList = new ArrayList<Node>();
		this.edgeList = new ArrayList<Edge>();
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public List<Edge> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<Edge> edgeList) {
		this.edgeList = edgeList;
	}

}
