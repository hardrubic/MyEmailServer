package server.antispam.wordsearch.process;

import java.util.Map;

public interface Processor {
	/**
	 * 处理操作
	 * 
	 * @param wordsTree
	 *            词表树
	 * @param text
	 *            目标文本
	 * @param minLen
	 *            词树中最短的词的长度
	 * @return 返回处理结果
	 */
	@SuppressWarnings("rawtypes")
	public Object process(Map<String, Map> wordsTree, String text, int minLen);
}
