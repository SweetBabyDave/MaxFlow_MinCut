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
        vertices[source].addEdge(source, destination, capacity);

        return true;
    }

    /**
     * Algorithm to find max-flow in a network
     */
    public int findMaxFlow(int s, int t, boolean report) {
        // TODO:
        fillResidualGraph();
        while (hasAugmentingPath(s, t)) {
            System.out.println("First");
            break;
        }
        return 0;
    }

    /**
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        boolean[] visited = new boolean[this.rGraph.length];
        Queue<Integer> queue = new LinkedList<>();
        int[] parent = new int[this.rGraph.length];
        Arrays.fill(parent, -1);
        queue.add(s);
        visited[s] = true;

        while (!queue.isEmpty() && parent[t] == -1) {
            int v = queue.remove();

            for (int w=0; w < this.rGraph.length; w++) {
                if (this.rGraph[v][w] > 0 && !visited[w]) {
                    queue.add(w);
                    parent[w] = v;
                    visited[w] = true;
                }
            }
        }
        return visited[t];
    }

    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {
        // TODO:
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("The Graph " + name + " \n");
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
        for (int i=0; i < this.rGraph.length; i++) {
            for (int j=0; j < this.rGraph[i].length; j++) {
                System.out.print(this.rGraph[i][j] + " ");
            }
            System.out.println();
        }
    }
}
