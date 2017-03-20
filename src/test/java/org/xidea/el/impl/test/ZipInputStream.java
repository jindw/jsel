package org.xidea.el.impl.test;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.Box.Filler;


public class ZipInputStream extends java.util.zip.ZipInputStream{
	  public ZipInputStream(InputStream stream) {
		super((stream));
	}

	public int read(byte[] buf, int s, int l)
			    throws IOException{
		  try{
			  return super.read(buf,s,l);
		  }catch(java.util.zip.ZipException ze){
			  ze.printStackTrace();
			  return 0;
		  }
	  }
}
class WrapperStream extends java.io.PushbackInputStream{

	 int c;
	protected WrapperStream(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	public int read(byte[] buf, int offset, int count)
		    throws IOException{
		this.c = super.read(buf, offset, count);
		return this.c;
	}
	
}