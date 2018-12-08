import java.io.File;

public class FileExpert {
	
	
	public FileExpert() {}
	
	protected static void removeFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
}