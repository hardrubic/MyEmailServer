/*
 */
package server.antispam.wordsearch.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import server.antispam.wordsearch.entity.Keyword;
import server.antispam.wordsearch.utils.AnalysisUtils;

/**
 * 数据初始化处理器。主要是将敏感词解析为树状存储，这样以提高敏感词检索效率。
 * 
 * @author taofucheng
 */
@SuppressWarnings({ "rawtypes" })
public class DataInit {
	/** 生成的临时词库树。用于在最后生成的时候一次性替换，尽量减少对正在查询时的干扰 */
	private Map<String, Map> wordsTreeTmp = new HashMap<String, Map>();

	/**
	 * 构造、生成词库树。并返回所有敏感词中最短的词的长度。
	 * 
	 * @param sensitiveWords
	 *            词库
	 * @param wordsTree
	 *            聚合词库的树
	 * @return 返回所有敏感词中最短的词的长度。
	 */
	public int generalTree(Set<Keyword> sensitiveWords, Map<String, Map> wordsTree) {
		int len = 0;
		if (sensitiveWords == null || sensitiveWords.isEmpty() || wordsTree == null) {
			return len;
		}
		wordsTreeTmp.clear();
		for (Keyword w : sensitiveWords) {
			if (len == 0) {
				len = w.getWordLength();
			} else if (w.getWordLength() < len) {
				len = w.getWordLength();
			}
			AnalysisUtils.makeTreeByWord(wordsTreeTmp, w.getWord(), w);
		}
		wordsTree.clear();
		wordsTree.putAll(wordsTreeTmp);
		return len;
	}
}
