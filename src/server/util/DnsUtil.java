package server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xbill.DNS.*;

public class DnsUtil {
	class Mx implements Comparable<Mx> {
		String server;
		int priority;

		public Mx(String server, int priority) {
			this.server = server;
			this.priority = priority;
		}

		@Override
		public int compareTo(Mx o) {
			if (o.getPriority() > priority) {
				return 1;
			} else if (o.getPriority() == priority) {
				return 0;
			} else {
				return -1;
			}
		}
		
		public String getServer() {
			return server;
		}

		public int getPriority() {
			return priority;
		}
	}

	public static List<String> getMailServer(String domain) {
		Record[] records = null;
		try {
			records = new Lookup(domain, Type.MX).run();
		} catch (TextParseException e) {
			e.printStackTrace();
		}
		if (records == null) {
			return null;
		}
		List<Mx> serverList = new ArrayList<Mx>();
		for (int i = 0; i < records.length; i++) {
			MXRecord mx = (MXRecord) records[i];
			serverList.add(new DnsUtil().new Mx(mx.getAdditionalName()
					.toString(), mx.getPriority()));
		}
		// 按优先级排序
		Collections.sort(serverList);
		List<String> returnList = new ArrayList<String>();
		for (int i = 0; i < serverList.size(); i++) {
			returnList.add(serverList.get(i).getServer());
		}
		return returnList;
	}
	
	public static void main(String[] args) {
		List<String> list =getMailServer("126.com");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
}
