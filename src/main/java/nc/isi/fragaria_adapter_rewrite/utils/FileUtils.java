package nc.isi.fragaria_adapter_rewrite.utils;

public final class FileUtils {
	private FileUtils() {
	}

	public static String removeExtension(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

}
