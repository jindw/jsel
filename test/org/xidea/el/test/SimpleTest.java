package org.xidea.el.test;

import org.xidea.el.Expression;
import org.xidea.el.impl.ExpressionFactoryImpl;

public class SimpleTest {
	public static void main(String...args){
		Expression el = ExpressionFactoryImpl.getInstance().create("1+1");
		Object val = el.evaluate();
		System.out.println(val);
		
	}

}
