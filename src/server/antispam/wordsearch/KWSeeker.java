package server.antispam.wordsearch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import server.antispam.wordsearch.entity.Keyword;
import server.antispam.wordsearch.process.DataInit;
import server.antispam.wordsearch.process.Processor;
import server.antispam.wordsearch.process.WordFinder;


/**
 * 关键词快速查找，即：从指定的文本中快速查找出包含有指定词库中的词。<br>
 * 算法简介：<br>
 * 初始化时将所有关键词聚合构造成一棵棵的关键词树（建树相当于建索引），在查询关键词时，对文本中每个字进行树查询（树查询的效率是很高的）。<br>
 * 注意：不要在使用过程中使用除analysis以外的方法！
 * 
 * @author taofucheng
 */
@SuppressWarnings({ "rawtypes" })
public class KWSeeker {
	/** 所有的关键词。 */
	private Set<Keyword> sensitiveWords = new HashSet<Keyword>();
	/** 关键词树。第一级是模块分类 */
	private Map<String, Map> wordsTree = new ConcurrentHashMap<String, Map>();
	/** 最短的关键词长度。用于对短于这个长度的文本不处理的判断，以节省一定的效率 */
	private int wordLeastLen = 0;

	private KWSeeker() {
		// 不对外开放
	}

	/**
	 * 获取一个实例
	 * 
	 * @return
	 */
	public static KWSeeker getInstance(List<Keyword> newWords) {
		KWSeeker sw = new KWSeeker();
		sw.addWord(newWords.toArray(new Keyword[] {}));
		return sw;
	}

	/**
	 * 初始化关键词的一些预处理的操作。
	 */
	private void initWords() {
		wordLeastLen = new DataInit().generalTree(sensitiveWords, wordsTree);
	}

	/**
	 * 添加一个或多个新的关键词。先对每个词进行去除前后空格，然后，如果有为null或""的元素，将不会被添加进去，其它才能被正常添加进词库。
	 * 
	 * @param newWord
	 *            新的关键词。可以是若干个字符串，也可以是字符串数组。
	 */
	public void addWord(Keyword... newWord) {
		if (newWord != null && newWord.length > 0) {
			for (Keyword sw : newWord) {
				if (StringUtils.isNotEmpty(sw.getWord())) {
					sensitiveWords.add(sw);
				}
			}
			initWords();
		}
	}

	/**
	 * 清除所有的关键词
	 */
	public void clear() {
		sensitiveWords.clear();
		initWords();
	}

	/**
	 * 是否包含指定的词
	 * 
	 * @param word
	 *            指定的关键词
	 * @return true:包含；false:不包含
	 */
	public boolean contains(String word) {
		if (sensitiveWords.isEmpty() || StringUtils.isEmpty(word)) {
			return false;
		}
		for (Keyword w : sensitiveWords) {
			if (word.equals(w.getWord())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 含有敏感的个数
	 * 
	 * @return 返回具体的数量
	 */
	public int size() {
		return sensitiveWords.size();
	}

	/**
	 * 从词库中移除指定的关键词
	 * 
	 * @param word
	 *            指定需要被移除的关键词
	 * @return true:移除成功；false:移除失败
	 */
	public boolean remove(String word) {
		if (sensitiveWords.isEmpty() || StringUtils.isEmpty(word)) {
			return false;
		}
		Set<Keyword> ws = new HashSet<Keyword>();
		for (Keyword w : sensitiveWords) {
			if (word.equals(w.getWord())) {
				ws.add(w);
			}
		}
		// 以上不直接remove的原因是：会对循环造成问题，引起bug。
		if (!ws.isEmpty()) {
			for (Keyword w : ws) {
				sensitiveWords.remove(w);
			}
			initWords();
			return true;
		}
		return false;
	}


	/**
	 * 将指定的字符串中的关键词提取出来。
	 * 
	 * @param text
	 *            指定的字符串。即：预处理的字符串
	 * @param keywords
	 *            指定使用的词库
	 * @return 返回其中所有关键词。如果没有，则返回null。
	 */
	public Set<String> findWords(String text) {
		return new WordFinder().process(wordsTree, text, wordLeastLen);
	}

	/**
	 * 使用指定的处理器进行处理！
	 * 
	 * @param proc
	 *            处理器
	 * @param text
	 *            目标文本
	 * @param fragment
	 *            命中词的处理器
	 * @return 返回处理结果
	 */
	public Object process(Processor proc, String text) {
		return proc.process(wordsTree, text, wordLeastLen);
	}
}
