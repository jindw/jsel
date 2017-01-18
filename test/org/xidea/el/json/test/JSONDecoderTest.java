package org.xidea.el.json.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.xidea.el.impl.ExpressionImpl;
import org.xidea.el.json.JSONDecoder;
import org.xidea.el.json.JSONEncoder;

public class JSONDecoderTest {
	private int int0 = hashCode();
	private String str = ""+int0;
	private boolean b0 = 1 == (int0 & 1);
	private String[] strs = {str};
	private HashMap map;
	private List list;
	
	private JSONDecoder decoder = new JSONDecoder(false);
	@Test
	public void testDecodeList() throws IOException {
	    System.out.println(JSONDecoder.decode("[,,3,]"));
	    System.out.println(JSONDecoder.decode("[\t,,3,]"));
	    System.out.println(JSONDecoder.decode("[\t,\t\t,3,4,,]"));
	    System.out.println(JSONDecoder.decode("[1,2,3]"));
	    System.out.println(JSONDecoder.decode("[1,2,3,]"));
	    System.out.println(JSONDecoder.decode("[1,2,,3,]"));
	}
		@Test
		public void testDecodeObject() throws IOException {
		JSONDecoderTest test0 = new JSONDecoderTest();
		test0.setList(Arrays.asList("a","b"));
		String str1 = JSONEncoder.encode(test0);
		JSONDecoderTest test1 = decoder.decodeObject(str1,(Type)JSONDecoderTest.class);
		JSONDecoderTest test2 = decoder.transform(new JSONObject(str1), (Type)JSONDecoderTest.class);
		String str2 = JSONEncoder.encode(test1);
		String str21 = JSONEncoder.encode(test2);
		System.out.println(str2);
		assertEquals(str1,str2);
		assertEquals(str21,str2);
		assertFalse(test1 == test0);
		
		test1.setMap((Map)JSONDecoder.decode(str1));
		String str3 = JSONEncoder.encode(test1);
		JSONDecoderTest test3 = decoder.decodeObject(str3,(Type)JSONDecoderTest.class);
		JSONObject source = new JSONObject(str3);
		JSONDecoderTest test32 = decoder.transform(source, (Type)JSONDecoderTest.class);

		System.out.println("!!!");
		System.out.println(str3);
		String str4 = JSONEncoder.encode(test3);
		String str5 = JSONEncoder.encode(test32);
		System.out.println("#");
		System.out.println(source.get("map"));
		System.out.println(test32.map);
		System.out.println("#");
		System.out.println(str4);
		System.out.println(str5);
		assertEquals(str3,str4);
		assertEquals(str5,str4);
		
	}
	public static class Wrapper<T>{
		public List<T> list;
	}
	public static class T{
		public Wrapper<Integer> data;
	}
	@Test
	public void testG(){
		String code = "{\"data\":{\"list\":[1,2]}}";
		T t = decoder.decode(code, T.class);
		System.out.println(t);
	}

	public int getInt0() {
		return int0;
	}

	public void setInt0(int int0) {
		this.int0 = int0;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public boolean isB0() {
		return b0;
	}

	public void setB0(boolean b0) {
		this.b0 = b0;
	}

	public String[] getStrs() {
		return strs;
	}

	public void setStrs(String[] strs) {
		this.strs = strs;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = new HashMap(map);
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEncodeObject() throws IOException {
		//非JSON标准,注释,多行
		assertEquals("a\nb",JSONDecoder.decode("/**/\"a\\nb\""));
		assertEquals("\"a\\nb\"",JSONEncoder.encode("a\nb"));
		assertEquals(-1,JSONDecoder.decode("-1"));
		assertEquals(-1.1,JSONDecoder.decode("-1.1"));
		assertEquals(-0xFF1,JSONDecoder.decode("-0xFF1"));

		assertEquals(1,JSONDecoder.decode("1"));
		assertEquals(1.1,JSONDecoder.decode("1.1"));
		assertEquals(0xFF1,JSONDecoder.decode("0xFF1"));
		
	}

	@Test
	public void testJSEL() throws IOException {
		Object o = new ExpressionImpl("{key:'value',n:-1}").evaluate();
		System.out.println(o);
		assertEquals(JSONDecoder.decode("{\"key\":\"value\",\"n\":-1}"), o);
	}


}
