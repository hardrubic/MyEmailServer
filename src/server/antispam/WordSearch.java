package server.antispam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import server.antispam.wordsearch.KWSeeker;
import server.antispam.wordsearch.entity.Keyword;
import server.bin.EmailServerStart;

public class WordSearch {
	private static WordSearch wordSearch;
	private List<Keyword> keywordList;
	Logger logger=EmailServerStart.antispamLog;
	
	private WordSearch(){}
	
	public static WordSearch getInstance(){
		if(wordSearch==null){
			wordSearch=new WordSearch();
		}
		return wordSearch;
	}
	
	/**
	 * 初始化敏感词列表
	 */
	public void initKeywordList(){
		keywordList=new ArrayList<Keyword>();
		/*   读取文件*/
		BufferedReader br=null;
		try {
			File userListFile = new File("src/keywords.txt");
			String str=null;
			if (userListFile.exists()) {
				br=new BufferedReader(new FileReader(userListFile));
				while((str=br.readLine()) != null){
					keywordList.add(new Keyword(str));
				}
			} else {
				userListFile.createNewFile();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*
		System.out.println("打印keywords");
		for (int i = 0; i < keywordList.size(); i++) {
			System.out.println(keywordList.get(i));
		}
		*/
		
		/*   读取DB
		SessionFactory sf = DBConnectionHibernate.getSessionFactoryInstance();
		Session session = sf.openSession();
		Query query = session.createQuery("from Keyword keyword");
		keywordList=query.list();
		session.close();
		sf.close();
		*/
	}
	
	/**
	 * 敏感词检查
	 * @param text
	 * @return
	 */
	public boolean checkVocabulary(String text){
		if(keywordList==null){
			initKeywordList();
		}
		KWSeeker kw1 = KWSeeker.getInstance(keywordList);
		// 找出文本中所有敏感词！
		Set<String> s=kw1.findWords(text);
		if(s.size()==0){
			//没有找到敏感词
			return false;
		}
		logger.info("antispam:发现邮件正文存在敏感词：");
		//获取查找到的敏感词
		for (Iterator iterator = s.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			logger.info("antispam:"+string);
		}
		return true;
	}

	public static void main(String[] args) {
			List<Keyword> list = new ArrayList<Keyword>();
			list.add(new Keyword("bcd"));
			list.add(new Keyword("bcdef"));
			KWSeeker kw1 = KWSeeker.getInstance(list);
			// 添加一个词
	//		kw1.addWord(new Keyword("test3"));
			// 找出文本中所有含有上面词库中的词！
			Set<String> s=kw1.findWords("abcdefghijk");
			System.out.println(s.size());
			for (Iterator iterator = s.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				System.out.println(string);
			}
			// 使用指定的processor（如：WordFinder找出文本中所有含有上面词库中的词）对文本进行处理！
			//kw1.process(new WordFinder(), "这是tes1,要注意哦！");
		}
}
