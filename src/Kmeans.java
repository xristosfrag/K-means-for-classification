import java.util.Scanner;
import java.util.Random;
import java.lang.Math;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;

// ---------------------------------------------------------------
// ----------------CLUSTER----------------------------------------
// ---------------------------------------------------------------
class Cluster {

    public ArrayList<Point> points;
    public Point centroid;
    public int id;

    public Cluster(int id) {
        this.id = id;
        points = new ArrayList<Point>();
        centroid = null;
    }

    public double computeSSE() {
        double clusterSSE = 0;

        for (Point p : points) {
            clusterSSE += Point.distance(p, centroid);
        }
        return clusterSSE;

    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        points.clear();
    }

    public void printCluster() {
        System.out.println("Cluster: " + id);
        System.out.println("Centroid: " + centroid);
        System.out.println("Number of points at this cluster: " + points.size());

        // System.out.println("Points: \n{");
        // for (Point p : points) {
        // System.out.println(p);
        // }
        // System.out.println("}");
    }
}

// ---------------------------------------------------------------
// ----------------POINT----------------------------------------
// ---------------------------------------------------------------
class Point {

    private double x = 0;
    private double y = 0;
    private int cluster_number = 0;

    public Point(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }

    public int getCluster() {
        return this.cluster_number;
    }

    protected static double distance(Point p, Point centroid) {
        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    protected static ArrayList<Point> loadPoints(String file) {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("File " + file + " was not found\nor could not be opened\nSystem Exits...");
            System.exit(0);
        }
        String line = "";

        ArrayList<Point> points = new ArrayList<Point>();

        System.out.println("Reading from file");
        while (inputStream.hasNextLine()) {
            line = inputStream.nextLine();
            String[] point = line.split(",");
            float x1 = Float.parseFloat(point[0]);
            float x2 = Float.parseFloat(point[1]);
            points.add(new Point(x1, x2));
        }
        return points;
    }

    protected static void producePoints(int max, int min, int numPoints, String file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("Error in opening the file points.txt");
            System.exit(0);
        }
        PrintWriter outputWriter = new PrintWriter(outputStream);

        // System.out.println("Writing to file.");
        Random random = new Random();
        for (int i = 0; i < numPoints; i++) {

            float x1 = min + random.nextFloat() * (max - min);
            float x2 = min + random.nextFloat() * (max - min);

            outputWriter.println(x1 + "," + x2);
            outputWriter.flush();
        }
    }
}

// ---------------------------------------------------------------
// ----------------K-MEANS----------------------------------------
// ---------------------------------------------------------------
public class Kmeans {

    private static int NUM_CLUSTERS = 3;
    private int NUM_POINTS = 4000;
    private static final int MIN_COORDINATE = -1;
    private static final int MAX_COORDINATE = 1;
    private static Scanner scanner;

    private ArrayList<Point> points;
    private static ArrayList<Cluster> clusters;

    /*
     * Constructor
     */
    public Kmeans() {
        points = new ArrayList<Point>();
        clusters = new ArrayList<Cluster>();
    }

    /*
     * Initialises the process
     */
    public void init() {
        // check if dir '../data/' does not exist and create it
        File dir = new File("../data/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        String file = "../data/points.txt";

        // Create Points and writes them in the file
        Point.producePoints(MAX_COORDINATE, MIN_COORDINATE, NUM_POINTS, file);

        // Load Points
        points = Point.loadPoints(file);

        // Create Clusters
        // Set Random Centroids
        scanner = new Scanner(System.in);

        System.out.print(
                "Enter the number of clusters(bigger than 0) you want the program to apply to your data.\nEnter '-1' in order to stop execution and produce SSE-#clusters file: ");
        NUM_CLUSTERS = scanner.nextInt();

        if (NUM_CLUSTERS == -1) {
            return;
        }

        while (NUM_CLUSTERS < 1 && NUM_CLUSTERS != -1) {
            System.out.print(
                    "Enter the number of clusters(bigger than 0) you want the program to apply to your data.\nEnter '-1' in order to stop execution and produce SSE-#clusters file: ");
            NUM_CLUSTERS = scanner.nextInt();
        }
        if (NUM_CLUSTERS == -1) {
            return;
        }
        System.out.println("");

        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster cluster = new Cluster(i);
            Point randomPoint = points.get((int) (Math.random() * (NUM_POINTS - 0)) + 0);
            Point centroid = randomPoint; // ana8esh gia centroid tou cluster i, enos tyxaiou point
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }

        // Print Initial state
        System.out.println("Initial State:");
        printCurrentState();
    }

