

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CodeWriter {

	private PrintWriter asmFile;
	private int continueTagNum = 0;
	private int trueTagNum = 0;
	private int negCounter = 0;
	private int comprisonCounter = 0;

	CodeWriter(String fileName) {
		File file = new File(fileName + ".asm");
		try {
			asmFile = new PrintWriter(file);
			//writeBootsrap();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	void setFileName() {
		// not sure what to do here yet
	}
	public void writeBootsrap(){
		asmFile.println("//Bootsrap");
		asmFile.println("@256");
		asmFile.println("D=A");
		asmFile.println("@SP");
		asmFile.println("M=D");
		this.writeCall(("Sys.Init"), 0, "");
		this.writeFunction("Sys.Init", 0);
		
	}
	
	public void writeFileNameComment(String fileName){
		asmFile.println("//We start file:" + fileName);
	}

	public void writeArithmetic(String command) {
		switch (command) {
		case "add":
			writeBinaryFunc("+");
			break;
		case "sub":
			writeBinaryFunc("-");
			break;
		case "and":
			writeBinaryFunc("&");
			break;
		case "or":
			writeBinaryFunc("|");
			break;
		case "neg":
			writeUnaryFunc("-");;
			break;
		case "not":
			writeUnaryFunc("!");;
			break;
		case "eq":
			writeComparisonFunc("JEQ");
			break;
		case "gt":
			writeComparisonFunc("JGT");
			break;
		case "lt":
			writeComparisonFunc("JLT");
			break;

		}
	}

	public void writePushPop(String command, String segment, int index, String fileName) {

		switch (command) {
		case "push":
			if (segment.equals("constant")) {
				pushConstant(index);
			} else {
				push(segment, index, fileName);
			}
			break;
		case "pop":
			pop(segment, index, fileName);
			break;
		}

	}

	public void writeFinalLines() {
		asmFile.println("($END)");
		asmFile.println("@$END");
		asmFile.println("0;JMP");
	}

	public void writeLabel(String labelName){		// label should be visible only to its function
		asmFile.println("// declare label " + labelName);
		asmFile.println("(" + labelName + ")");
	}
	
	public void writeGoto(String labelName){
		asmFile.println("// goto " + labelName);
		asmFile.println("@" + labelName);
		asmFile.println("0;JMP");
	}
	
	public void writeIfGoto(String labelName){
		asmFile.println("// if-goto " + labelName);
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@" + labelName);
		asmFile.println("D;JNE");
	}
	
	public void writeFunction(String functionName, int k){
		asmFile.println("(" + functionName + ")");
		for (int i = 0; i < k; i++){
			asmFile.println("@0");
			asmFile.println("D=A");
			pushDValToStack();
		}
	}
	
	public void writeCall(String sonFunction, int n, String parentFunction){
		asmFile.println("// call " + sonFunction);
		asmFile.println("@$return-"+parentFunction);	// push return address
		asmFile.println("D=A");
		pushDValToStack();
		asmFile.println("@LCL");
		asmFile.println("D=M");
		pushDValToStack();
		asmFile.println("@ARG");
		asmFile.println("D=M");
		pushDValToStack();
		asmFile.println("@THIS");
		asmFile.println("D=M");
		pushDValToStack();
		asmFile.println("@THAT");
		asmFile.println("D=M");
		pushDValToStack();
		asmFile.println("@" + n);			// ARG = SP - (n+5)
		asmFile.println("D=A");
		asmFile.println("@5");
		asmFile.println("D=D+A");
		asmFile.println("@SP");
		asmFile.println("D=M-D");
		asmFile.println("@ARG");
		asmFile.println("M=D");
		asmFile.println("@SP");				// LCL = SP
		asmFile.println("D=M");
		asmFile.println("@LCL");
		asmFile.println("M=D");
		asmFile.println("@" + sonFunction);		//goto function
		asmFile.println("0;JMP");
		asmFile.println("($return-" + parentFunction + ")");		//declare return label
	}
	
	public void writeReturn(){
		asmFile.println("// return");
		asmFile.println("@LCL");		//FRAME = LCL
		asmFile.println("D=M");
		asmFile.println("@$FRAME");
		asmFile.println("M=D");
		asmFile.println("@5");			//RET = *(FRAME - 5)
		asmFile.println("D=A");
		asmFile.println("@$FRAME");
		asmFile.println("A=M-D");
		asmFile.println("D=M");
		asmFile.println("@$RET");
		asmFile.println("M=D");
		asmFile.println("@SP");			//*ARG = pop() - inserting return value to top of stack
		asmFile.println("A=M-1");
		asmFile.println("D=M");
		asmFile.println("@ARG");
		asmFile.println("A=M");
		asmFile.println("M=D");
		asmFile.println("D=A");
		asmFile.println("@SP");			//SP = ARG + 1
		asmFile.println("M=D+1");
		asmFile.println("@$FRAME");		//THAT = *(FRAME -1)
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@THAT");
		asmFile.println("M=D");
		asmFile.println("@$FRAME");		//THIS = *(FRAME -2)
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@THIS");
		asmFile.println("M=D");
		asmFile.println("@$FRAME");		//ARG = *(FRAME -3)
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@ARG");
		asmFile.println("M=D");
		asmFile.println("@$FRAME");		//LCL = *(FRAME -4)
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@LCL");
		asmFile.println("M=D");
		
		asmFile.println("@$RET");
		asmFile.println("A=M");
		asmFile.println("0;JMP");
		
	}
	

	public void close() {
		asmFile.close();
	}

	private void writeBinaryFunc(String sign) {
		asmFile.println("// function " + sign + ":");
		fromStackToTmp();
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@R13");
		asmFile.println("D=D"+ sign + "M");
		updateStackFromDAndPointer();

	}


	private void writeUnaryFunc(String sign){
		asmFile.println("// function: " + sign);
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("M=" + sign + "M");
		asmFile.println("@SP");
		asmFile.println("M=M+1");
	}

	private void writeComparisonFunc(String jumpCondition){
		asmFile.println("//comparison " + jumpCondition.substring(1) + ":");
		saveSignsToTmp();
		asmFile.println("@R14");
		asmFile.println("D=M");
		asmFile.println("@R15");
		asmFile.println("D=D+M     //D is 0 if both numbers have different signs and is +-2 if they have the same sign");
		asmFile.println("@$USUAL-COMPARISON" + comprisonCounter);
		asmFile.println("D;JNE       //if D=+-2 we do regular compare");
		asmFile.println("//esle: we compare R15 and R14:");
		asmFile.println("@R15");
		asmFile.println("D=M");
		asmFile.println("@R14");
		asmFile.println("D=D-M");
		asmFile.println("@$TRUE" + trueTagNum);
		asmFile.println("D;" + jumpCondition);
		asmFile.println("D=0");
		asmFile.println("@$CONTINUE" + continueTagNum);
		asmFile.println("0;JMP");
		asmFile.println("($TRUE" + trueTagNum + ")");
		trueTagNum++;
		asmFile.println("D=-1");
		asmFile.println("($CONTINUE" + continueTagNum + ")");
		continueTagNum++;
		//write D to stack:
		asmFile.println("@SP");
		asmFile.println("M=M-1");
		asmFile.println("@SP");
		asmFile.println("A=M-1");
		asmFile.println("M=D");
		asmFile.println("@$CONTINUE" + continueTagNum);
		asmFile.println("0;JMP");
		continueTagNum++;
		// here we write the usual comparison
		asmFile.println("($USUAL-COMPARISON" + comprisonCounter + ")");
		fromStackToTmp();
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M"); // D holds x
		asmFile.println("@R13");
		asmFile.println("D=D-M");
		asmFile.println("@$TRUE" + trueTagNum);
		asmFile.println("D;" + jumpCondition);
		asmFile.println("D=0");
		asmFile.println("@$CONTINUE" + continueTagNum);
		asmFile.println("0;JMP");
		asmFile.println("($TRUE" + trueTagNum + ")");
		trueTagNum++;
		asmFile.println("D=-1");
		asmFile.println("($CONTINUE" + continueTagNum + ")");
		asmFile.println("@SP");
		asmFile.println("A=M");
		asmFile.println("M=D");
		asmFile.println("@SP");
		asmFile.println("M=M+1");
		asmFile.println("($CONTINUE" + (continueTagNum-1)+ ")");
		continueTagNum++;
		comprisonCounter++;
	}

	private void saveSignsToTmp(){
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@$NEG" + negCounter);
		asmFile.println("D;JLT");
		asmFile.println("@R14");
		asmFile.println("M=1");
		asmFile.println("@$CONTINUE" + continueTagNum);
		asmFile.println("0;JMP");
		asmFile.println("($NEG" + negCounter+ ")");
		asmFile.println("@R14");
		asmFile.println("M=-1");
		asmFile.println("($CONTINUE" + continueTagNum + ")");
		continueTagNum++;
		negCounter++;
		asmFile.println("@SP");
		asmFile.println("A=M-1");
		asmFile.println("D=M");
		asmFile.println("@$NEG" + negCounter);
		asmFile.println("D;JLT");
		asmFile.println("@R15");
		asmFile.println("M=1");
		asmFile.println("@$CONTINUE" + continueTagNum);
		asmFile.println("0;JMP");
		asmFile.println("($NEG" + negCounter + ")");
		asmFile.println("@R15");
		asmFile.println("M=-1");
		asmFile.println("($CONTINUE" + continueTagNum + ")");
		asmFile.println("@SP");
		asmFile.println("M=M+1");
		continueTagNum++;
	}




	private void push(String segment, int i, String fileName) // TODO: figure out segment
	// name/location etc
	{
		asmFile.println("// push " + segment + " " + i);
		getToRelevantPlace(segment, i, fileName);
		asmFile.println("D=M");
		pushDValToStack();
	}

	private void pushDValToStack() {
		asmFile.println("@SP");
		asmFile.println("A=M");
		asmFile.println("M=D");
		asmFile.println("@SP");
		asmFile.println("M=M+1");
	}

	private String getSegAssembly(String segment) {
		switch(segment) {
		case "local":
			return "LCL";
		case "argument":
			return "ARG";
		case "this":
			return "THIS";
		case "that":
			return "THAT";

		default:
			throw new IllegalArgumentException(); 
		}
	}

	private void pop(String segment, int i, String fileName) // TODO: figure out segment
	// name/location etc
	{
		asmFile.println("// pop " + segment + " " + i);
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@R13");
		asmFile.println("M=D");
		getToRelevantPlace(segment, i, fileName);
		asmFile.println("D=A");
		asmFile.println("@R14");
		asmFile.println("M=D");
		asmFile.println("@R13");
		asmFile.println("D=M");
		asmFile.println("@R14");
		asmFile.println("A=M");
		asmFile.println("M=D");

	}

	private void getToRelevantPlace(String segment, int i, String fileName)
	{
		if (segment.equals("pointer")){
			if (i == 0){
				asmFile.println("@3");
			}
			else if (i == 1) {
				asmFile.println("@4");
			}
		}

		else if (segment.equals("temp")) {
			asmFile.println("@5");
			asmFile.println("D=A");
			asmFile.println("@" + i);
			asmFile.println("A=D+A");
		}

		else if (segment.equals("static")) {
			asmFile.println("@"+fileName+"."+i);
		}
		else {
			String asmSeg = getSegAssembly(segment);
			asmFile.println("@" + asmSeg);
			progressXSteps(i);	
		}
	}

	private void progressXSteps(int i) {
		asmFile.println("D=M");
		asmFile.println("@" + i);
		asmFile.println("A=D+A");
	}

	private void pushConstant(int i) {
		asmFile.println("// push constant " + i);
		asmFile.println("@" + i);
		asmFile.println("D=A");
		updateStackFromDAndPointer();
	}

	private void fromStackToTmp() {
		asmFile.println("@SP");
		asmFile.println("AM=M-1");
		asmFile.println("D=M");
		asmFile.println("@R13");
		asmFile.println("M=D");
	}

	private void updateStackFromDAndPointer() {
		asmFile.println("@SP");
		asmFile.println("A=M");
		asmFile.println("M=D");
		asmFile.println("@SP");
		asmFile.println("M=M+1");
	}

}
