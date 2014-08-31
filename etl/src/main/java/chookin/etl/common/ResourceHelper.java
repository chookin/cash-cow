package chookin.etl.common;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ResourceHelper {
	private static final Logger LOG = Logger.getLogger(ResourceHelper.class);

	private ResourceHelper() {
	}

	/**
	 * get the relative path of relativeTo that relatives absoluteDirPath
     * Thanks to: "http://mrpmorris.blogspot.com/2007/05/convert-absolute-path-to-relative-path.html"
	 * 
	 * @param absoluteDirPath
	 * @param relativeTo
	 * @return
	 */
	public static String getRelativePath(String absoluteDirPath,
			String relativeTo) {
		absoluteDirPath = absoluteDirPath.replaceAll("\\\\", "/");
		relativeTo = relativeTo.replaceAll("\\\\", "/");

		if (absoluteDirPath.equals(relativeTo) == true) {
			StringBuilder relativePath = new StringBuilder();
			relativePath.append("./").append(relativeTo.substring(relativeTo
					.lastIndexOf("/")));
			return relativePath.toString();
		} else {
			String[] absoluteDirectories = absoluteDirPath.split("/");
			String[] relativeDirectories = relativeTo.split("/");

			// Get the shortest of the two paths
			int length = absoluteDirectories.length < relativeDirectories.length ? absoluteDirectories.length
					: relativeDirectories.length;

			// Use to determine where in the loop we exited
			int lastCommonRoot = -1;
			int index;

			// Find common root
			for (index = 0; index < length; index++) {
				if (absoluteDirectories[index]
						.equals(relativeDirectories[index])) {
					lastCommonRoot = index;
				} else {
					break;
					// If we didn't find a common prefix then throw
				}
			}
			if (lastCommonRoot != -1) {
				// Build up the relative path
				StringBuilder relativePath = new StringBuilder();
				// Add on the ..
				for (index = lastCommonRoot + 1; index < absoluteDirectories.length; index++) {
					if (absoluteDirectories[index].length() > 0) {
						relativePath.append("../");
					}
				}
				for (index = lastCommonRoot + 1; index < relativeDirectories.length - 1; index++) {
					relativePath.append(relativeDirectories[index] + "/");
				}
				relativePath
						.append(relativeDirectories[relativeDirectories.length - 1]);
				return relativePath.toString();
			}else {
				return null;
			}
		}
	}

	/**
	 * @param dirpath
	 * @throws IOException
	 *             if exists a file with the same name
	 */
	public static void mkdirs(String dirpath) throws IOException {
		if (new File(dirpath).isDirectory()) {
			return;
		}
		dirpath = dirpath.replace('\\', '/').replace("//", "/");
		int indexDiskPathEnd = dirpath.indexOf(':') + 1;
		String disk = dirpath.substring(0, indexDiskPathEnd);

		String[] paths = dirpath.substring(indexDiskPathEnd).split("/");
		String basePath = disk;
		for (String path : paths) {
			if (path.isEmpty()) {
				continue;
			}
			basePath = basePath + "/" + path;
			File file = new File(basePath);
			if (file.isDirectory()) {
				continue;
			}
			if (file.isFile()) {
				throw new IOException(String.format("already exist file %s",
						file.getPath()));
			}
			file.mkdir();
		}
	}
}
