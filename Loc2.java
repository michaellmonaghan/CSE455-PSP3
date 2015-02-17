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
			System.out.println();
			objectLOC(is).printLOC();
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

	public static ObjectInfo objectLOC(InputStream is) throws IOException {

		ObjectInfo oi = new ObjectInfo();
		final String CLASS = "class" + " ";
		int i = 0;
		
		char b;
		char p = ' ';
		int r = 0;
		int loc = 0;
		SearchCase c = SearchCase.EMPTY;

		// Find Class names.
		while ((r = is.read()) != -1) {
			b = (char) r;

			if (b == CLASS.charAt(i)) {
				if (i < CLASS.length() - 1) {
					i++;
				} else {
					// found a class
					String name = "";
					while ((r = is.read()) != -1 && (char) r != ' ') {
						name += (char) r;
					}
				oi.add(name);
				}
				
			} else {
				i = 0;
			}

			// Find LOC.
			switch (c) {
			case EMPTY:
			// This handles empty space before the code
				if (b == '\t' || b == ' ' || b == '\r' || b == '\n') {
				} else if (b == '/') {
					if (p == '/') {
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
			// Find name
				
			// Look for a new line
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
			loc++;
		oi.loc = loc;
		return oi;
	}

	public static class ObjectInfo {
		int loc = 0;
		ArrayList<String> objectNames = new ArrayList<String>();

		public ObjectInfo() {
		}

		public ObjectInfo(int loc, ArrayList<String> methods) {
			this.loc = loc;
			this.objectNames = methods;
		}

		public void add(String s) {
			objectNames.add(s);
		}

		public void printLOC() {
			System.out.println("LOC: \t\t" + loc);
			System.out.println("Objects: \t" + objectNames);
			System.out.println("Count: \t\t" + objectNames.size());
		}
	}
}
