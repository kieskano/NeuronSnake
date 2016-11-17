package han.model.network;

import java.util.*;

/**
 * Created by han on 16-11-16.
 * Network class containing nodes of the network
 */
public class Network {

    /**
     * Lists to store the nodes that make up the network
     */
    private List<Node> inputNodeGroup = new ArrayList<>();
    private List<List<Node>> hiddenNodeGroupList = new ArrayList<>();
    private List<Node> outputNodeGroup = new ArrayList<>();
    private List<Edge> edgeList = new ArrayList<>();
    private int totalAmountOfGroups = 0;
    private List<Integer> dimensions = new ArrayList<>();

    /**
     * Constructor for the network when provided with all the variable amount of nodes per node group
     *
     * @param amountOfInputNodes       Amount of input nodes in input node group
     * @param amountOfHiddenNodeGroups Amount of hidden node group
     * @param amountOfHiddenNodes      Amount of hidden Nodes per hidden node group
     * @param amountOfOutputNodes      Amount of output Nodes per output node group
     */
    public Network(int amountOfInputNodes, int amountOfHiddenNodeGroups, int amountOfHiddenNodes,
                   int amountOfOutputNodes) {
        addToDimensions(amountOfInputNodes, amountOfHiddenNodeGroups, amountOfHiddenNodes,
                amountOfOutputNodes);
        createNodes(amountOfInputNodes, amountOfHiddenNodeGroups, amountOfHiddenNodes, amountOfOutputNodes);
        try {
            createEdges(inputNodeGroup, hiddenNodeGroupList.get(0));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("There should at least be one hidden node group");
        }
        for (int i = 1; i < hiddenNodeGroupList.size(); i++) {
            createEdges(hiddenNodeGroupList.get(i - 1), hiddenNodeGroupList.get(i));
        }
        createEdges(hiddenNodeGroupList.get(hiddenNodeGroupList.size() - 1), outputNodeGroup);
    }

    /**
     * Adds the nodes to the network
     *
     * @param amountOfInputNodes       Amount of input nodes in input node group
     * @param amountOfHiddenNodeGroups Amount of hidden node group
     * @param amountOfHiddenNodes      Amount of hidden Nodes per hidden node group
     * @param amountOfOutputNodes      Amount of output Nodes per output node group
     */
    private void createNodes(int amountOfInputNodes, int amountOfHiddenNodeGroups, int amountOfHiddenNodes,
                             int amountOfOutputNodes) {
        this.totalAmountOfGroups = 1 + amountOfHiddenNodeGroups + 1;
        for (int i = 0; i < amountOfInputNodes; i++) {
            inputNodeGroup.add(new Node(0, i, Node.TypeOfNode.INPUT));
        }
        for (int i = 0; i < amountOfOutputNodes; i++) {
            outputNodeGroup.add(new Node(amountOfHiddenNodeGroups + 1, i, Node.TypeOfNode.OUTPUT));
        }
        for (int i = 0; i < amountOfHiddenNodeGroups; i++) {
            List<Node> hiddenNodeGroup = new ArrayList<>();
            for (int j = 0; j < amountOfHiddenNodes; j++) {
                hiddenNodeGroup.add(new Node(i + 1, j, Node.TypeOfNode.HIDDEN));
            }
            hiddenNodeGroupList.add(hiddenNodeGroup);
        }
    }

    private void createEdges(List<Node> sourceNodeList, List<Node> destinationNodeList) {
        for (Node sourceNode : sourceNodeList) {
            Collections.shuffle(destinationNodeList);
            for (Node destinationNode : destinationNodeList) {
                Edge edge = new Edge(sourceNode, destinationNode);
                sourceNode.addEdge(edge);
                edgeList.add(edge);
            }
        }
    }

    public void addToDimensions(int a, int b, int c, int d) {
        dimensions.add(a);
        dimensions.add(b);
        dimensions.add(c);
        dimensions.add(d);
    }

    public void propagateSignal() {
        initStage();
        propagateStage();
        updateStage();
    }

    private void updateStage() {

        for (List<Node> hiddenNodeGroup : hiddenNodeGroupList) {
            for (Node hiddenNode : hiddenNodeGroup) {
                hiddenNode.applyActivationFunction();
                hiddenNode.updateColor();
                for (Edge edge : hiddenNode.getEdges()) {
                    edge.updateColor();
                }
            }
        }

        for (Node outputNode : outputNodeGroup) {
            outputNode.applyActivationFunction();
            outputNode.updateColor();
        }
    }

    private void propagateStage() {
        for (Node inputNode : inputNodeGroup) {
            if (inputNode.getStrength() > 0) {
                for (Edge edge : inputNode.getEdges()) {
                    if (edge.getWeight() != 0) {
                        edge.getDestinationNode().addToInputSignals(edge.getWeight() * inputNode.getStrength());
                    }
                }
            }
        }

        for (List<Node> hiddenNodeGroup : hiddenNodeGroupList) {
            for (Node hiddenNode : hiddenNodeGroup) {
                hiddenNode.applyActivationFunction();
                hiddenNode.updateColor();
                for (Edge edge : hiddenNode.getEdges()) {
                    edge.updateColor();
                }
                if (hiddenNode.getStrength() > 0) {
                    for (Edge edge : hiddenNode.getEdges()) {
                        if (edge.getWeight() != 0) {
                            edge.getDestinationNode().addToInputSignals(edge.getWeight() * hiddenNode.getStrength());
                        }
                    }
                }
            }
        }
    }

    private void initStage() {
        Random random = new Random();
        for (Node inputNode : inputNodeGroup) {
            //TODO This function currently sets the inputs to a random double, this should be from surroundings
            //inputNode.getInputStrenght() or so
            double rand = random.nextDouble();
            if (rand < 0.5) {
                inputNode.setStrength(0);
            } else {
                inputNode.setStrength(random.nextDouble());
            }
            inputNode.updateColor();
            for (Edge edge : inputNode.getEdges()) {
                edge.updateColor();
            }
        }

        //Reset hidden nodes
        for (List<Node> hiddenNodeGroup : hiddenNodeGroupList) {
            for (Node hiddenNode : hiddenNodeGroup) {
                hiddenNode.setStrength(0);
                hiddenNode.setInputSignals(0);
            }
        }

        //Reset output nodes
        for (Node outputNode : outputNodeGroup) {
            outputNode.setStrength(0);
            outputNode.setInputSignals(0);
        }
    }

    public int getAmountOfInputNodes() {
        return dimensions.get(0);
    }

    public int getAmountOfHiddenNodeGroups() {
        return dimensions.get(1);
    }

    public int getAmountOfHiddenNodes() {
        return dimensions.get(2);
    }

    public int getAmountOfOutputNodes() {
        return dimensions.get(3);
    }

    public List<Node> getInputNodeGroup() {
        return inputNodeGroup;
    }

    public List<List<Node>> getHiddenNodeGroupList() {
        return hiddenNodeGroupList;
    }

    public List<Node> getOutputNodeGroup() {
        return outputNodeGroup;
    }

    public List<Edge> getEdgeList() {
        return edgeList;
    }

    public int getTotalAmountOfGroups() {
        return totalAmountOfGroups;
    }
}
