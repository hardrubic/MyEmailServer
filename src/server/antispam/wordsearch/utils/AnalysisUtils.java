package server.antispam.wordsearch.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import server.antispam.wordsearch.entity.Keyword;
import server.antispam.wordsearch.entity.StopCharacter;

/**
 * 关键词分析工具类。
 * 
 * @author taofucheng
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AnalysisUtils {
	/**
	 * 构建关键词
	 * 
	 * @param word
	 *            词语
	 * @param endTag
	 *            属性信息
	 * @return
	 */
	public static Keyword getKeyword(String word, Map<String, Object> endTag) {
		Keyword tmp = new Keyword(word);
		Map<String, Object> props = (Map<String, Object>) endTag.get(Keyword.propsName);
		if (props != null && !props.isEmpty()) {
			tmp.getAllProps().putAll(props);
		}
		return tmp;
	}

	/**
	 * 将指定的词构造到一棵树中。
	 * 
	 * @param tree
	 *            构造出来的树
	 * @param word
	 *            指定的词
	 * @param keyword
	 *            对应的词
	 * @return
	 */
	public static Map<String, Map> makeTreeByWord(Map<String, Map> tree, String word, Keyword keyword) {
		word = StringUtils.trimToEmpty(word);
		if (StringUtils.isEmpty(word)) {
			tree.put(StopCharacter.TREE_END_TAG, getAttrMap(keyword));
			return tree;
		}
		String next = word.substring(0, 1);
		Map w = tree.get(next);
		if (w == null) {
			w = new HashMap<String, Map>();
		}
		tree.put(next, makeTreeByWord(w, word.substring(1), keyword));
		return tree;
	}

	/**
	 * 获取关键词的一些属性
	 * 
	 * @param word
	 *            关键词
	 * @return
	 */
	private static Map getAttrMap(Keyword word) {
		Map<String, Object> attribute = new HashMap<String, Object>();
		attribute.put(Keyword.propsName, word.getAllProps());
		return attribute;
	}

	/**
	 * 根据精确、模糊等匹配方式返回相应的实际关键词。
	 * 
	 * @param tmp
	 * @param text
	 * @return
	 */
	private static Keyword checkPattern(Keyword tmp, String pre, String sufix) {
		return tmp;
	}

	/**
	 * 查询文本开头的词是否在词库树中，如果在，则返回对应的词，如果不在，则返回null。
	 * 
	 * @param append
	 *            追加的词
	 * @param pre
	 *            词的前一个字，如果为空，则表示前面没有内容
	 * @param nextWordsTree
	 *            下一层树
	 * @param text
	 *            剩余的文本内容
	 * @return 返回找到的词
	 */
	public static Keyword getSensitiveWord(String append, String pre, Map<String, Map> nextWordsTree, String text) {
		if (nextWordsTree == null || nextWordsTree.isEmpty()) {
			return null;
		}
		Map<String, Object> endTag = nextWordsTree.get(StopCharacter.TREE_END_TAG);
		if (StringUtils.isEmpty(text)) {
			if (endTag != null) {
				// 如果有结束符，则表示匹配成功
				return checkPattern(getKeyword(append, endTag), pre, null);
			} else {
				// 没有，则返回null
				return null;
			}
		}
		String next = text.substring(0, 1);
		String suffix = null;
		if (text.length() > 0) {
			suffix = text.substring(0, 1);
		}
		Map<String, Map> nextTree = nextWordsTree.get(next);
		if (endTag == null) {
			if (nextTree != null && nextTree.size() > 0) {
				// 没有结束标志，则表示关键词没有结束，继续往下走。
				return getSensitiveWord(append + next, pre, nextTree, text.substring(1));
			} else {
				// 如果没有下一个匹配的字，表示匹配结束！
				return null;
			}
		} else {
			Keyword tmp = null;
			if (nextTree != null && nextTree.size() > 0) {
				// 如果大于0，则表示还有更长的词，继续往下找
				tmp = getSensitiveWord(append + next, pre, nextTree, text.substring(1));
				if (tmp == null) {
					// 没有更长的词，则就返回这个词。在返回之前，先判断它是模糊的，还是精确的
					tmp = getKeyword(append, endTag);
				}
				return checkPattern(tmp, pre, suffix);
			} else {
				// 没有往下的词了，那就是关键词结束了。
				return checkPattern(getKeyword(append, endTag), pre, suffix);
			}
		}
	}
}
