import java.util.*;

public class Graph {
    private final GraphNode[] vertices;  // Adjacency list for graph.
    private final String name;  //The file from which the graph was created.
    private final int[][] rGraph;

    public Graph(String name, int vertexCount) {
        this.name = name;

        vertices = new GraphNode[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices[vertex] = new GraphNode(vertex);
        }

        this.rGraph = new int[vertexCount][vertexCount];
    }

    public boolean addEdge(int source, int destination, int capacity) {
        // A little bit of validation
        if (source < 0 || source >= vertices.length) return false;
        if (destination < 0 || destination >= vertices.length) return false;

        // This adds the actual requested edge, along with its capacity
        this.vertices[source].addEdge(source, destination, capacity);
        this.vertices[destination].addEdge(destination, source, 0);

        return true;
    }

    /**
     * Algorithm to find max-flow in a network
     */
    public int findMaxFlow(int s, int t, boolean report) {
        fillResidualGraph();
        int[][] holderGraph = new int[this.rGraph.length][this.rGraph.length];
        int totalFlow = 0;
        int availableFlow;
        int currS;
        int currT;
        if (report) {
            System.out.printf("-- Max Flow: %s --\n", this.name);
        }

        while (hasAugmentingPath(s, t)) {
            ArrayList<Integer> currPath = new ArrayList<>();
            currPath.add(s);
            availableFlow = Integer.MAX_VALUE;

            currT = t;
            while (currT != s) {
                currPath.add(1, currT);
                currS = this.vertices[currT].parent;
                availableFlow = Math.min(availableFlow, this.rGraph[currS][currT]);
                currT = currS;
            }

            currT = t;
            while (currT != s) {
                currS = this.vertices[currT].parent;
                if (report) {
                    holderGraph[currS][currT] += availableFlow;
                }

                this.rGraph[currS][currT] -= availableFlow;
                this.rGraph[currT][currS] += availableFlow;
                currT = currS;
            }

            totalFlow += availableFlow;

            if (report) {
                System.out.printf("Flow %d: ", availableFlow);
                for (int i : currPath) {
                    System.out.printf("%d ", i);
                }
                System.out.println();
            }
        }

        if (report) {
            for (int i=0; i < holderGraph.length; i++) {
                for (int j=0; j < holderGraph.length; j++) {
                    if (holderGraph[i][j] > 0) {
                        System.out.printf("Edge(%d, %d) transports %d items\n", i, j, holderGraph[i][j]);
                    }
                }
            }
        }

        return totalFlow;
    }

    /**k
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        Queue<Integer> queue = new LinkedList<>();
        for (GraphNode x : this.vertices) {
            x.parent = -1;
            x.visited = false;
        }
        queue.add(s);
        this.vertices[s].visited = true;

        while (!queue.isEmpty() && this.vertices[t].parent == -1) {
            int v = queue.remove();

            for (GraphNode.EdgeInfo w : this.vertices[v].successor) {
                if (this.rGraph[v][w.to] > 0 && !this.vertices[w.to].visited && this.vertices[s] != this.vertices[w.to]) {
                    this.vertices[w.to].visited = true;
                    this.vertices[w.to].parent = v;
                    queue.add(w.to);
                }
            }
        }
        return this.vertices[t].parent != -1;
    }

    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {
        // TODO:
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("The Graph ").append(name).append(" \n");
        for (var vertex : vertices) {
            sb.append((vertex.toString()));
        }
        return sb.toString();
    }

    public void fillResidualGraph() {
        for (int i=0; i < this.rGraph.length; i++) {
            for (int j=0; j < this.vertices[i].successor.size(); j++) {
                this.rGraph[i][this.vertices[i].successor.get(j).to] = this.vertices[i].successor.get(j).capacity;
            }
        }
    }

    public void displayResidualGraph() {
        for (int[] ints : this.rGraph) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println();
        }
    }
}
