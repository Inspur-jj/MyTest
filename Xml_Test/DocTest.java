package zipTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class DocTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws DocumentException, IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("xml.xml"))));
		String text = "";
		StringBuilder xml = new StringBuilder();
		while ((text = in.readLine()) != null) {
			System.out.println(text);
			xml.append(text);
		}
		in.close();
		Document doc = DocumentHelper.parseText(xml.toString());
		Node patentElement = doc.selectSingleNode("/data/pub_thesis");
		if (patentElement.selectNodes("archive_file").size() <= 0) {
			// 表示这是老记录 没有archive_file这个节点
			((Element) patentElement).addElement("archive_file");
			Element archive_file = (Element) doc.selectSingleNode("/data/pub_thesis/archive_file");
			archive_file.addElement("test").addText("AJDAISHDAHNFLKAJ");

			System.out.println("测试后");
			System.out.println(doc.asXML());
		} else {
			Element archive_file = (Element) doc.selectSingleNode("/data/pub_thesis/archive_file");
			List<Element> existFiles = archive_file.elements("file");
			String maxSeq = "1";
			if (existFiles.size() > 0) {
				for (Element tempFile : existFiles) {
					String seq_no = tempFile.attributeValue("seq_no");
					Attribute seq = tempFile.attribute("seq_no");
					String oldName = tempFile.elementText("filename");
					if (oldName.equals("ALSKDJALSKDJLK")) {// 用新的覆盖掉旧的
						System.out.println(archive_file.remove(tempFile));
					}

					System.out.println(doc.asXML());
					if (maxSeq.compareTo(seq_no) < 0) {
						maxSeq = seq_no;
					}
				}
				maxSeq = Integer.parseInt(maxSeq) + 1 + "";
			}

			Element fileNode = archive_file.addElement("file");
			fileNode.addAttribute("seq_no", maxSeq);
			fileNode.addElement("file_name");
			fileNode.addElement("file_code");
			fileNode.addElement("file_remark");
		}
	}
}
