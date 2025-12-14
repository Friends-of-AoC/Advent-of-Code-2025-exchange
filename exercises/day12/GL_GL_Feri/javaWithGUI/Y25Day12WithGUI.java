import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * see: https://adventofcode.com/2025/day/12
 */
public class Y25Day12WithGUI {
	
	static Y25GUIOutput12 output;

	public static record InputData(int id, List<String> shape, int treeWidth, int treeHeight, List<Integer> idCounts) {
		public boolean isPresent() {
			return shape != null;
		}
	}

	// bit masks for neighbours
	// 
	// 876
	// 543
	// 210
	//
	static int[][] CORNER_MASK = {
		{0b000011011, 0b000111111, 0b000110110},
		{0b011011011, 0b111111111, 0b110110110},
		{0b011011000, 0b111111000, 0b110110000}
	};
	
	private static final int CM_TOP = 0;
	private static final int CM_CENTER = 1;
	private static final int CM_BOTTOM = 2;
	private static final int CM_LEFT = 0;
	private static final int CM_RIGHT = 0;

	private static final String INPUT_PRESENT_TITLE_RX = "^([0-9]+)[:]$";
	private static final String INPUT_PRESENT_SHAPE_RX = "^([#.]+)$";
	private static final String INPUT_TREE_RX = "^([0-9]+)x([0-9]+)[:] ([0-9 ]+)$";
	
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
			if (line.matches(INPUT_PRESENT_TITLE_RX)) {
				Integer id = Integer.parseInt(line.replaceFirst(INPUT_PRESENT_TITLE_RX, "$1"));
				List<String> shape = new ArrayList<>();
				line = scanner.nextLine().trim();
				while (!line.isEmpty()) {
					if (line.matches(INPUT_PRESENT_SHAPE_RX)) {
						shape.add(line);
					}
					line = scanner.nextLine().trim();
				}
				return new InputData(id, shape, 0, 0, null);
			}
			else if (line.matches(INPUT_TREE_RX)) {
				int treeWidth = Integer.parseInt(line.replaceFirst(INPUT_TREE_RX, "$1"));
				int treeheight = Integer.parseInt(line.replaceFirst(INPUT_TREE_RX, "$2"));
				String[] idStrList = line.replaceFirst(INPUT_TREE_RX, "$3").trim().split(" ");
				List<Integer> idCounts = new ArrayList<>();
				for (String idStr:idStrList) {
					idCounts.add(Integer.parseInt(idStr));
				}
				return new InputData(0, null, treeWidth, treeheight, idCounts);
			}
			else {
				throw new RuntimeException("invalid line '"+line+"'");
			}
		}
	}
	
	public static record Pos(int x, int y) {
		@Override
		public String toString() {
			return "("+x+","+y+")";
		}
		public Pos move(Pos delta) {
			return new Pos(x + delta.x, y + delta.y);
		}
		public Pos move(int dx, int dy) {
			return new Pos(x + dx, y + dy);
		}
		public Set<Pos> directNeighbours() {
			Set<Pos> neighbours = new HashSet<>();
			neighbours.add(move(0,-1));
			neighbours.add(move(-1,0));
			neighbours.add(move(1,0));
			neighbours.add(move(0,1));
			return neighbours;
		}
		public Set<Pos> neighbours() {
			Set<Pos> result = new HashSet<>();
			result.add(move(-1,-1));
			result.add(move( 0,-1));
			result.add(move( 1,-1));
			result.add(move(-1, 0));
			result.add(move( 1, 0));
			result.add(move(-1, 1));
			result.add(move( 0, 1));
			result.add(move( 1, 1));
			return result;
		}
	}
	
	public static class Shape {
		int id;
		List<Set<Pos>> points;
		List<Set<Pos>> touchPoints;
		List<Pos> offsets;
		List<Integer> bitCodes;
		List<int[][]> gridMasks;
		int size;
		int numDots;
		public Shape(int id, List<String> shapeLines) {
			this.id = id;
			points = new ArrayList<>();
			touchPoints = new ArrayList<>();
			offsets = new ArrayList<>();
			bitCodes = new ArrayList<>();
			gridMasks = new ArrayList<>();
			size = shapeLines.size();
			Set<Pos> baseShape = new HashSet<>();
			Set<Pos> flipShape = new HashSet<>();
			numDots = 0;
			for (int y=0; y<size; y++) {
				for (int x=0; x<size; x++) {
					if (shapeLines.get(y).charAt(x) == '#') {
						baseShape.add(new Pos(x, y));
						flipShape.add(new Pos(size - x - 1, y));
						numDots++;
					}
				}
			}
			Set<Pos> r90Shape = rot90(baseShape);
			Set<Pos> r180Shape = rot90(r90Shape);
			Set<Pos> r270Shape = rot90(r180Shape);
			Set<Pos> r90FlipShape = rot90(flipShape);
			Set<Pos> r180FlipShape = rot90(r90FlipShape);
			Set<Pos> r270FlipShape = rot90(r180FlipShape);
			addNewShape(baseShape);
			addNewShape(flipShape);
			addNewShape(r90Shape);
			addNewShape(r90FlipShape);
			addNewShape(r180Shape);
			addNewShape(r180FlipShape);
			addNewShape(r270Shape);
			addNewShape(r270FlipShape);
			calcTouchPoints();
			calcBitCodes();
		}
		
		static Pos[] BITCODE_POS = {
				new Pos(0,0), new Pos(1,0), new Pos(2,0),
				new Pos(0,1), new Pos(1,1), new Pos(2,1),
				new Pos(0,2), new Pos(1,2), new Pos(2,2),
		};
		
		
		private void calcBitCodes() {
			for (Set<Pos> shapePoints:points) {
				int code = 0;
				int bit = 1 << 8;
				for (Pos p:BITCODE_POS) {
					if (shapePoints.contains(p)) {
						code |= bit;
					}
					bit >>= 1;
				}
				bitCodes.add(code);
				int[][] gridMask = new int[2*size-1][2*size-1];
				for (int y=-size+1; y<=size-1; y++) {
					for (int x=-size+1; x<=size-1; x++) {
						Pos delta = new Pos(x, y);
						code = 0;
						bit = 1 << 8;
						for (Pos p:BITCODE_POS) {
							Pos dp = p.move(delta);
							if (shapePoints.contains(dp)) {
								code |= bit;
							}
							bit >>= 1;
						}
						gridMask[y+size-1][x+size-1] = code ^ 0x1FF;
//						showBitCode("GM("+x+","+y+")", getGridMask(x,y));
					}
				}
				gridMasks.add(gridMask);
			}
		}
		
		private int getGridMask(int state, int x, int y) {
			return gridMasks.get(state)[y + size - 1][x + size - 1];
		}

		private void showBitCode(String text, int bitCode) {
			StringBuilder result = new StringBuilder();
			result.append(text).append(": \n");
			for (int i=0; i<9; i++) {
				if ((bitCode & (1 << i)) != 0) {
					result.append('#');
				} else {
					result.append('.');
				}
				if (i % 3 == 2) {
					result.append('\n');
				}
			}
			System.out.println(result.toString());
		}

		public void calcTouchPoints() {
			for (Set<Pos> shapePoints:points) {
				Set<Pos> shapeTouchPoints = new HashSet<>();
				for (Pos p:shapePoints) {
					shapeTouchPoints.addAll(p.directNeighbours());
				}
				shapeTouchPoints.removeAll(shapePoints);
				touchPoints.add(shapeTouchPoints);
			}
		}
		
		public int getNumStates() {
			return points.size();
		}
		
		@Override public String toString() {
			return "Shape["+id+": "+points+"]";
		}
		
		public Set<Pos> rot90(Set<Pos> shape) {
			Set<Pos> newShape = new HashSet<>();
			for (Pos p:shape) {
				newShape.add(new Pos(size - p.y - 1, p.x));
			}
			return newShape;
		}
		
		public void addNewShape(Set<Pos> newShape) {
			for (Set<Pos> existingShape:points) {
				if (existingShape.equals(newShape)) {
					return;
				}
			}
			points.add(newShape);
			Pos newOffset = null;
			for (int x=0; x<3; x++) {
				if (newShape.contains(new Pos(x,0))) {
					newOffset = new Pos(-x,0);
					break;
				}
			}
			if (newOffset == null) {
				throw new RuntimeException("first row point not found");
			}
			offsets.add(newOffset);
//			print(newShape, newOffset);
		}
		
		public void print(Set<Pos> shapePoints, Pos offset) {
			StringBuilder result = new StringBuilder();
			for (int y=0; y<size; y++) {
				for (int x=0; x<size; x++) {
					if (shapePoints.contains(new Pos(x, y))) {
						if (offset.x == -x && offset.y == -y) {
							result.append('X');
						} else {
							result.append('#');
						}
					} else {
						result.append('.');
					}
				}
				result.append('\n');
			}
			System.out.println(result.toString());
		}
		
		public Set<Pos> getPositions(int state, Pos pos) {
			Set<Pos> shapePoints = points.get(state);
			Pos shapeOffset = offsets.get(state);
			pos = pos.move(shapeOffset);
			Set<Pos> result = new HashSet<>();
			for (Pos p:shapePoints) {
				result.add(pos.move(p));
			}
			return result;
		}
		
		public Set<Pos> getOuterPositions(int state, Pos pos) {
			Set<Pos> outerPoints = touchPoints.get(state);
			Pos shapeOffset = offsets.get(state);
			pos = pos.move(shapeOffset);
			Set<Pos> result = new HashSet<>();
			for (Pos p:outerPoints) {
				result.add(pos.move(p));
			}
			return result;
		}
		
		public boolean matchesBitCode(int bc) {
			for (int bitCode:bitCodes) {
				if ((bitCode & bc) == bitCode) {
					return true;
				}
			}
			return false;
		}
		public boolean matchesBitCode(int bc, int state) {
			int bitCode = bitCodes.get(state);
			if ((bitCode & bc) == bitCode) {
				return true;
			}
			return false;
		}
	}
	
	public static class TreeSpace {
		Set<Pos> occupied;
		int width;
		int height;
		
	}
	
	
	public static record PlacedShape(int shapeId, int state, int x, int y, double measure) {}
	
	public static class World {
		Map<Integer, Shape> shapes;
		int[] shapeIDCounts;
		int[][] grid;
		int width;
		int height;
		int[][] bcgrid;
		int[] bitCodeWeights;
		public World() {
			shapes = new HashMap<>();
		}
		public int get(int x, int y) {
			if (x < 0 || x >= width || y < 0 || y >= height) {
				return 1;
			}
			return grid[y][x];
		}
		public void initBCGrid() {
			bcgrid = new int[height][width];
			for (int y=1; y<height-1; y++) {
				for (int x=1; x<width-1; x++) {
					bcgrid[y][x] = CORNER_MASK[CM_CENTER][CM_CENTER];
				}
			}
			for (int y=1; y<height-1; y++) {
				bcgrid[y][0] = CORNER_MASK[CM_CENTER][CM_LEFT];
				bcgrid[y][width-1] = CORNER_MASK[CM_CENTER][CM_RIGHT];
			}
			for (int x=1; x<width-1; x++) {
				bcgrid[0][x] = CORNER_MASK[CM_TOP][CM_CENTER];
				bcgrid[height-1][x] = CORNER_MASK[CM_BOTTOM][CM_CENTER];
			}
			bcgrid[0][0] = CORNER_MASK[CM_TOP][CM_LEFT];
			bcgrid[height-1][0] = CORNER_MASK[CM_BOTTOM][CM_LEFT];
			bcgrid[height-1][width-1] = CORNER_MASK[CM_BOTTOM][CM_RIGHT];
			bcgrid[0][width-1] = CORNER_MASK[CM_TOP][CM_RIGHT];
		}
		public void calcBCGrid() {
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int bc = 0;
					int bit = 1;
					for (int dy=-1; dy<=1; dy++) {
						for (int dx=-1; dx<=1; dx++) {
							if (get(x+dx, y+dy) != 0) {
								bc |= bit;
							}
							bit <<= 1;
						}
					}
					bcgrid[y][x] = bc;
				}
			}
		}
		public void addShape(Shape shape) {
			shapes.put(shape.id, shape);
		}
		@Override
		public String toString() {
			return shapes.toString();
		}
		public boolean solve(int width, int height, List<Integer> shapeIds) {
			prepare(width, height, shapeIds);
			while (sum(shapeIDCounts) > 0) {
				PlacedShape bestPS = null;
				for (int shapeId=0; shapeId<shapeIDCounts.length; shapeId++) {
					if (shapeIDCounts[shapeId] == 0) {
						continue;
					}
					PlacedShape ps = place(shapeId);
					if (ps == null) {
						continue;
					}
					if (bestPS == null 
							|| ps.measure < bestPS.measure 
							|| (ps.measure == bestPS.measure && shapeIDCounts[ps.shapeId] > shapeIDCounts[bestPS.shapeId])) {
						bestPS = ps;
					}
				}
				if (bestPS == null) {
					return false;
				}
				applyPlacedShape(bestPS);
				showBCGrid("shape "+bestPS.shapeId+" at ("+bestPS.x+","+bestPS.y+") measure="+bestPS.measure);
			}
			return true;
		}
		private void applyPlacedShape(PlacedShape ps) {
			shapeIDCounts[ps.shapeId]--;
			Shape shape = shapes.get(ps.shapeId);
			for (int dy = -2; dy <= 2; dy++) {
				for (int dx = -2; dx <= 2; dx++) {
					int gx = ps.x + dx;
					int gy = ps.y + dy;
					if (gx < 0 || gx >= width || gy < 0 || gy >= height) {
						continue;
					}
					bcgrid[gy][gx] &= shape.getGridMask(ps.state, dx, dy);
				}
			}
		}
		public void calcBitCodeWeights() {
			bitCodeWeights = new int[512];
			for (int bc=0; bc<512; bc++) {
				int weight = 0;
				for (int shapeId:shapes.keySet()) {
					Shape shape = shapes.get(shapeId);
					if (shape.matchesBitCode(bc)) {
						weight += shapeIDCounts[shapeId];
					}
				}
				bitCodeWeights[bc] = weight;
			}
		}
		private PlacedShape place(int shapeId) {
			PlacedShape result = null;
			Shape shape = shapes.get(shapeId);
			for (int y=1; y<height-1; y++) {
				for (int x=1; x<width-1; x++) {
					for (int state=0; state<shape.getNumStates(); state++) {
						if (shape.matchesBitCode(bcgrid[y][x], state)) {
							PlacedShape ps = calcMeasure(shape, state, x, y);
							if (result == null || ps.measure < result.measure) {
								result = ps;
							}
						}
					}
				}
			}
			return result;
		}
	
		private PlacedShape calcMeasure(Y25Day12WithGUI.Shape shape, int state, int x, int y) {
			int measure = 0;
			for (int dy = -2; dy <= 2; dy++) {
				for (int dx = -2; dx <= 2; dx++) {
					int gx = x + dx;
					int gy = y + dy;
					if (gx < 0 || gx >= width || gy < 0 || gy >= height) {
						continue;
					}
					int bc = bcgrid[gy][gx];
					int bcNew = bc & shape.getGridMask(state, dx, dy);
					measure += bitCodeWeights[bc] - bitCodeWeights[bcNew];
				}
			}
			return new PlacedShape(shape.id, state, x, y, measure);
		}
		private int sum(int[] arr) {
			int result = 0;
			for (int v:arr) {
				result += v;
			}
			return result;
		}
		private void showGrid() {
			StringBuilder result = new StringBuilder();
			result.append("---------------------\n");
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					if (get(x,y)==1) {
						result.append('#');
					} else {
						result.append('.');
					}
				}
				result.append('\n');
			}
			System.out.println(result.toString());
		}
		private void showBCGrid(String title) {
			StringBuilder result = new StringBuilder();
			result.append(title).append("\n");
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					if ((bcgrid[y][x] & 0b000010000)==0) {
						result.append('#');
					} else {
						result.append('.');
					}
				}
				result.append('\n');
			}
			output.addStep(result.toString());
		}
		
		private void prepare(int width, int height, List<Integer> shapeIds) {
			this.width = width;
			this.height = height;
			this.shapeIDCounts = new int[shapeIds.size()];
			for (int i=0; i<shapeIds.size(); i++) {
				this.shapeIDCounts[i] = shapeIds.get(i);
			}
			calcBitCodeWeights();
			grid = new int[height][width];
			initBCGrid();
		}

	}
	
	public static void mainPart1(String inputFile) throws FileNotFoundException {
		output = new Y25GUIOutput12("2025 Day 07 Part 1", true);
		World world = new World();
		int solved = 0;
		int failed = 0;
		for (InputData data:new InputProcessor(inputFile)) {
			System.out.println(data);
			if (data.isPresent()) {
				Shape shape = new Shape(data.id, data.shape());
				world.addShape(shape);
			}
			else {
				if (world.solve(data.treeWidth(), data.treeHeight(), data.idCounts())) {
					solved++;
					System.out.println("Solution found for "+data.treeWidth()+"x"+data.treeHeight()+": "+data.idCounts());
				}
				else {
					failed++;
					System.out.println("FAILED "+data.treeWidth()+"x"+data.treeHeight()+": "+data.idCounts());
				}
			}
		}
		System.out.println("solved: "+solved+", failed: "+failed);
	}


	public static void mainPart2(String inputFile) {
	}

	

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("--- PART I ---");
		mainPart1("exercises/day12/Feri/input-example.txt");
//		mainPart1("exercises/day12/Feri/input.txt");            // < 1000  
		System.out.println("---------------");
//		System.out.println("--- PART II ---");
		mainPart2("exercises/day12/Feri/input-example.txt");
//		mainPart2("exercises/day12/Feri/input.txt");   
//		System.out.println("---------------");    // 
	}
	
}
