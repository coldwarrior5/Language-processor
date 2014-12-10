package hr.unizg.fer.lab3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.directory.InvalidAttributesException;

class Constant{
	
	public final char c;
	public final Integer x;
	
	public  Constant(String in) throws InvalidAttributesException{		
		if(SemantickiAnalizator.checkInt(in)){
			this.x = Integer.parseInt(in);
			this.c = '\0';
		}		
		else if(in.length() == 1){
			this.c = in.charAt(0);
			this.x = null;
		}		
		else{
			throw new InvalidAttributesException();
		}		
	}
	
	public String get(){
		if(this.x != null){
			return this.x.toString();
		}		
		else{
			return this.c + "";
		}
	}
}

class Array{
	List <Object> list = new ArrayList<Object>();
	
	public void add(Object add) throws IllegalArgumentException{
		
		if(add instanceof Constant){
			list.add(add);
		}
		else if(SemantickiAnalizator.checkInt((String) add)){
			list.add(add);
		}
		else if(add.toString().length()==1){
			list.add(add);
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	public Iterator<Object> iterator(){
		return list.iterator();
	}
	
}

public class SemantickiAnalizator {
	
	protected static boolean checkInt(String in){
		try{
			Integer.parseInt(in);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		
		Boolean jo = Utilities.ProvjeriNizConstChar("hau\\\"odfh\\nsf");
		
		StablastaTablicaZnakova stz = new StablastaTablicaZnakova();
		Parser p = new Parser();
		Izrazi.mSTZ = NaredbenaStrukturaPrograma.mSTZ = DeklaracijeIDefinicije.mSTZ = stz;
		Izrazi.mParser = NaredbenaStrukturaPrograma.mParser = DeklaracijeIDefinicije.mParser = p;
		
		System.out.println("Checking if checkInt works");
		boolean check=checkInt("1223");
		System.out.println(check);
		System.out.println("Checking if Constant works");
		List<Constant> ListOfConstants = new ArrayList<Constant>();
		try {
			ListOfConstants.add(new Constant("1223"));
		} catch (InvalidAttributesException e) {
		}
		try {
			ListOfConstants.add(new Constant("a"));
		} catch (InvalidAttributesException e) {
		}
		try {
			ListOfConstants.add(new Constant("cool"));
		} catch (InvalidAttributesException e) {
		}
		Iterator<Constant> itr = ListOfConstants.iterator();
		while(itr.hasNext()) {
	         Constant element = itr.next();
	         System.out.println(element.get());
	      }
		System.out.println("Lets now try arrays");
		Array newArray = new Array();
		newArray.add("s");
		newArray.add("144556");
		try {
			newArray.add(new Constant("a"));
		} catch (InvalidAttributesException e) {
		}
		try{
			newArray.add("banana");
		}catch(IllegalArgumentException e){
			
		}
		Iterator<Object> i = newArray.iterator();
		while(i.hasNext()) {
	         Object element = i.next();
	         if (element instanceof Constant){
	        	 element=((Constant) element).get();
	         }
	         System.out.println(element);
	      }
	}
}
