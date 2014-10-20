package hr.unizg.fer;

public class GLA {
	
	/**
	 * Fixes the input string in a way so that each '\' gets copied next to it.
	 * '\' --> "\\"
	 * @author Bojan
	 * @param s - input string
	 */
	private static String FixString(String s){
		
		StringBuilder sb = new StringBuilder(s);
		
		for (int i = 0; i < sb.length(); ++i)
			if (sb.charAt(i) == '\\'){
				sb.insert(i, '\\');
				++i;
			}
		
		return sb.toString();
	}
	
	/**
	 * Entry point for of this program.
	 * @author Bojan
	 * @param args
	 */
	public static void main(String[] args) {
		Parser parser = new Parser();
		
		SourceCodeBuilder scb = new SourceCodeBuilder();
		scb.AddImport("java.util.List");
		scb.AddImport("java.util.ArrayList");
		
		scb.AddVariable("String mInput;");
		scb.AddVariable("int mCurrentLine;");
		scb.AddVariable("String mCurrentState;");
		// First state written after %X is the initial state.
		scb.AddVariable("String mInitialState = \"" + parser.GetStateList().get(0).mStateName + "\";");
		scb.AddVariable("int mLastProcessedPos;");
		scb.AddVariable("int mReaderPos;");
		scb.AddVariable("List<LexicalRule> mLexRules = new ArrayList<LexicalRule>();");
		scb.AddVariable("List<LA_OutputElement> mOutput = new ArrayList<LA_OutputElement>();");
		
		String fBody = "LexicalRule temp;\n";
		for (int i = 0; i < parser.GetLexicalRuleList().size(); ++i){
			fBody += "temp = new LexicalRule();\n";
			fBody += "temp.mLexicalState = \"" 
					+ parser.GetLexicalRuleList().get(i).mLexicalState + "\";\n";
			fBody += "temp.mENFA = new eNFA(\"" 
					+ FixString(parser.GetLexicalRuleList().get(i).mRegEx) + "\");\n";
			fBody += "temp.mDiscardString = " 
					+ parser.GetLexicalRuleList().get(i).mDiscardString + ";\n";
			fBody += "temp.mLexicalTokenName = \"" 
					+ parser.GetLexicalRuleList().get(i).mLexicalTokenName + "\";\n";
			fBody += "temp.mNewLine = " 
					+ parser.GetLexicalRuleList().get(i).mNewLine + ";\n";
			fBody += "temp.mGoToState = " 
					+ parser.GetLexicalRuleList().get(i).mGoToState + ";\n";
			fBody += "temp.mGoToStateName = \"" 
					+ parser.GetLexicalRuleList().get(i).mGoToStateName + "\";\n";
			fBody += "temp.mReturn = " 
					+ parser.GetLexicalRuleList().get(i).mReturn + ";\n";
			fBody += "temp.mReturnAt = " 
					+ parser.GetLexicalRuleList().get(i).mReturnAt + ";\n";
			
			fBody += "mLexRules.add(temp);\n";
		}
		scb.AddFunction("void", "Initialize", "", fBody);
		scb.AddFunction("void", "ApplyRule", "int ruleIndex",
				"LA_OutputElement temp = new LA_OutputElement();\n"
				+ "temp.mLine = mCurrentLine;\n"
				+ "temp.mUniformToken = mLexRules.get(ruleIndex).mLexicalTokenName;\n"
				+ "if (mLexRules.get(ruleIndex).mNewLine) ++mCurrentLine;\n"
				+ "if (mLexRules.get(ruleIndex).mGoToState) mCurrentState = mLexRules.get(ruleIndex).mGoToStateName;\n"
				+ "if (mLexRules.get(ruleIndex).mReturn){\n"
				+ "	temp.mLexicUint = mInput.substring(mLastProcessedPos + 1, mLastProcessedPos + mLexRules.get(ruleIndex).mReturnAt + 1);\n"
				+ "	mLastProcessedPos += mLexRules.get(ruleIndex).mReturnAt;\n"
				+ "}\n"
				+ "else{\n"
				+ "	temp.mLexicUint = mInput.substring(mLastProcessedPos + 1, mReaderPos + 1);\n"
				+ "	mLastProcessedPos = mReaderPos;\n"
				+ "}\n"
				+ "if (!mLexRules.get(ruleIndex).mDiscardString) mOutput.add(temp);");
		
		scb.AddInMain("mCurrentState = mInitialState;");
		scb.AddInMain("mLastProcessedPos = -1;");
		scb.AddInMain("mCurrentLine = 1; // We start with first line of code.");
		scb.AddInMain("Initialize();");
		scb.AddEmptyLineInMain();
			
		// Now the input and analysis.
		scb.AddInMain("// Now the input and analysis.");
		scb.AddInMain("mInput = UtilitiesLA.ReadStringFromInput();");
		scb.AddInMain("while(mLastProcessedPos < mInput.length() - 1){");
		scb.AddInMain("	mReaderPos = mLastProcessedPos + 1;");
		scb.AddInMain("	for(int i = 0; i < mLexRules.size(); ++i) mLexRules.get(i).mENFA.Reset(); // Resets the eNFAs for new lexic unit analysis.");
		scb.AddInMain("	int bestRuleToApplySoFar = Integer.MAX_VALUE;");
		scb.AddInMain("	Boolean canApplyRule;");
		scb.AddInMain("	int tempReaderPos = mReaderPos;");
		scb.AddInMain("	do{ // loops for each character from mReaderPos to the end.");
		scb.AddInMain("		int bestRuleToApply = Integer.MAX_VALUE;");
		scb.AddInMain("		canApplyRule = false;");
		scb.AddInMain("		for(int i = 0; i < mLexRules.size(); ++i){");
		scb.AddInMain("			mLexRules.get(i).mENFA.InputChar(mInput.charAt(tempReaderPos));");
		scb.AddInMain("			if (mLexRules.get(i).mENFA.IsInAcceptableState() && mLexRules.get(i).mLexicalState.equals(mCurrentState)){");
		scb.AddInMain("				canApplyRule = true;");
		scb.AddInMain("				if (i < bestRuleToApply) bestRuleToApply = i;");
		scb.AddInMain("			}");
		scb.AddInMain("		}");
		scb.AddInMain("		if (canApplyRule){");
		scb.AddInMain("			bestRuleToApplySoFar = bestRuleToApply;");
		scb.AddInMain("			mReaderPos = tempReaderPos;");
		scb.AddInMain("		}");
		scb.AddInMain("		++tempReaderPos;");
		scb.AddInMain("	}while(tempReaderPos < mInput.length());");
		scb.AddEmptyLineInMain();
		scb.AddInMain("	if (bestRuleToApplySoFar != Integer.MAX_VALUE)ApplyRule(bestRuleToApplySoFar);");
		scb.AddInMain("	else mLastProcessedPos += 1; // error recovery");
		scb.AddInMain("}");
		scb.AddEmptyLineInMain();
		scb.AddInMain("// We are done, just write that shit.");
		scb.AddInMain("for (int i = 0; i < mOutput.size(); ++i) ");
		scb.AddInMain("	System.out.println(mOutput.get(i).mUniformToken + \" \" + mOutput.get(i).mLine + \" \" + mOutput.get(i).mLexicUint);");
		
		// writes the generated program to a file.
		scb.Write("src/hr/unizg/fer/");		//works for terminal
		/*log.Write("src/hr/unizg/fer/");			//works for Eclipse
		 * 
		 */
	}
}
