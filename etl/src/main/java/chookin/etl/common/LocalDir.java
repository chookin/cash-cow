package chookin.etl.common;

import java.io.File;

/**
 * local directory
 * 
 */
public class LocalDir implements FileSystem {
	private String path;

	public LocalDir(String pathname) {
		this.path = new File(pathname).getPath();
	}

	public String getPath() {
		return this.path;
	}

	/*
	 * if the directory does not exist, then create it
	 * 
	 * @see chookin.etl.common.FileSystem#open()
	 */
	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * for a directory, we do nothing on close method
	 * 
	 * @see chookin.etl.common.FileSystem#close()
	 */
	@Override
	public boolean close() {
		return true;
	}
}
