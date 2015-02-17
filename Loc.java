import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class Loc {
	public static void main(String[] args) {
		InputStream is = null;
		try {
			if (args.length != 0) {
				is = new FileInputStream(args[0]);
				System.out.println("using file " + args[0] + " "
						+ (is.available() / 1000f) + " kB");
			} else {
				is = System.in;
				System.out.println("using stdin");
			}
			loc(is).printLOC();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private enum SearchCase {
		EMPTY, NEXT_LINE, BLOCK_COMMENT
	}

	public static ClassLOC loc(InputStream is) throws IOException {
		String cl = "class" + " ";
		int i = 0;
		char b = ' ';
		int r;
		while ((r = is.read()) != -1) {
			b = (char) r;
			if (b == cl.charAt(i)) {
				if (i < cl.length() - 1) {
					i++;
				} else {
					System.out.println("found class");
					return classloc(is);
				}
			} else {
				i = 0;
			}
		}
		return new ClassLOC();
	}

	public static ClassLOC classloc(InputStream is) throws IOException {
		
		System.out.println("ClassLOC");
		
		ClassLOC cloc = new ClassLOC();
		
		char b;
		char p = ' ';
		int loc=0;
		int r = 0;
		int br = 1;
		SearchCase c = SearchCase.EMPTY;
		
		while ((r = is.read()) != -1 && (char) r != '{') {}
		System.out.println("Found opening bracket");
		
		int i = 0;
		String cl = "class" + " ";
		while ((r = is.read()) != -1) {
			b = (char) r;
			if (b == '{') {
				br++;
			System.out.println("br=" + br);
				if (br == 2) {
					loc = 0;
				}
			} else if (b == '}') {
				br--;
				System.out.println("br=" + br);
				if (br == 1) { 
					MethodLOC m = new MethodLOC();
					m.loc = loc;
					cloc.add(m);
					loc = 0;
				} else if (br == 0) { 
					break;
				}
				
			} 
			if (b == cl.charAt(i)){
				if (i < cl.length() - 1) {
					i++;
				} else {
					/*cloc.add(*///classloc(is)/*)*/;
					System.out.println("found another class");
					i = 0;
				}
			} if (br > 1) {
				i = 0;
				switch (c) {
				case EMPTY:
				if (emptySpace(b)) {
				} else if (b == '/') {
					if(p == '/') {
						c = SearchCase.NEXT_LINE;
					}
				} else if (b == '*' && p == '/') {
					c = SearchCase.BLOCK_COMMENT;
				} else {
					c = SearchCase.NEXT_LINE;
					loc++;
				}
				break;
				
				case NEXT_LINE:
				if (b == '\r')
					c = SearchCase.EMPTY;
				break;
				
				case BLOCK_COMMENT:
				if (p == '*' && b == '/') {
					c = SearchCase.EMPTY;
				}
				break;
				
				default:
				break;
			}
			p = b;
		}
		if (c == SearchCase.NEXT_LINE)
			loc++;}
		return cloc;
	}

	private static boolean emptySpace(char b) {
		return b == '\t' || b == ' ' || b == '\r';
	}

	public static class ClassLOC {
		ClassLOC() {
		}

		String name = "Class";
		private List<ClassLOC> classes = new ArrayList<ClassLOC>();
		private List<MethodLOC> methods = new ArrayList<MethodLOC>();

		public void add(ClassLOC c) {
			classes.add(c);
		}

		public void add(MethodLOC m) {
			methods.add(m);
		}

		public int loc() {
			int loc = 0;
			for (ClassLOC c : classes) {
				loc += c.loc();
			}
			for (MethodLOC m : methods) {
				loc += m.loc;
			}
			return loc;
		}

		public void printLOC() {
			printLOC(0);
		}

		private void printLOC(int level) {
			System.out.println("Class LOC Total " + loc());
			for (MethodLOC m : methods) {
			System.out.println("Method LOC Total " + m.loc);
			}
			//for (ClassLOC c : classes) {
			//	c.printLOC(level + 1);
			//}

		}

	}

	public static class MethodLOC {
		MethodLOC() {
		}

		public int loc;
		public String name = "Method";
	}
}
