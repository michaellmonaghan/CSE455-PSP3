
import java.io.*;


class Loc {
	public static void main(String[] args) {
		InputStream is = null;
		try {
			if (args.length != 0) {
				is = new FileInputStream(args[0]);
				System.out.println("using file " + args[0] + " " + (is.available()/1000f) + " kB");
			} else { 
				is = System.in;
				System.out.println("using stdin");
			}
			System.out.println(loc(is) + " lines of code");
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
	
	public static int loc(InputStream is) throws IOException {
		char b;
		char p = ' ';
		int loc=0;
		int r = 0;
		SearchCase c = SearchCase.EMPTY;
		while ((r = is.read()) != -1) {
			b = (char) r;
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
			loc++;
		return loc;
	}
	
	private static boolean emptySpace(char b) {
		return b == '\t';
	}
	
}
