package server.antispam.wordsearch.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

/**
 * 在文本中搜索到的关键词。
 * 
 * @author taofucheng
 */
@Entity
public class Keyword  {
	/** 本类中props属性对外的名称 */
	public static final String propsName = "props";
	/** 关键词内容 */
	private String word;
	private int wordLength = 0;
	/** 所有属性 */
	private Map<String, Object> props = new HashMap<String, Object>();
	private int id;

	public Keyword(){
		
	}
	public Keyword(String word) {
		setWord(word);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return StringUtils.trimToEmpty(word);
	}

	public void setWord(String word) {
		this.word = word;
		this.wordLength = word.length();
	}

	@Transient
	public int getWordLength() {
		return wordLength;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append("[word=");
		sb.append(getWord());
		sb.append("],");
		sb.append("[props=");
		sb.append(props);
		sb.append("]");
		return sb.toString();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Keyword) {
			Keyword k = (Keyword) obj;
			if (k.toString().equals(this.toString())) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * 向属性信息中增加描述
	 * 
	 * @param mapword
	 * @param string
	 */
	public void putProp(String key, String value) {
		props.put(key, value);
	}

	@Transient
	public String getStringProp(String key) {
		return (String) props.get(key);
	}

	@Transient
	public Map<String, Object> getAllProps() {
		return props;
	}
	
	
}
