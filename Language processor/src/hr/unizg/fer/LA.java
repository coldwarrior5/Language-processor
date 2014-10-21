package hr.unizg.fer;

import java.util.List;
import java.util.ArrayList;

public class LA{

	private static String mInput;
	private static int mCurrentLine;
	private static String mCurrentState;
	private static String mInitialState = "S_pocetno";
	private static int mLastProcessedPos;
	private static int mReaderPos;
	private static List<LexicalRule> mLexRules = new ArrayList<LexicalRule>();
	private static List<LA_OutputElement> mOutput = new ArrayList<LA_OutputElement>();

	private static void Initialize(){
		LexicalRule temp;
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("\\t|\\_");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("\\n");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = true;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("#\\|");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = false;
		temp.mGoToState = true;
		temp.mGoToStateName = "S_komentar";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_komentar";
		temp.mENFA = new eNFA("\\|#");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = false;
		temp.mGoToState = true;
		temp.mGoToStateName = "S_pocetno";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_komentar";
		temp.mENFA = new eNFA("\\n");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = true;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_komentar";
		temp.mENFA = new eNFA("(\\(|\\)|\\{|\\}|\\||\\*|\\\\|\\$|\\t|\\n|\\_|!|\"|#|%|&|'|+|,|-|.|/|0|1|2|3|4|5|6|7|8|9|:|;|<|=|>|?|@|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|[|]|^|_|`|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|~)");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("((0|1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)*|0x((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)*)");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "OPERAND";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("\\(");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "LIJEVA_ZAGRADA";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("\\)");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "DESNA_ZAGRADA";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("-");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "OP_MINUS";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("-(\\t|\\n|\\_)*-");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "OP_MINUS";
		temp.mNewLine = false;
		temp.mGoToState = true;
		temp.mGoToStateName = "S_unarni";
		temp.mReturn = true;
		temp.mReturnAt = 1;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_pocetno";
		temp.mENFA = new eNFA("\\((\\t|\\n|\\_)*-");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "LIJEVA_ZAGRADA";
		temp.mNewLine = false;
		temp.mGoToState = true;
		temp.mGoToStateName = "S_unarni";
		temp.mReturn = true;
		temp.mReturnAt = 1;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_unarni";
		temp.mENFA = new eNFA("\\t|\\_");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_unarni";
		temp.mENFA = new eNFA("\\n");
		temp.mDiscardString = true;
		temp.mLexicalTokenName = "null";
		temp.mNewLine = true;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_unarni";
		temp.mENFA = new eNFA("-");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "UMINUS";
		temp.mNewLine = false;
		temp.mGoToState = true;
		temp.mGoToStateName = "S_pocetno";
		temp.mReturn = false;
		temp.mReturnAt = 0;
		mLexRules.add(temp);
		temp = new LexicalRule();
		temp.mLexicalState = "S_unarni";
		temp.mENFA = new eNFA("-(\\t|\\n|\\_)*-");
		temp.mDiscardString = false;
		temp.mLexicalTokenName = "UMINUS";
		temp.mNewLine = false;
		temp.mGoToState = false;
		temp.mGoToStateName = "null";
		temp.mReturn = true;
		temp.mReturnAt = 1;
		mLexRules.add(temp);
	}

	private static void ApplyRule(int ruleIndex){
		LA_OutputElement temp = new LA_OutputElement();
		temp.mLine = mCurrentLine;
		temp.mUniformToken = mLexRules.get(ruleIndex).mLexicalTokenName;
		if (mLexRules.get(ruleIndex).mNewLine) ++mCurrentLine;
		if (mLexRules.get(ruleIndex).mGoToState) mCurrentState = mLexRules.get(ruleIndex).mGoToStateName;
		if (mLexRules.get(ruleIndex).mReturn){
			temp.mLexicUint = mInput.substring(mLastProcessedPos + 1, mLastProcessedPos + mLexRules.get(ruleIndex).mReturnAt + 1);
			mLastProcessedPos += mLexRules.get(ruleIndex).mReturnAt;
		}
		else{
			temp.mLexicUint = mInput.substring(mLastProcessedPos + 1, mReaderPos + 1);
			mLastProcessedPos = mReaderPos;
		}
		if (!mLexRules.get(ruleIndex).mDiscardString) mOutput.add(temp);
	}

	public static void main(String[] args){
		mCurrentState = mInitialState;
		mLastProcessedPos = -1;
		mCurrentLine = 1; // We start with first line of code.
		Initialize();

		// Now the input and analysis.
		mInput = UtilitiesLA.ReadStringFromInput();
		while(mLastProcessedPos < mInput.length() - 1){
			mReaderPos = mLastProcessedPos + 1;
			for(int i = 0; i < mLexRules.size(); ++i) mLexRules.get(i).mENFA.Reset(); // Resets the eNFAs for new lexic unit analysis.
			int bestRuleToApplySoFar = Integer.MAX_VALUE;
			Boolean canApplyRule;
			int tempReaderPos = mReaderPos;
			do{ // loops for each character from mReaderPos to the end.
				int bestRuleToApply = Integer.MAX_VALUE;
				canApplyRule = false;
				for(int i = 0; i < mLexRules.size(); ++i){
					mLexRules.get(i).mENFA.InputChar(mInput.charAt(tempReaderPos));
					if (mLexRules.get(i).mENFA.IsInAcceptableState() && mLexRules.get(i).mLexicalState.equals(mCurrentState)){
						canApplyRule = true;
						if (i < bestRuleToApply) bestRuleToApply = i;
					}
				}
				if (canApplyRule){
					bestRuleToApplySoFar = bestRuleToApply;
					mReaderPos = tempReaderPos;
				}
				++tempReaderPos;
			}while(tempReaderPos < mInput.length());

			if (bestRuleToApplySoFar != Integer.MAX_VALUE)ApplyRule(bestRuleToApplySoFar);
			else mLastProcessedPos += 1; // error recovery
		}

		// We are done, just write that shit.
		for (int i = 0; i < mOutput.size(); ++i) 
			System.out.println(mOutput.get(i).mUniformToken + " " + mOutput.get(i).mLine + " " + mOutput.get(i).mLexicUint);

	}
}