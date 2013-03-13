package instantMessenger;

public class Utilities {

	public static String removeChar(String s, char c) {
		StringBuffer r = new StringBuffer(s.length());
		r.setLength(s.length());
		int current = 0;
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != c)
				r.setCharAt(current++, cur);
		}
		return r.toString();
	}

	public static String insertChar(String src, char c, int pos) {
		String dst = "";

		if (src.length() == 0)
			dst = dst + c;
		else if (pos == src.length())
			dst = src + c;
		else
			dst = src.substring(0, pos) + c + src.substring(pos, src.length());

		return dst;
	}
	
	public static String insertString(String src, String s, int pos) {
		String dst = "";

		if (src.length() == 0)
			dst = dst + s;
		else if (pos == src.length())
			dst = src + s;
		else if (pos < src.length())
			dst = src.substring(0, pos) + s + src.substring(pos, src.length());

		return dst;
	}
}
