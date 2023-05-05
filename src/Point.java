
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class Point {

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
		return Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2);
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}

	protected static ArrayList<Point> loadPoints(String file) {
		Scanner inputStream = null;
		try {
			inputStream = new Scanner(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.out.println("File " + file + " was not found\nor could not be opened");
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

			System.out.print(x1 + ", " + x2 + " \n");

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

		System.out.println("Writing to file.");
		Random random = new Random();
		for (int i = 0; i < numPoints; i++) {
			float x1 = min + random.nextFloat() * (max - min);
			// random.nextInt(max - min) + min;
			float x2 = min + random.nextFloat() * (max - min);
			// random.nextInt(max - min) + min;
			outputWriter.println(x1 + "," + x2);
			outputWriter.flush();
		}
	}
}