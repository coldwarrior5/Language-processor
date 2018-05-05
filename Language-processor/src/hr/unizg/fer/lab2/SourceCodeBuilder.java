package hr.unizg.fer.lab2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SourceCodeBuilder {
	
	private String mClassName;
	private StringBuilder mStringB;
	private int mImportInsertPoint;
	private int mClassInsertPoint;
	private int mVarInsertPoint;
	private int mFuncInsertPoint;
	private int mMainInsertPoint;
	
	/**
	 * Creates a new instance of SourceCodeGenerator.
	 * @param - none
	 */
	public SourceCodeBuilder(String className){
		mClassName = className;
		mStringB = new StringBuilder();
		mStringB.append(
				"package hr.unizg.fer.lab2;"
				+ "\n"
				+ "\n"
				+ "\npublic class " + mClassName + "{"
				+ "\n"
				+ "\n"
				+ "\n\tpublic static void main(String[] args){"
				+ "\n"
				+ "\n\t}"
				+ "\n}");
		
		mImportInsertPoint = 27;
		mClassInsertPoint = 28;
		mVarInsertPoint = 44 + mClassName.length();
		mFuncInsertPoint = 44 + mClassName.length();
		mMainInsertPoint = 86 + mClassName.length();
	}
	
	/**
	 * Adds a new package to the import part of the source.
	 * @param packageName - name of the package to import
	 */
	public void AddImport(String packageName){
		String inStr = 	"\nimport " + packageName + ";";
		mStringB.insert(mImportInsertPoint, inStr);
		mImportInsertPoint += inStr.length();
		mClassInsertPoint += inStr.length();
		mVarInsertPoint += inStr.length();
		mFuncInsertPoint += inStr.length();
		mMainInsertPoint += inStr.length();
	}	
	
	/**
	 * Adds a new class to the source.
	 * @param name - name of the new class
	 * body - body of new class
	 */
	public void AddClass(String name, String body){
		
		String[] lines = body.split("\n");
		String inStr = 	"\nclass " + name + "{";
		for (int i = 0; i < lines.length; ++i) inStr += "\n\t" + lines[i];
		inStr += "\n}\n";
				
		mStringB.insert(mClassInsertPoint, inStr);
		mClassInsertPoint += inStr.length();
		mVarInsertPoint += inStr.length();
		mFuncInsertPoint += inStr.length();
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Adds a new private static variable to the LA class.
	 * @param typeAndName - type and name of the new variable (eg. "int a")
	 */
	public void AddVariable(String typeAndName){
		String inStr = 	"\n\tprivate static " + typeAndName;
		mStringB.insert(mVarInsertPoint, inStr);
		mVarInsertPoint += inStr.length();
		mFuncInsertPoint += inStr.length();
		mMainInsertPoint += inStr.length();
	}	
	
	/**
	 * Adds a new private static function to the LA class.
	 * @param retType - type of the new function
	 * name - name of the new function
	 * arguments - arguments of the new function (eg. "String a, String b, int c")
	 * body - body of the new function.
	 */
	public void AddFunction(String retType, String name, String arguments, String body){
		
		String[] lines = body.split("\n");
		String inStr = 	"\n\n\tprivate static " + retType + " " + name + "(" + arguments + "){";
		for (int i = 0; i < lines.length; ++i) inStr += "\n\t\t" + lines[i];
		inStr += "\n\t}";
				
		mStringB.insert(mFuncInsertPoint, inStr);
		mFuncInsertPoint += inStr.length();
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Adds a new source parts in the public static main function in the LA class.
	 * @param stringToAdd - new string that represents some part of the code.
	 */
	public void AddInMain(String stringToAdd){
		
		String inStr = "";
		String[] lines = stringToAdd.split("\n");
		for (int i = 0; i < lines.length; ++i) inStr += "\n\t\t" + lines[i];
		mStringB.insert(mMainInsertPoint, inStr);
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Adds a new empty line in the source of the public static main function in the LA class.
	 * @param - none
	 */
	public void AddEmptyLineInMain(){
		
		String inStr = "\n";
		mStringB.insert(mMainInsertPoint, inStr);
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Writes all generated code to a file named LA.java.
	 * @param dir - directory of the file to write into or create.
	 */
	public void Write(String dir){
		try {
			PrintWriter writer = new PrintWriter(dir + mClassName + ".java", "UTF-8");
			writer.print(mStringB.toString());
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}