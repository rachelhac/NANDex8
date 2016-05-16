

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private File VMfile;
	private FileReader fileReader;
	private Scanner reader;
	private String currentLine = "";
	private String commandType = "";
	private String firstWord = "";
	private String secWord = "";
	private String thirdWord = "";
	private CodeWriter codeWriter;
	private String currentFileName;
	private Stack<String> functionNameStack;

	public Parser(File file, String currentFileName, CodeWriter cw) throws FileNotFoundException, IOException{
		this.VMfile = file;
		this.fileReader = new FileReader(this.VMfile);
		this.reader = new Scanner(this.fileReader);
		this.codeWriter = cw;
		this.currentFileName = currentFileName;
		this.functionNameStack= new Stack<String>();
		this.functionNameStack.push("Sys.Init");
	}

	/**
	 * Are there more commands in the input?
	 * If no lines are left, before returning false, it writes the final lines and closes both files.
	 * @return true if there are more lines, false otherwise.
	 */
	public boolean hasMoreCommands() throws IOException{
		if (this.reader.hasNextLine()){
			return true;
		}
		this.fileReader.close();
		return false;
	}

	/**
	 * Reads the next command from the input and makes it the current command.
	 * Should be called only if f hasMoreCommands() is true.
	 * Initially there is no current command.
	 */
	public void advance(){
		this.currentLine = reader.nextLine().trim();
		while (this.emptyLine() && this.reader.hasNextLine()){
			System.out.println("skipped line...");
			this.currentLine = reader.nextLine().trim();
		}
		if (!this.emptyLine()){
			System.out.println("current line is:" + this.currentLine);
			parseLine();
		}
		else {
			this.unParseLine();
		}
	}
	
	private boolean emptyLine(){
		return (this.currentLine.isEmpty() || this.currentLine.matches("\\s*//.*"));
	}

	void parseLine(){
		String lineParser = "([a-z-]+)(\\s+([\\w.:]+))?(\\s+([0-9]+))?\\s*(//.*)*";
		Pattern p = Pattern.compile(lineParser);
		Matcher m = p.matcher(currentLine);
		if (m.matches()){
			this.firstWord = m.group(1);
			System.out.println("1.##" + this.firstWord + "##");
			if (m.group(3) != null){
				this.secWord = m.group(3);
				System.out.println("2.##" + this.secWord + "##");
			}
			if (m.group(5) != null){
				this.thirdWord = m.group(5);
				System.out.println("3.##" + this.thirdWord + "##");
			}
			commandType();
		}
	}

	private void unParseLine(){
		this.firstWord = "";
//		this.secWord = "";
//		this.thirdWord = "";
	}

	private void commandType(){
		if (this.firstWord.equals("add") | this.firstWord.equals("sub") | this.firstWord.equals("neg") |
				this.firstWord.equals("eq") | this.firstWord.equals("gt") | this.firstWord.equals("lt")|
				this.firstWord.equals("and") | this.firstWord.equals("or") | this.firstWord.equals("not"))
		{
			this.commandType = "arithmetic";
		}
		else if (this.firstWord.equals("push") | this.firstWord.equals("pop")){
			this.commandType = "push_pop";
		}
		else
		{
			this.commandType = this.firstWord;
		}
	}

	public void writeLineCode(){

		switch(this.commandType)
		{
		case "push_pop": 
			codeWriter.writePushPop(this.firstWord, this.secWord, Integer.parseInt(this.thirdWord), currentFileName);
		break;
		case "arithmetic": 
			codeWriter.writeArithmetic(this.firstWord);
		break;
		case "label": 
			codeWriter.writeLabel(functionNameStack.peek() + "$" + this.secWord);		//the next types we didn't implement yet
		break;
		case "goto": 
			codeWriter.writeGoto(functionNameStack.peek() + "$" + this.secWord);
		break;
		case "if-goto": 
			codeWriter.writeIfGoto(functionNameStack.peek() + "$" + this.secWord);
		break;
		case "function": 
			functionNameStack.push(this.currentFileName + "." + this.secWord);
			codeWriter.writeFunction(functionNameStack.peek(), Integer.parseInt(this.thirdWord));
		break;
		case "return":
				functionNameStack.pop();
				codeWriter.writeReturn();
		break;
		case "call": 
				codeWriter.writeCall(this.currentFileName + "." + this.secWord, Integer.parseInt(this.thirdWord), functionNameStack.peek());;
		break;
		default:
			break;
		}
	}

}


