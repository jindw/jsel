package org.xidea.el.json.test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.junit.Test;
import org.xidea.el.impl.ReflectUtil;
import org.xidea.el.json.test.Callback.PrepareCallback;

interface A<T>{}
interface B<T,Y> extends PrepareCallback<T,Y>{
    
}
class BC implements B<byte[],String>{

    public Object prepare(byte[] rawData) {
	// TODO Auto-generated method stub
	return null;
    }

    public void callback(String result) {
	// TODO Auto-generated method stub
	
    }

    public void error(Throwable ex, boolean callbackError) {
	// TODO Auto-generated method stub
	
    }
    
}

class SearchCallback implements
PrepareCallback<byte[], A<String>> {

    public void error(Throwable ex, boolean callbackError) {
	// TODO Auto-generated method stub
	
    }

    public Object prepare(byte[] rawData) {
	// TODO Auto-generated method stub
	return null;
    }

    public void callback(A<String> result) {
	// TODO Auto-generated method stub
	
    }


}
public class ReflectTest {
    public byte[] bytes;
    public byte[] getBytes2(){return null;}
    @Test
    public void testBytes() throws NoSuchFieldException, SecurityException{
	Field field = ReflectTest.class.getDeclaredField("bytes");
	Type type = field.getType();
	System.out.println(type);
	System.out.println(type.getClass());
	System.out.println(field.getType().getComponentType());
	type = ReflectUtil.getPropertyType(ReflectTest.class, "bytes2");
	type = ReflectUtil.getParameterizedType((new SearchCallback(){}).getClass(), PrepareCallback.class, 0);
	System.out.println();
	System.out.println(type);
	System.out.println(type.getClass());
    }

}
