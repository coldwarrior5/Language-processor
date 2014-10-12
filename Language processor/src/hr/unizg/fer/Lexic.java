package hr.unizg.fer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * 
 * This is class where you create java document
 * Here is where we will be adding methods to add lines in the lexical analyzer.
 *
 */
public class Lexic {
	
	private PrintWriter mWriter;
	
	/**
	 * 
	 * @param name String, name of the class
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * This is constructor that creates the file and adds the beginning lines.
	 * 
	 */
	public Lexic(String name) throws FileNotFoundException, UnsupportedEncodingException{
		
		mWriter = new PrintWriter("src/hr/unizg/fer/" + name + ".java", "UTF-8");
		mWriter.println("package hr.unizg.fer;");
		
	}
	
	/**
	 * 
	 * @param importFile
	 * This function adds import lines in the file
	 * 
	 */
	public void Import(String importFile){
		
		mWriter.println("import " + importFile + ";");
		
	}
	
	/**
	 * 
	 * This function adds the beginning of the class
	 * 
	 */
	public void StartClass(){
		
		mWriter.println("public class LA {");
		
	}
	
	/**
	 * 
	 * @param Object string, type of object you want to define (e.g. Scanner)
	 * @param name string, name of the variable
	 * @param variables string, input variables
	 * This functions creates an object (e.g. Scanner input = new Scanner(System.in);)
	 * 
	 */
	public void DefineObject(String Object, String name, String variables){
		
		mWriter.println(Object + " " + name + " = new " + Object + " ( " + variables + " );");
		
	}
	
	/**
	 * 
	 * @param fPrivate boolean, true = private, false = public
	 * @param fStatic boolean, true = static, false = non static
	 * @param fReturn string, return type
	 * @param fName string, name of the function
	 * @param fVariables string, input variables
	 * @param fText string, body of the function
	 * This function inserts a function into the document
	 * 
	 */
	public void Fuction(boolean fPrivate, boolean fStatic, String fReturn, String fName, String fVariables){
		
		String output="";
		if(fPrivate) output="private ";
		if(fStatic) output="static ";
		output=output + " " + fReturn + " " + fName + " ( " + fVariables + " ){";
		mWriter.println(output);
		
	}
	
	/**
	 * 
	 * @param variable
	 * @param start
	 * @param end
	 * @param step
	 * This function implements for loop
	 */
	public void For(String variable, int start, int end, int step){
		
		mWriter.println("for( " + variable + " = " + start + " ; " + variable + " < " + end + " ; " + variable + " += " + step + "){");
		
	}
	
	
	public void Input(String input){
		
		mWriter.println(input);
		
	}
	
	/**
	 * 
	 * This function adds }; to a function
	 * 
	 */
	public void closeFunction(){
		
		mWriter.println("};");
		
	}
	
	/**
	 * 
	 * This function adds } to a class
	 * 
	 */
	public void close(){
		
		mWriter.println("}");
		
	}
	
	/**
	 * 
	 * This function saves the file
	 * 
	 */
	public void EndFile(){
		
		mWriter.close();
		
	}
}
