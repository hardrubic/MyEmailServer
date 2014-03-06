package server.antispam.wordsearch.process;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import server.antispam.wordsearch.entity.Keyword;
import server.antispam.wordsearch.utils.AnalysisUtils;

/**
 * 对文本中的关键词进行提取。主要根据关键词对文本中的关键词进行提取！
 * 
 * @author taofucheng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class WordFinder implements Processor {
	/**
	 * 将文本中的关键词提取出来。
	 * 
	 * @param wordsTree
	 *            关键词库树
	 * @param text
	 *            待处理的文本
	 * @return 返回提取的关键词或null
	 */
	public Set<String> process(Map<String, Map> wordsTree, String text, int minLen) {
		Set<String> collections = new HashSet<String>();
		String pre = null;// 词的前面一个字
		while (true) {
			if (wordsTree == null || wordsTree.isEmpty() || StringUtils.isEmpty(text)) {
				return collections;
			}
			if (text.length() < minLen) {
				return collections;
			}
			String chr = text.substring(0, 1);
			text = text.substring(1);
			Map<String, Map> nextWord = wordsTree.get(chr);
			if (nextWord == null) {
				// 没有对应的下一个字，表示这不是关键词的开头，进行下一个循环
				pre = chr;
				continue;
			} else {
				Keyword w = AnalysisUtils.getSensitiveWord(chr, pre, nextWord, text);
				if (w == null) {
					// 开头没有关键词，下一个循环
					pre = chr;
					continue;
				} else {
					collections.add(w.getWord());
					text = text.substring(w.getWordLength() - 1);
					pre = w.getWord().substring(w.getWordLength() - 1, w.getWordLength());
					// 跳过当前的词，进行下一个循环
					continue;
				}
			}
		}
	}
}
