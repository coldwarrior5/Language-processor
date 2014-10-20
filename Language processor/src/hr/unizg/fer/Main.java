package hr.unizg.fer;

public class Main {
	/**
	 * @author Bojan
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		Parser parser = new Parser("D:\\FER\\PPJ\\Lab1\\07_regex_regdefs\\test.lan");
		
		LAG lag = new LAG();
		lag.AddImport("java.util.List");
		lag.AddImport("java.util.ArrayList");
		
		lag.AddVariable("String mInput;");
		lag.AddVariable("int mCurrentLine;");
		lag.AddVariable("String mCurrentState;");
		// First state written after %X is the initial state.
		lag.AddVariable("String mInitialState = \"" + parser.GetStateList().get(0).mStateName + "\";");
		lag.AddVariable("int mLastProcessedPos;");
		lag.AddVariable("int mReaderPos;");
		lag.AddVariable("List<LexicalRule> mLexRules = new ArrayList<LexicalRule>();");
		lag.AddVariable("List<LA_OutputElement> mOutput = new ArrayList<LA_OutputElement>();");
		
		String fBody = "LexicalRule temp;\n";
		for (int i = 0; i < parser.GetLexicalRuleList().size(); ++i){
			fBody += "temp = new LexicalRule();\n";
			fBody += "temp.mLexicalState = \"" 
					+ parser.GetLexicalRuleList().get(i).mLexicalState + "\";\n";
			fBody += "temp.mENFA = new eNFA(\"" 
					+ parser.GetLexicalRuleList().get(i).mRegEx + "\");\n";
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
		lag.AddFunction("void", "Initialize", "", fBody);
		lag.AddFunction("void", "ApplyRule", "int ruleIndex",
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
		
		lag.AddInMain("mCurrentState = mInitialState;");
		lag.AddInMain("mLastProcessedPos = -1;");
		lag.AddInMain("mCurrentLine = 1; // We start with first line of code.");
		lag.AddInMain("Initialize();");
		lag.AddEmptyLineInMain();
			
		// Now the input and analysis.
		lag.AddInMain("// Now the input and analysis.");
		lag.AddInMain("mInput = UtilitiesLA.ReadStringFromInput();");
		lag.AddInMain("while(mLastProcessedPos < mInput.length() - 1){");
		lag.AddInMain("	mReaderPos = mLastProcessedPos + 1;");
		lag.AddInMain("	for(int i = 0; i < mLexRules.size(); ++i) mLexRules.get(i).mENFA.Reset(); // Resets the eNFAs for new lexic unit analysis.");
		lag.AddInMain("	int bestRuleToApplySoFar = Integer.MAX_VALUE;");
		lag.AddInMain("	Boolean canApplyRule;");
		lag.AddInMain("	int tempReaderPos = mReaderPos;");
		lag.AddInMain("	do{ // loops for each character from mReaderPos to the end.");
		lag.AddInMain("		int bestRuleToApply = Integer.MAX_VALUE;");
		lag.AddInMain("		canApplyRule = false;");
		lag.AddInMain("		for(int i = 0; i < mLexRules.size(); ++i){");
		lag.AddInMain("			mLexRules.get(i).mENFA.InputChar(mInput.charAt(tempReaderPos));");
		lag.AddInMain("			if (mLexRules.get(i).mENFA.IsInAcceptableState() && mLexRules.get(i).mLexicalState.equals(mCurrentState)){");
		lag.AddInMain("				canApplyRule = true;");
		lag.AddInMain("				if (i < bestRuleToApply) bestRuleToApply = i;");
		lag.AddInMain("			}");
		lag.AddInMain("		}");
		lag.AddInMain("		if (canApplyRule){");
		lag.AddInMain("			bestRuleToApplySoFar = bestRuleToApply;");
		lag.AddInMain("			mReaderPos = tempReaderPos;");
		lag.AddInMain("		}");
		lag.AddInMain("		++tempReaderPos;");
		lag.AddInMain("	}while(tempReaderPos < mInput.length());");
		lag.AddEmptyLineInMain();
		lag.AddInMain("	if (bestRuleToApplySoFar != Integer.MAX_VALUE)ApplyRule(bestRuleToApplySoFar);");
		lag.AddInMain("	else mLastProcessedPos += 1; // error recovery");
		lag.AddInMain("}");
		lag.AddEmptyLineInMain();
		lag.AddInMain("// We are done, just write that shit.");
		lag.AddInMain("for (int i = 0; i < mOutput.size(); ++i) ");
		lag.AddInMain("	System.out.println(mOutput.get(i).mUniformToken + \" \" + mOutput.get(i).mLine + \" \" + mOutput.get(i).mLexicUint);");
		
		// writes the generated program to a file.
		lag.Write("src/hr/unizg/fer/");
	}
}
