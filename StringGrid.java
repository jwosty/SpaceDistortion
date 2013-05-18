package jw.spacedistortion;

public class StringGrid {
	private String[] strings;
	public int width;
	
	public StringGrid(String... strings) {
		this.strings = strings;
		this.width = this.findWidth();
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
}