    private void printCurrentState() {
        System.out.println("==============================================================");
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Cluster c = clusters.get(i);
            c.printCluster();
        }
        System.out.println("==============================================================\n");
    }

    /*
     * The process to calculate the K Means, with iterating method. Steps are as
     * follows: 1. Calculate Euclidean Distance 2. Set Point to Cluster 3. Set
     * Centroid to Cluster 4. Calculate SSE
     */
    public void assignPointsToClusters() {
        double max_value = Double.MAX_VALUE; // biggest value for double number

        int cluster_id = 0;
        for (Point point : points) // for each point
        {
            Double distance = 0.0;
            double min_value = max_value; // for each distance smaller than min, the point will be assigned to cluster

            for (int i = 0; i < NUM_CLUSTERS; i++) // gia ka8e cluster
            {
                Cluster cluster = clusters.get(i);
                // calculate the distance between the point and the centroid of the cluster
                distance = Point.distance(point, cluster.getCentroid());

                // belongs to cluster i
                if (distance < min_value) {
                    min_value = distance;
                    cluster_id = i;
                }
            }
            point.setCluster(cluster_id);
            clusters.get(cluster_id).addPoint(point);
        }
    }

    /*
     * calculates the sum of squared errors of the clusters
     */
    private double computeSSE() {
        double SSE = 0;

        for (Cluster cluster : clusters) {
            SSE += cluster.computeSSE();
        }
        return SSE;
    }

    /*
     * calculates the new centroids of the clusters based on the points that belong
     */
    public void rellocateCentroids() {
        for (Cluster cluster : clusters) {

            double X1 = 0;
            double X2 = 0;
            ArrayList<Point> pointsInCluster = cluster.getPoints();

            for (Point point : pointsInCluster) {
                X1 += point.getX();
                X2 += point.getY();
            }

            int clusterSize = pointsInCluster.size();
            // an den einai adeio to cluster. Giati mporei na mh tou anate8oun points
            if (clusterSize > 0) {
                X1 = X1 / clusterSize;
                X2 = X2 / clusterSize;

                cluster.centroid = new Point(X1, X2);
            }
        }
    }

    /*
     * returns the centroids of the clusters
     */
    public ArrayList<Point> getCentroids() {
        ArrayList<Point> centroids = new ArrayList<Point>();
        for (Cluster cluster : clusters) {
            centroids.add(cluster.getCentroid());
        }
        return centroids;
    }

    public double run() {
        // int iterations = 0;
        double distance = 0;
        boolean done = false;
        double SSE = 0;

        while (!done) {
            // iterations++;
            // System.out.println("iterations: " + iterations);

            for (Cluster cluster : clusters) {
                cluster.clear();
            }

            ArrayList<Point> centroidsBefore = getCentroids();

            assignPointsToClusters();
            rellocateCentroids();
            SSE = computeSSE();

            // 8a stamathsei otan ta centroids den allazoun pleon 8esh
            distance = 0;
            ArrayList<Point> centroidsNow = getCentroids();
            for (int k = 0; k < centroidsBefore.size(); k++) {
                distance += Point.distance(centroidsBefore.get(k), centroidsNow.get(k));
            }

            if (distance < 0.1) {
                done = true;
            }
        }
        System.out.println("Current distance: " + SSE);
        return SSE;
    }

    /*
     * prints the clusters in files
     */
    public static void logClusters(int M) {
        FileOutputStream outputStream = null;

        // System.out.println("Writing to files.");
        int counter = 0;
        for (Cluster cluster : clusters) {

            try {
                outputStream = new FileOutputStream("../output/Clusters" + M + "_" + counter + ".txt");
            } catch (FileNotFoundException e) {
                System.out.println("Error in opening the file points.txt");
                System.exit(0);
            }
            PrintWriter outputWriter = new PrintWriter(outputStream);

            ArrayList<Point> pts = cluster.getPoints();

            int counterL = 0;
            int l = pts.size();
            outputWriter.println("X                         Y");
            outputWriter.flush();
            for (Point p : pts) {
                counterL++;
                if (counterL == l) {
                    outputWriter.print(p.getX() + "     " + p.getY());
                } else {
                    outputWriter.println(p.getX() + "       " + p.getY());
                }
            }
            outputWriter.flush();
            counter++;
        }
    }

    /*
     * prints the centroids in files
     */
    public static void produceLogFile(int i, ArrayList<Integer> numClusters, ArrayList<Double> distances) {
        FileOutputStream outputStream = null;

        try {
            if (i == -1) {
                outputStream = new FileOutputStream("../output/curve.txt");
            } else {
                outputStream = new FileOutputStream("../output/logFileCentroids" + i);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error in opening the file ../output/points.txt");
            System.exit(0);
        }
        if (i >= 0) {
            // save points in clusters
            // System.out.println("Writing to files.");
            PrintWriter outputWriter = new PrintWriter(outputStream);

            outputWriter.println("X                         Y");
            outputWriter.flush();
            for (Cluster cluster : clusters) {

                Point centroid = cluster.getCentroid();
                outputWriter.println(centroid.getX() + "        " + centroid.getY());
                outputWriter.flush();
            }
        } else {
            // System.out.println("Writing to files.");
            PrintWriter outputWriter = new PrintWriter(outputStream);

            outputWriter.println("X                         Y");
            outputWriter.flush();
            for (int j = 0; j < numClusters.size(); j++) {
                outputWriter.println(numClusters.get(j) + "       " + distances.get(j));
                outputWriter.flush();
            }
        }

    }

    public static void main(String[] args) {

        ArrayList<Integer> numClusters = new ArrayList<Integer>();
        ArrayList<Double> SSEs = new ArrayList<Double>();
        int counter = 0;

        Kmeans kmeans = new Kmeans();
        kmeans.init();
        numClusters.add(NUM_CLUSTERS);

        while (NUM_CLUSTERS != -1)

        {
            if (counter > 0) // edw 8a mpei apo th defterh fora kai meta
            {
                kmeans = new Kmeans();
                kmeans.init();
                if (NUM_CLUSTERS == -1) {
                    break;
                }
                numClusters.add(NUM_CLUSTERS);
            }

            double bestDist = Double.MAX_VALUE;

            for (int j = 0; j < 20; j++)

            {
                Double distance = kmeans.run();
                if (bestDist > distance) {
                    bestDist = distance;
                }
            }
            SSEs.add(bestDist);

            System.out.println("\nFinal State");
            kmeans.printCurrentState();
            System.out.println("===========================================================");
            System.out.println("System finished clustering after " + "with SSE " + bestDist);
            System.out.println("===========================================================");
            // create directory './output' if it doesn't exist
            File directory = new File("../output/");
            if (!directory.exists()) {
                directory.mkdir();
            }
            produceLogFile(numClusters.get(counter), numClusters, SSEs);
            logClusters(numClusters.get(counter));

            counter++;
        }
        for (int i = 0; i < SSEs.size(); i++) {
            System.out.println("For " + numClusters.get(i) + " cluster -> SSE: " + SSEs.get(i));
        }
        if (counter > 0)
            produceLogFile(-1, numClusters, SSEs);
        scanner.close();
    }
}