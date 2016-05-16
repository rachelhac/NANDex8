

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

public class VMtranslator {

	public static void main(String[] args) throws IOException, FileNotFoundException {

		//String theirTests = "C:\\Users\\Rachel\\Documents\\University\\NAND\\nand2tetris\\nand2tetris\\projects\\07\\finalTest";
		//String ourTests = "C:\\Users\\mkessler\\workspace\\ex7nand\\PushPopTest.vm";
		//File file = new File(theirTests);
		File file = new File(args[0]).getAbsoluteFile();
		File[] fileArray = null;
		String fileName = null;
		CodeWriter cw = null;

		if (file.isFile()) {
			fileArray = new File[1];
			fileArray[0] = file;
			System.out.println("for file: path is:" + file.getAbsolutePath());
			fileName = file.getAbsolutePath();
			int last = fileName.lastIndexOf(".");
			fileName = last >= 1 ? fileName.substring(0, last) : fileName;
		}
		else if (file.isDirectory()) {
			fileArray = file.listFiles(new FilenameFilter() {
				public boolean accept(File file, String name) {
					return name.toLowerCase().endsWith(".vm");
				}
			});
			System.out.println("for directory: fileNameis:" + file.getAbsolutePath());
			fileName = file.getAbsolutePath() +File.separator + file.getName();
			System.out.println(fileName);
		}
		cw = new CodeWriter(fileName);


		for (File f : fileArray)
		{
			cw.writeFileNameComment(f.getName());
			Parser p = new Parser(f, f.getName().substring(0, f.getName().lastIndexOf(".")), cw);
			while (p.hasMoreCommands()) {
				p.advance();
				p.writeLineCode();
			}
		}
		cw.writeFinalLines();
		cw.close();
	}
}
