package uk.ac.manchester.bioinfo.BioNet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.Viewer;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * BioNet - Biological networks analysis tool
 * A simple program for protein interaction network analysis.
 * 
 * @author Samuel Acosta <samuel.acostamelgarejo@postgrad.manchester.ac.uk>
 * @version 1.0
 * @since 1.0
 */

/**
 * Main application class
 */
public class App extends Application {

	Network network;

	public static void main(String[] args) {
		System.out.println("BioNet running");
		launch(args);
	}

	/**
	 * Start method of the application
	 */
	public void start(Stage primaryStage) {

		try {

			List<Button> buttons = setButtons(primaryStage);

			VBox root = new VBox(7);
			Button button;
			// Buttons disabled except for "Load model from file" (initial state)
			for (int i = 0; i < buttons.size(); i++) {
				button = buttons.get(i);
				if (i > 0)
					button.setDisable(true);
				root.getChildren().add(button);
			}
			root.setAlignment(Pos.CENTER);

			BackgroundImage background = new BackgroundImage(
					new Image(getClass().getClassLoader().getResourceAsStream("background.png"), 424, 600, true, true),
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);
			root.setBackground(new Background(background));

			Scene scene = new Scene(root, 424, 598);
			primaryStage.resizableProperty().setValue(Boolean.FALSE);
			primaryStage.setTitle("BioNet");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, "An exception has occurred:\n" + e.getMessage(), ButtonType.OK);
			alert.setHeaderText("Exception");
			alert.show();
		}

	}

	/**
	 * Method that creates all the panel buttons.
	 * 
	 * @param primaryStage The main stage of the app.
	 * @return A list of all the buttons of the panel.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Button> setButtons(Stage primaryStage) throws IOException {

		List<Button> buttons = new ArrayList<Button>();

		try {

			Button btnReadFile = new Button("Load model from file");
			btnReadFile.setOnAction(e -> {
				try {
					FileChooser fileChooser = new FileChooser();
					File selectedFile = fileChooser.showOpenDialog(primaryStage);
					if (selectedFile == null || "".equals(selectedFile)) {
						throw new IllegalStateException("A file must be specified.");
					}
					this.network = buildFromFile(selectedFile);
					// Enabling buttons
					for (int i = 0; i < buttons.size(); i++) {
						buttons.get(i).setDisable(false);
					}
					Alert alert = new Alert(AlertType.INFORMATION, "File loaded succesfully", ButtonType.OK);
					alert.setHeaderText("Load model from file");
					alert.show();
				} catch (Exception e2) {
					this.network = null;
					for (int i = 0; i < buttons.size(); i++) {
						if (i > 0)
							buttons.get(i).setDisable(true);
					}
					Alert alert = new Alert(AlertType.ERROR, "Exception while reading file:\n" + e2.getMessage(),
							ButtonType.OK);
					alert.show();
				}

			});
			buttons.add(btnReadFile);

			Button btnAddInteraction = new Button("Add protein interaction");
			btnAddInteraction.setOnAction(e -> {
				try {
					Alert alert;
					ChoiceDialog dialog;
					Optional<String> node1, node2;
					List<String> nodes = getNodesNames();
					Collections.sort(nodes);
					dialog = new ChoiceDialog(nodes.get(0), nodes);
					dialog.setTitle("Select proteins");
					dialog.setHeaderText("Please select the first protein:");
					node1 = dialog.showAndWait();
					if (!node1.isPresent()) {
						alert = new Alert(AlertType.ERROR, "Two proteins must be specified", ButtonType.OK);
						alert.showAndWait();
						return;
					}
					dialog = new ChoiceDialog(nodes.get(0), nodes);
					dialog.setTitle("Select proteins");
					dialog.setHeaderText("Please select the second protein:");
					node2 = dialog.showAndWait();
					if (!node2.isPresent()) {
						alert = new Alert(AlertType.ERROR, "Two proteins must be specified", ButtonType.OK);
						alert.showAndWait();
						return;
					}
					addInteraction(getNodeByName(node1.get()), getNodeByName(node2.get()));
					alert = new Alert(AlertType.INFORMATION,
							"Interaction " + node1.get() + "-" + node2.get() + " added succesfully", ButtonType.OK);
					alert.setHeaderText("Add protein interaction");
					alert.show();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR, "Exception while adding interaction:\n" + e2.getMessage(),
							ButtonType.OK);
					alert.show();
				}
			});
			buttons.add(btnAddInteraction);

			Button btnViewNodeDegree = new Button("Get node degree");
			btnViewNodeDegree.setOnAction(e -> {
				try {
					Alert alert;
					ChoiceDialog dialog;
					Optional<String> node;
					List<String> nodes = getNodesNames();
					Collections.sort(nodes);
					dialog = new ChoiceDialog(nodes.get(0), nodes);
					dialog.setTitle("Select protein");
					dialog.setHeaderText("Please select the protein:");
					node = dialog.showAndWait();
					if (!node.isPresent()) {
						alert = new Alert(AlertType.ERROR, "A protein must be specified", ButtonType.OK);
						alert.showAndWait();
						return;
					}
					int degree = getDegree(getNodeByName(node.get()));
					alert = new Alert(AlertType.INFORMATION, "The node degree of the protein is: " + degree,
							ButtonType.OK);
					alert.setHeaderText("Get node degree");
					alert.showAndWait();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while calculating node degree:\n" + e2.getMessage(), ButtonType.OK);
					alert.show();
				}

			});
			buttons.add(btnViewNodeDegree);

			Button btnGetAverageDegree = new Button("Get network average degree");
			btnGetAverageDegree.setOnAction(e -> {
				try {
					double avgDegree = getAverageDegree();
					Alert alert = new Alert(AlertType.INFORMATION,
							"The average degree of the protein network is:\n" + String.valueOf(avgDegree),
							ButtonType.OK);
					alert.setHeaderText("Get network average degree");
					alert.showAndWait();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while calculating average degree:\n" + e2.getMessage(), ButtonType.OK);
					alert.show();
				}
			});
			buttons.add(btnGetAverageDegree);

			Button btnGetNetworkHubs = new Button("Get network hubs");
			btnGetNetworkHubs.setOnAction(e -> {
				try {
					int highestDegree = getHighestDegree();
					List<Node> hubs = getHubs();
					String hubsStr = "";
					for (int i = 0; i < hubs.size(); i++) {
						hubsStr += hubs.get(i).getName() + "\n";
					}
					Alert alert = new Alert(AlertType.INFORMATION,
							"The highest degree of the network is:\n" + highestDegree + "\nHub node(s):\n" + hubsStr,
							ButtonType.OK);
					alert.setHeaderText("Get network hubs");
					alert.showAndWait();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while calculating highest network degree:\n" + e2.getMessage(), ButtonType.OK);
					alert.show();
				}

			});
			buttons.add(btnGetNetworkHubs);

			Button btnViewDegreeDist = new Button("View degree distribution");
			btnViewDegreeDist.setOnAction(e -> {
				try {
					Map<Integer, Integer> degDistribution = getDegreeDistribution();
					String degDistributionStr = "The protein network degree distribution is:\n\n";
					degDistributionStr += "DEGREE\t\tNR. OF NODES\n";
					for (Map.Entry<Integer, Integer> entry : degDistribution.entrySet()) {
						degDistributionStr += entry.getKey() + "\t\t\t" + entry.getValue() + "\n";
					}
					Alert alert = new Alert(AlertType.INFORMATION, degDistributionStr, ButtonType.OK);
					alert.setHeaderText("View degree distribution");
					alert.showAndWait();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while calculating degree distribution:\n" + e2.getMessage(), ButtonType.OK);
					alert.show();
				}
			});
			buttons.add(btnViewDegreeDist);

			Button btnExportDegDistribution = new Button("Export degree distribution");
			btnExportDegDistribution.setOnAction(e -> {
				try {
					DirectoryChooser directoryChooser = new DirectoryChooser();
					Map<Integer, Integer> degDistribution = getDegreeDistribution();
					File selectedDir = directoryChooser.showDialog(primaryStage);
					String fullPath = selectedDir.getAbsolutePath() + "/degree_distribution.txt";
					writeDegreeDistribution(degDistribution, fullPath);
					Alert alert = new Alert(AlertType.INFORMATION, "Degree distribution exported successfully",
							ButtonType.OK);
					alert.setHeaderText("Export degree distribution");
					alert.show();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while exporting degree distribution:\n" + e2.getMessage(), ButtonType.OK);
					alert.show();
				}

			});
			buttons.add(btnExportDegDistribution);

			Button btnDisplayNetwork = new Button("Display network");
			btnDisplayNetwork.setOnAction(e -> {
				try {
					displayNetwork();
				} catch (Exception e2) {
					Alert alert = new Alert(AlertType.ERROR,
							"Exception while trying to display network:\n" + e2.getMessage(), ButtonType.OK);
					alert.setHeaderText("Display network");
					alert.show();
				}
			});
			buttons.add(btnDisplayNetwork);

			Button btnReset = new Button("Reset");
			btnReset.setOnAction(e -> {
				this.network = null;
				for (int i = 0; i < buttons.size(); i++) {
					if (i > 0)
						buttons.get(i).setDisable(true);
				}
			});
			buttons.add(btnReset);

		} catch (Exception e) {
			throw e;
		}

		return buttons;

	}

	/**
	 * Method that reads a txt file containing the model and builds the protein
	 * network.
	 * 
	 * @param file The file selected by the user.
	 * @return The network object with all the edges and nodes.
	 */
	public Network buildFromFile(File file) throws IOException {

		Network newNetwork = new Network();
		String line;
		Node node1;
		Node node2;
		Edge edge;

		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
			Boolean isFirstLine = Boolean.TRUE;
			while ((line = reader.readLine()) != null) {
				if (isFirstLine) {
					// Checking file formatting
					if (line.indexOf("\t") == -1) {
						// First line does not contain tabs, it's not the correct format
						throw new IOException("File not in a supported format.");
					}
					isFirstLine = Boolean.FALSE;
				}
				node1 = new Node(line.split("\t")[0]);
				// Both nodes and edges are added to the network only if not already present
				if (!newNetwork.getNodeList().contains(node1)) {
					newNetwork.getNodeList().add(node1);
				}
				node2 = new Node(line.split("\t")[1]);
				if (!newNetwork.getNodeList().contains(node2)) {
					newNetwork.getNodeList().add(node2);
				}
				edge = new Edge(node1, node2);
				if (!newNetwork.getEdgeList().contains(edge)) {
					newNetwork.getEdgeList().add(edge);
				}
			}

		} catch (IOException e) {
			throw e;
		}

		return newNetwork;
	}

	/**
	 * Method that adds a new edge between two existing nodes in the network.
	 * 
	 * @param node1 The first node of the new edge.
	 * @param node2 The second node of the new edge.
	 * @return The created edge.
	 */
	public Edge addInteraction(Node node1, Node node2) throws IllegalStateException {
		Edge edge = new Edge(node1, node2);
		if (this.network.getEdgeList().contains(edge)) {
			throw new IllegalStateException("Interaction already exists.");
		} else {
			this.network.getEdgeList().add(edge);
		}
		return edge;
	}

	/**
	 * Method that calculates the degree of a single node.
	 * 
	 * @param node The node of the query.
	 * @return The degree of the query node.
	 */
	public int getDegree(Node node) {
		int degree = 0;
		for (int i = 0; i < this.network.getEdgeList().size(); i++) {
			if (this.network.getEdgeList().get(i).getNode1().equals(node)
					&& this.network.getEdgeList().get(i).getNode2().equals(node)) {
				degree = degree + 2; // It's a self-interaction, degree 2
			} else if (this.network.getEdgeList().get(i).getNode1().equals(node)
					|| this.network.getEdgeList().get(i).getNode2().equals(node)) {
				degree++;
			}
		}
		return degree;
	}

	/**
	 * Method that calculates the average degree of the complete network.
	 * 
	 * @return The average degree of the complete network.
	 */
	public double getAverageDegree() {
		int sum = 0;
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			sum += getDegree(this.network.getNodeList().get(i));
		}
		return (double) sum / this.network.getNodeList().size();
	}

	/**
	 * Method that calculates the hub or hubs of the network.
	 * 
	 * @return A list of all the hubs in the network (single element if it's just
	 *         one).
	 */
	public List<Node> getHubs() {
		List<Node> hubList = new ArrayList<Node>();
		Node node;
		int highestDegree = getHighestDegree();
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			node = this.network.getNodeList().get(i);
			if (highestDegree == getDegree(node)) {
				hubList.add(node);
			}
		}
		return hubList;
	};

	/**
	 * Method that calculates the highest degree of the network.
	 * 
	 * @return An int representing the highest degree of the network.
	 */
	public int getHighestDegree() {
		int highestDegree = 0;
		int degree = 0;
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			degree = getDegree(this.network.getNodeList().get(i));
			if (degree > highestDegree) {
				highestDegree = degree;
			}
		}
		return highestDegree;
	}

	/**
	 * Method that calculates the degree distribution of the network.
	 * 
	 * @return A map of each degree of the network and the amount of nodes with that
	 *         degree.
	 */
	public Map<Integer, Integer> getDegreeDistribution() {
		Integer degree;
		HashMap<Integer, Integer> degreeDistribution = new HashMap<Integer, Integer>();
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			degree = getDegree(this.network.getNodeList().get(i));
			if (degreeDistribution.containsKey(degree)) {
				degreeDistribution.put(degree, degreeDistribution.get(degree) + 1);
			} else {
				degreeDistribution.put(degree, 1);
			}
		}
		return degreeDistribution;
	}

	/**
	 * Method that writes the degree distribution to a specified directory.
	 * 
	 * @param degDistribution A map of each degree of the network and the amount of
	 *                        nodes with that degree.
	 * @param outputFilename  The absolute path of the file selected by the user.
	 */
	public void writeDegreeDistribution(Map<Integer, Integer> degDistribution, String outputFilename)
			throws IOException {
		Path outputFile = Paths.get(outputFilename);
		try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
			writer.write("PROTEIN NETWORK DEGREE DISTRIBUTION\n");
			writer.write("-----------------------------------\n");
			writer.write("DEGREE" + "\tNR OF NODES\n");
			for (Map.Entry<Integer, Integer> entry : degDistribution.entrySet()) {
				writer.write(entry.getKey() + "\t\t" + entry.getValue() + "\n");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Method that returns a Node element from the network from based on its name.
	 * 
	 * @param nodeName The name of the query node.
	 * @return The node element corresponding to the query.
	 */
	public Node getNodeByName(String nodeName) {
		for (Node node : this.network.getNodeList()) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Method that returns a list of all the names of nodes in the network.
	 * 
	 * @return A list of all the names of nodes in the network.
	 */
	public List<String> getNodesNames() {
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			names.add(this.network.getNodeList().get(i).getName());
		}
		return names;
	}

	/**
	 * Method that produces a visual representation of the whole network in its
	 * current state.
	 * 
	 */
	public void displayNetwork() {
		String nodeName;
		Graph graph = new SingleGraph("BioNetGraph");
		for (int i = 0; i < this.network.getNodeList().size(); i++) {
			nodeName = this.network.getNodeList().get(i).getName();
			graph.addNode(nodeName).addAttribute("ui.label", nodeName);
		}
		String node1Name, node2Name, edgeName;
		for (int i = 0; i < this.network.getEdgeList().size(); i++) {
			node1Name = this.network.getEdgeList().get(i).getNode1().getName();
			node2Name = this.network.getEdgeList().get(i).getNode2().getName();
			edgeName = node1Name + "-" + node2Name;
			graph.addEdge(edgeName, node1Name, node2Name);
		}
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		Viewer viewer = graph.display();
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
	}

}