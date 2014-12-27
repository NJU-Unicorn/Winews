package cn.edu.nju.unicorn.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFilter {
	private HashSet<Pattern> filterList = new HashSet<Pattern>();
	private HashSet<Pattern> matchList = new HashSet<Pattern>();

	public URLFilter() {
	}

	public void addFilterRegex(String regex) {
		Pattern p = Pattern.compile(regex);
		filterList.add(p);
	}

	public void addMatchRegex(String regex) {
		Pattern p = Pattern.compile(regex);
		matchList.add(p);
	}

	public boolean filter(String s) {
		for (Pattern p : filterList) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				return false;
			}
		}
		return true;
	}

	public ArrayList<String> filter(ArrayList<String> list) {
		for (Pattern p : filterList) {
			for (int i = 0; i < list.size(); i++) {
				Matcher m = p.matcher(list.get(i));
				if (m.find()) {
					list.remove(i);
					i--;
				}
			}
		}
		return list;
	}
}
