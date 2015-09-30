package artGame.ui;

import java.io.File;
import java.io.IOException;

public class FileUtils {

	public static String getFilePath(String file) throws IOException {
		return getFileFromDir(file, ".");
	}
	
	private static String getFileFromDir(String file, String dir) throws IOException {
		File base = new File(dir);
		if(base.isDirectory()){
			for(File f : base.listFiles()){
				if(f.isDirectory()){
					String returnVal = getFileFromDir(file, f.getCanonicalPath());
					if(returnVal != null){
						return returnVal;
					}
				} else {
					if(f.getCanonicalPath().contains(file)){
						return f.getCanonicalPath();
					}
				}
			}
		}
		return null;
	}
	
}
