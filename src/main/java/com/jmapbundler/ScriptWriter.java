package com.jmapbundler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ScriptWriter extends FileExpert {

	private final String scriptName;

	public ScriptWriter(String scriptName) {
		this.scriptName = scriptName;
	}

	public void writeScriptFile() {
		File js = new File(scriptName);
		try {
			this.removeFile(js);
			FileWriter kirjuri = this.addResource(new FileWriter(js));
			kirjuri.append(this.getScriptFileContent("mapFunctions.js"));
		} catch (Exception e) {
			System.out.println("FileWriter failed while writing mapFunctions.js");
		} finally {
			try {
				closeAllResources();
				js.setReadable(true, false);
			} catch (Exception e) {}
		}
	}

	private String getScriptFileContent(String resourceName) throws IOException {
		InputStream cssStream = this.getClass().getResourceAsStream("/" + resourceName);
		Scanner streamScanner = this.addResource(new Scanner(cssStream)).useDelimiter("\\A");
		if (streamScanner.hasNext()) {
			return streamScanner.next();
		}
		return null;
	}

}
