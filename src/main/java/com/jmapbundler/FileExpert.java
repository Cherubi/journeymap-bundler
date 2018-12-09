package com.jmapbundler;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class FileExpert {

	private final List<Closeable> closeableResources = new ArrayList<>();

	protected void removeFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}

	protected <T extends Closeable> T addResource(T resource) {
		this.closeableResources.add(resource);
		return resource;
	}

	protected void closeAllResources() {
		for (Closeable resource : this.closeableResources) {
			try {
				resource.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.closeableResources.clear();
	}

}
