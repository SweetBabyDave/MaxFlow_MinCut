import java.lang.reflect.Array;
import java.util.*;

public class Graph {
    private final GraphNode[] vertices;  // Adjacency list for graph.
    private final String name;  //The file from which the graph was created.
    private final int[][] rGraph;
    private final int[][] rGraphFlow;

    public Graph(String name, int vertexCount) {
        this.name = name;

        vertices = new GraphNode[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices[vertex] = new GraphNode(vertex);
        }

        this.rGraph = new int[vertexCount][vertexCount];
        this.rGraphFlow = new int[vertexCount][vertexCount];
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
                    this.rGraphFlow[currS][currT] += availableFlow;
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
        System.out.println();
        if (report) {
            for (int i=0; i < this.rGraphFlow.length; i++) {
                for (int j=0; j < this.rGraphFlow.length; j++) {
                    if (this.rGraphFlow[i][j] > 0) {
                        System.out.printf("Edge(%d, %d) transports %d items\n", i, j, this.rGraphFlow[i][j]);
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
        ArrayList<Integer> setR = new ArrayList<>();
        ArrayList<Integer> realSet = new ArrayList<>();
        int currS;

        for (GraphNode x : this.vertices) {
            x.visited = false;
        }

        // This while loop finds the set of all of the vertices that can be accessed from the source.
        setR.add(s);
        while (!setR.isEmpty()) {
            currS = setR.remove(0);
            realSet.add(currS);

            if (!this.vertices[currS].visited) {
                for (int i = 0; i < this.rGraph.length; i++) {
                    if (this.rGraph[currS][i] != 0) {
                        if (!realSet.contains(i)) {
                            setR.add(i);
                            this.vertices[currS].visited = true;
                        }
                    }
                }
            }
        }

        // This then finds out which vertices were not in the set and finds the paths to cut off.
        System.out.println();
        System.out.printf("-- Min Cut: %s --\n", this.name);
        for (int i=0; i < this.vertices.length; i++) {
            if (!realSet.contains(i)) {
                for (var r : this.vertices[i].successor) {
                    if (realSet.contains(r.to) && this.rGraphFlow[r.to][r.from] != 0) {
                        System.out.printf("Min Cut Edge: (%d, %d) : %d\n", r.to, r.from, this.rGraphFlow[r.to][r.from]);
                    }
                }
            }
        }
        System.out.println();
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
