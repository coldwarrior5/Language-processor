package hr.unizg.fer.lab1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SourceCodeBuilder {
	
	private StringBuilder mLA_String;
	private int mImportInsertPoint;
	private int mClassInsertPoint;
	private int mVarInsertPoint;
	private int mFuncInsertPoint;
	private int mMainInsertPoint;
	
	/**
	 * Creates a new instance of SourceCodeGenerator.
	 * @param - none
	 */
	public SourceCodeBuilder(){
		mLA_String = new StringBuilder();
		mLA_String.append(
				"package hr.unizg.fer.lab1;"
				+ "\n"
				+ "\n"
				+ "\npublic class LA{"
				+ "\n"
				+ "\n"
				+ "\n\tpublic static void main(String[] args){"
				+ "\n"
				+ "\n\t}"
				+ "\n}");
		
		mImportInsertPoint = 27;
		mClassInsertPoint = 28;
		mVarInsertPoint = 46;
		mFuncInsertPoint = 46;
		mMainInsertPoint = 88;
	}
	
	/**
	 * Adds a new package to the import part of the source.
	 * @param packageName - name of the package to import
	 */
	public void AddImport(String packageName){
		String inStr = 	"\nimport " + packageName + ";";
		mLA_String.insert(mImportInsertPoint, inStr);
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
				
		mLA_String.insert(mClassInsertPoint, inStr);
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
		mLA_String.insert(mVarInsertPoint, inStr);
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
				
		mLA_String.insert(mFuncInsertPoint, inStr);
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
		mLA_String.insert(mMainInsertPoint, inStr);
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Adds a new empty line in the source of the public static main function in the LA class.
	 * @param - none
	 */
	public void AddEmptyLineInMain(){
		
		String inStr = "\n";
		mLA_String.insert(mMainInsertPoint, inStr);
		mMainInsertPoint += inStr.length();
	}
	
	/**
	 * Writes all generated code to a file named LA.java.
	 * @param dir - directory of the file to write into or create.
	 */
	public void Write(String dir){
		try {
			PrintWriter writer = new PrintWriter(dir + "LA" + ".java", "UTF-8");
			writer.print(mLA_String.toString());
			writer.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}