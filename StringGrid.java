package jw.spacedistortion;

public class StringGrid {
	private String[] strings;
	public int width;
	public int height;
	
	public StringGrid(String... strings) {
		this.strings = strings;
		this.width = this.findWidth();
		this.height = strings.length;
	}
	
	private int findWidth() {
		int w = 0;
		for (int i = 0; i < strings.length; i++) {
			int rowW = strings[i].length();
			if (w < rowW) {
				w = rowW;
			}
		}
		return w;
	}

	// Retrieves the character at the given coordinates
	public char get(int x, int y) {
		return strings[y].charAt(x);
	}
}