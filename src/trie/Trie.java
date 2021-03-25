package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {

	private Trie() { }
	
	private static String preFixfind(TrieNode focusNode, String word, String[] allWords) {
		String preFix = "";		
		String focusWord = allWords[focusNode.substr.wordIndex];
		focusWord = focusWord.substring(0, focusNode.substr.endIndex+1);
	    int minLength = Math.min(focusWord.length(), word.length());
	    for (int i = 0; i < minLength; i++) {
	        if (focusWord.charAt(i) == word.charAt(i)) {
	            preFix += focusWord.charAt(i);
	        }else {
	        	break;
	        }
	    }
		return preFix;
	}
	
	private static ArrayList<TrieNode> matchRecurse(TrieNode cur, String[] allWords, ArrayList<TrieNode> matches) {
		if(allWords[cur.substr.wordIndex].substring(0, cur.substr.endIndex+1).equals(allWords[cur.substr.wordIndex])) {
			matches.add(cur);
		}
		if(cur.sibling != null) {
			matchRecurse(cur.sibling, allWords, matches);
		} 
		if(cur.firstChild != null) {
			matchRecurse(cur.firstChild, allWords, matches);
		}
		return matches;
	}
	
	public static TrieNode buildTrie(String[] allWords) {		
		TrieNode root = new TrieNode(null, null, null);
		TrieNode cur = root;
		int index = 0;
		
		for(int wordIndex = 0; wordIndex < allWords.length; wordIndex += 0) {
			String word = allWords[wordIndex];
			if(allWords.length == 0){return root;}
			if(root.firstChild == null) {
				root.firstChild = new TrieNode(new Indexes(index,(short)0, (short)(word.length()-1)), null, null);
				wordIndex++;
			}else {
				index++;
				cur = root.firstChild;
				boolean finish = false;
				String preFix = "";		
				String prevFix = preFix;
				while(finish == false) {
					prevFix = preFix;
					preFix = preFixfind(cur, word, allWords);
					if(preFix == "" || (preFix.equals(prevFix))) {
						if(cur.sibling != null) {
							cur = cur.sibling;
						}else {
							cur.sibling = new TrieNode(new Indexes(index,(short)prevFix.length(), (short)(word.length()-1)), null, null);
							finish = true;
						}
					}else {
						if(preFix.length() < allWords[cur.substr.wordIndex].substring(0, cur.substr.endIndex+1).length()
								&& !allWords[cur.substr.wordIndex].substring(0, cur.substr.endIndex+1).equals(allWords[cur.substr.wordIndex])) {
							TrieNode temp = cur.firstChild;
							TrieNode tempFix = new TrieNode(new Indexes(cur.substr.wordIndex, (short) (preFix.length()), (short) (cur.substr.endIndex)), null, null);	
							cur.substr.endIndex = (short) (preFix.length()-1);
							cur.firstChild = tempFix;
							cur.firstChild.firstChild = temp;
							cur = cur.firstChild;
						}
						else if(cur.firstChild != null) {
							cur = cur.firstChild;
						}else {
							int saveStart = cur.substr.startIndex;
							String curWord = allWords[cur.substr.wordIndex];
							cur.firstChild = new TrieNode(new Indexes(cur.substr.wordIndex,(short)preFix.length(), (short)(curWord.length()-1)), null, null);
							cur.substr.startIndex = (short)(saveStart);
							cur.substr.endIndex = (short)(preFix.length()-1);
							cur = cur.firstChild;
							while(cur.sibling != null) {
								cur = cur.sibling;
							}
							cur.sibling = new TrieNode(new Indexes(wordIndex,(short)preFix.length(), (short)(word.length()-1)), null, null);
							finish = true;
						}
					}
				}
				wordIndex++;
			}
		}		
		return root;
	}
	 
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		ArrayList<TrieNode> matches = new ArrayList<TrieNode>();
		if(allWords.length == 0) {return null;}
		TrieNode cur = root.firstChild;
		boolean finish = false;
		
		String preFix2 = "";
		String limitFix = "";
		
		while(finish == false) {
			preFix2 = preFixfind(cur, prefix, allWords);
			if(preFix2 == "" || preFix2.equals(limitFix)) {
				if(cur.sibling == null) {return null;}
				cur = cur.sibling;
			}else {
				if(prefix.length() <= allWords[cur.substr.wordIndex].substring(0, cur.substr.endIndex+1).length()
					&& preFix2.length() == prefix.length()) {
					if(cur.firstChild == null) {
						matches.add(cur);
						return matches;
					}		
					matches = matchRecurse(cur.firstChild, allWords, matches);
					finish = true;
				}else {
					limitFix = preFix2;
					if(cur.firstChild == null) {return null;}
					cur = cur.firstChild;
				}
			}
		}
		return matches;
	}	
	
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
