package org.xidea.el.test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xidea.el.Expression;
import org.xidea.el.impl.ExpressionFactoryImpl;

public class SimpleTest {
	public static void main(String...args){
		//'abcdefg'.split('c')[0]
		Expression el = ExpressionFactoryImpl.getInstance().create("'abcdefg'.split(/[dg]/)[0]");
		Object val = el.evaluate();
		System.out.println(Arrays.asList(val));

		//System.out.println(Integer.parseInt("0x11"));
		//System.out.println(Float.parseFloat("0x11"));
		System.out.println(parseFloat("-0x123.1"));
	}

	private static final Pattern FLOAT_PARTTERN = Pattern
			.compile("^[\\+\\-]?[0-9]*(?:\\.[0-9]+)?");
	protected static Number parseFloat(String text) {
		if (text.length() > 0) {
			Matcher matcher = FLOAT_PARTTERN.matcher(text);
			if (matcher.find()) {
				return Double.parseDouble(matcher.group(0));
			}
		}
		return Double.NaN;
	}
}
