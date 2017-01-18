package org.xidea.el.json.test;

import org.junit.Test;
import org.xidea.el.json.JSONDecoder;

public class JSONObjectDecoderTest {
    public static class TestBean{
	public int i;
	private int x;
	public int getX(){return x;};
	public void setX(int x){this.x = x;}
    }
    @Test
    public void testDecoder(){
	JSONDecoder d = new JSONDecoder(false);
	TestBean b = d.decode("{i:1,x:\"2\", }", TestBean.class);
	b = d.decode("{i:1,,   x:\"2\",}", TestBean.class);
	b = d.decode("{i:1, ,  ,  x:\"2\",}", TestBean.class);
	b = d.decode("{i:1,,/**/,x:\"2\",}", TestBean.class);
	System.out.println(b.i+"/"+b.x);
	
    }

}
