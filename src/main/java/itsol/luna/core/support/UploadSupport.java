package itsol.luna.core.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class UploadSupport {

	@SuppressWarnings("resource")
	public static Boolean upload(File file, String contentType, String fileName) {
		try {
			//System.out.println(new File(".").getCanonicalPath() + "\\" + fileName);
			FileChannel src = new FileInputStream(file).getChannel();
			FileChannel dest = new FileOutputStream(new File(fileName)).getChannel();
			dest.transferFrom(src, 0, src.size());
			
			return true;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} 
	}
	
}
