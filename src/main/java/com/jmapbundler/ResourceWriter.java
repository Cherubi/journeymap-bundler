package com.jmapbundler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ResourceWriter extends FileExpert {

	private final String fileName;

	public ResourceWriter(String scriptName) {
		this.fileName = scriptName;
	}

	public void copyResourceFile() {
		File file = new File(fileName);
		try {
			this.removeFile(file);
			FileWriter kirjuri = this.addResource(new FileWriter(file));
			kirjuri.append(this.getResourceContent(fileName));
		} catch (Exception e) {
			System.out.println("Failed to copy resource " + fileName);
		} finally {
			try {
				closeAllResources();
				file.setReadable(true, false);
			} catch (Exception e) {}
		}
	}

	private String getResourceContent(String resourceName) throws IOException {
		InputStream stream = this.getClass().getResourceAsStream("/" + resourceName);
		Scanner streamScanner = this.addResource(new Scanner(stream)).useDelimiter("\\A");
		if (streamScanner.hasNext()) {
			return streamScanner.next();
		}
		return null;
	}

}
