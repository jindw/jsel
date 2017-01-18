package org.xidea.el.json.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.xidea.el.json.JSONEncoder;

public class JSONEncoderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEncodeObject() throws IOException {
		assertEquals("{}", JSONEncoder.encode(new Object()));
		StringBuilder out = new StringBuilder();
		new JSONEncoder(null,false,10).encode(new Object(),out);
		assertEquals("{\"class\":\"java.lang.Object\"}", out.toString());
		

		out = new StringBuilder();
		HashMap value = new HashMap();
		value.put("class", Object.class);
		new JSONEncoder(null,true,10).encode(value,out);
		assertEquals("{\"class\":\"java.lang.Object\"}", out.toString());

		out = new StringBuilder();
		new JSONEncoder(null,true,10).encode(new Object(),out);
		assertEquals("{}", out.toString());
	}

	@Test
	public void testEncodeObjectField() throws IOException {
		assertEquals("{}", JSONEncoder.encode(new Object()));
		StringBuilder out = new StringBuilder();
		Object value = new Object(){public int a;};
		new JSONEncoder(null,false,10).encode(value,out);
		assertEquals("{\"a\":0,\"class\":\""+value.getClass().getName()+"\"}", out.toString());
	}

}
