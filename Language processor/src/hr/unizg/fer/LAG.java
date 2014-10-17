package hr.unizg.fer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class LAG {
	
	private StringBuilder mLA_String;
	private int mFuncInsertPoint;
	private int mVarInsertPoint;
	
	public LAG(){
		mLA_String = new StringBuilder();
		mLA_String.append(
				"package hr.unizg.fer;"
				+ "\n"
				+ "\n"
				+ "\npublic class LA{"
				+ "\n"
				+ "\n"
				+ "\n\tpublic static void main(String[] args){"
				+ "\n"
				+ "\n\t}"
				+ "\n}");
		
		mVarInsertPoint = 41;
		mFuncInsertPoint = 41;
	}
	
	public void AddFunction(String name){
		String inStr = 	"\n\n\tprivate static void " + name + "(){"
						+ "\n"
						+ "\n\t}";
				
		mLA_String.insert(mFuncInsertPoint, inStr);
		mFuncInsertPoint += inStr.length();
	}
	
	public void AddVariable(String type, String name){
		String inStr = 	"\n\tprivate static " + type + " " + name + ";";
		mLA_String.insert(mVarInsertPoint, inStr);
		mVarInsertPoint += inStr.length();
		mFuncInsertPoint += inStr.length();
	}
	
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
