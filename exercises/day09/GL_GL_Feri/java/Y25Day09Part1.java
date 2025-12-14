import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/09
 */
public class Y25Day09Part1 {
	
	public static record InputData(int x, int y) {}

	private static final String INPUT_RX = "^([0-9]+),([0-9]+)$";
	
	public static class InputProcessor implements Iterable<InputData>, Iterator<InputData> {
		private Scanner scanner;
		public InputProcessor(String inputFile) {
			try {
				scanner = new Scanner(new File(inputFile));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		@Override public Iterator<InputData> iterator() { return this; }
		@Override public boolean hasNext() { return scanner.hasNext(); }
		@Override public InputData next() {
			String rawLine = scanner.nextLine();
			String line = rawLine.trim();
			while (line.length() == 0) {
				line = scanner.nextLine();
			}
			if (line.matches(INPUT_RX)) {
				int x = Integer.parseInt(line.replaceFirst(INPUT_RX, "$1"));
				int y = Integer.parseInt(line.replaceFirst(INPUT_RX, "$2"));
				return new InputData(x,y);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}
	
	public static record Pos(long x, long y) {
		@Override
		public final String toString() {
			return "("+x+","+y+")";
		}
		public long area(Pos other) {
			long dx = Math.abs(x - other.x);
			long dy = Math.abs(y - other.y);
			return (dx+1)*(dy+1);
		}
		public Pos min(Pos other) {
			if (other == null) {
				return this;
			}
			long x = Math.min(this.x, other.x);
			long y = Math.min(this.y, other.y);
			return new Pos(x, y);
		}
		public Pos max(Pos other) {
			if (other == null) {
				return this;
			}
			long x = Math.max(this.x, other.x);
			long y = Math.max(this.y, other.y);
			return new Pos(x, y);
		}
		public List<Pos> neighbours() {
			List<Pos> result = new ArrayList<>();
			for (long dx=-1; dx<=1; dx++) {
				for (long dy=-1; dy<=1; dy++) {
					if (dx == 0 && dy==0) {
						continue;
					}
					result.add(new Pos(x+dx, y+dy));
				}
			}
			return result;
		}
		public List<Pos> directNeighbours() {
			List<Pos> result = new ArrayList<>();
			result.add(new Pos(x, y-1));
			result.add(new Pos(x+1, y));
			result.add(new Pos(x, y+1));
			result.add(new Pos(x-1, y));
			return result;
		}
	}
	
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		List<Pos> redTiles = new ArrayList<>();
		for (InputData data:new InputProcessor(inputFile)) {
			redTiles.add(new Pos(data.x, data.y));
		}
		long maxArea = -1;
		for (int i=0; i<redTiles.size(); i++) {
			Pos pos1 = redTiles.get(i);
			for (int j=i+1; j<redTiles.size(); j++) {
				Pos pos2 = redTiles.get(j);
				long area = pos1.area(pos2);
				System.out.println("Area between "+pos1+" and "+pos2+" = "+area);
				if (area > maxArea) {
					maxArea = area;
				}
			}
		}
		System.out.println("MaxArea: "+maxArea);
	}


	public static class World {
		Set<Pos> redTiles;
		Set<Pos> greenTiles;
		Set<Pos> fillTiles;
		Pos firstPos = null;
		Pos lastPos = null;
		Pos minPos = null;
		Pos maxPos = null;
		public World() {
			this.redTiles = new HashSet<>();
			this.greenTiles = new HashSet<>();
			fillTiles = new HashSet<>();
			this.firstPos = null;
			this.lastPos = null;
			this.minPos = null;
			this.maxPos = null;
		}
		public void init() {
			System.out.println("Initializing world...");
			addRedTile(firstPos);
			fillGreen();
			System.out.println("INIT finished");
		}
		private void fillGreen() {
			for (Pos neighbour : firstPos.neighbours()) {
				if (tryFillGreen(neighbour)) {
					greenTiles.addAll(fillTiles);
					break;
				};
			};
		}
		private boolean tryFillGreen(Pos fillStartPos) {
			System.out.println("Trying to fill green from "+fillStartPos);
			fillTiles.clear();
			if (!isFree(fillStartPos)) {
				return false;
			}
			fillTiles.add(fillStartPos);
			Set<Pos> currentRow = new HashSet<>();
			currentRow.add(fillStartPos);
			while (!currentRow.isEmpty()) {
				Set<Pos> nextRow = new HashSet<>();
				for (Pos pos : currentRow) {
					for (Pos directNeighbour : pos.directNeighbours()) {
						if (!checkRange(directNeighbour)) {
							fillTiles.clear();
							return false;
						}
						if (isFree(directNeighbour)) {
							fillTiles.add(directNeighbour);
							nextRow.add(directNeighbour);
						}
					}
				}
				currentRow = nextRow;
			}
			return true;
		}
		private boolean checkRange(Pos pos) {
			if (pos.x < minPos.x || pos.x > maxPos.x || pos.y < minPos.y || pos.y > maxPos.y) {
				return false;
			}
			return true;
		}
		private boolean isFree(Pos pos) {
			return !greenTiles.contains(pos) && !redTiles.contains(pos) && !fillTiles.contains(pos);
		}
		public void addRedTile(Pos pos) {
			redTiles.add(pos);
			if (firstPos == null) {
				firstPos = pos;
			}
			minPos = pos.min(minPos);
			maxPos = pos.max(maxPos);
			if (lastPos != null) {
				Pos minP = pos.min(lastPos);
				Pos maxP = pos.max(lastPos);
				if (minP.x == maxP.x) {
					for (long y=minP.y+1; y<=maxP.y-1; y++) {
						greenTiles.add(new Pos(minP.x, y));
					}
				}
				else {
					for (long x=minP.x+1; x<=maxP.x-1; x++) {
						greenTiles.add(new Pos(x, minP.y));
					}
				}
			}
			lastPos = pos;
		}
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (long y=minPos.y; y<=maxPos.y; y++) {
				for (long x=minPos.x; x<=maxPos.x; x++) {
					Pos p = new Pos(x,y);
					if (p.equals(firstPos)) {
						result.append("S");
					}
					else
					if (redTiles.contains(p)) {
						result.append("#");
					}
					else if (greenTiles.contains(p)) {
						result.append("X");
					}
					else {
						result.append(".");
					}
				}
				result.append("\n");
			}
			return result.toString();
		}
		public long findMaxArea() {
			long maxArea = -1;
			List<Pos> redTs = new ArrayList<>(this.redTiles);
			for (int i=0; i<redTs.size(); i++) {
				Pos pos1 = redTs.get(i);
				for (int j=i+1; j<redTs.size(); j++) {
					Pos pos2 = redTs.get(j);
					long area = pos1.area(pos2);
					if (area > maxArea) {
						if (checkFilled(pos1, pos2)) {
							System.out.println("Area between "+pos1+" and "+pos2+" = "+area);
							maxArea = area;
						}
					}
				}
			}
			return maxArea;
		}
		private boolean checkFilled(Pos pos1, Pos pos2) {
			Pos minP = pos1.min(pos2);
			Pos maxP = pos1.max(pos2);
			for (long x=minP.x; x<=maxP.x; x++) {
				for (long y=minP.y; y<=maxP.y; y++) {
					Pos p = new Pos(x,y);
					if (isFree(p)) {
						return false;
					}
				}
			}
			return true;
		}
	}


	public static void mainPart2(String inputFile) {
		World world = new World();
		for (InputData data:new InputProcessor(inputFile)) {
			world.addRedTile(new Pos(data.x, data.y));
		}
		world.init();
//		System.out.println(world.toString());
		long maxArea = world.findMaxArea();
		System.out.println("MaxArea: "+maxArea);
	}

	

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
//		mainPart1("exercises/day09/Feri/input-example.txt");
		mainPart1("exercises/day09/Feri/input.txt");  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
//		mainPart2("exercises/day09/Feri/input-example.txt");
//		mainPart2("exercises/day09/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}
	
}
