package org.xidea.el.test;

import java.util.Arrays;

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
		
	}

}
