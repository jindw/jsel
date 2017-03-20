package org.xidea.el.impl.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.xidea.el.fn.ECMA262Impl;

public class NumberArithmeticTest {
	@Test
	public void testToNumber() {
		assertEquals(17, ECMA262Impl.ToNumber("0x11"));
		assertEquals(17, ECMA262Impl.ToNumber("0X11"));
		assertEquals(-0, ECMA262Impl.ToNumber("-0"));
		assertEquals(-0, ECMA262Impl.ToNumber("0"));
		assertEquals(11, ECMA262Impl.ToNumber("11"));
		

		assertEquals(11, ECMA262Impl.ToNumber("011"));;
		assertEquals(-11, ECMA262Impl.ToNumber("-011"));
		
		
		double tiny = 0.0000001;
		assertEquals(10f, ECMA262Impl.ToNumber("+1e1").floatValue(),tiny);
		assertEquals(1.1, ECMA262Impl.ToNumber("1.1").doubleValue(),tiny);;
		assertEquals(-1.1, ECMA262Impl.ToNumber("-1.1").doubleValue(),tiny);
		assertEquals(1.1E1, ECMA262Impl.ToNumber("1.1E1").doubleValue(),tiny);
		assertEquals(1.1e-1, ECMA262Impl.ToNumber("1.1e-1").doubleValue(),tiny);
		assertEquals(1.1E1, ECMA262Impl.ToNumber("+1.1E1").doubleValue(),tiny);
		assertEquals(-1.1e-1, ECMA262Impl.ToNumber("-1.1e-1").doubleValue(),tiny);
		

		assertEquals(10d, ECMA262Impl.ToNumber("1E1").doubleValue(),tiny);
		assertEquals(10d, ECMA262Impl.ToNumber("1e1").doubleValue(),tiny);
	}

}
