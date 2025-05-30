package com.nordea.filereading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nordea.filereading.Model.Sentence;
import com.nordea.filereading.constant.FileConstant;
@Component
public class FileWrapper {
	private static final Logger logger = Logger.getLogger(FileWrapper.class.getName());

	public void loadFileDetails(String filePath) {
		try {
			//String content = readFile(FileConstant.INPUTFILE);
			String content = readFile(filePath);
			logger.info("content value is "+content);
			Map <Sentence,List<String>> sentenceMap = processText(content);
			writeToCSV(sentenceMap,FileConstant.CSVOUTPUT);
			writeToXML(sentenceMap,FileConstant.XMLOUTPUT);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	// Read the input file line by line
	private static String readFile(String filePath) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath));
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append(" ");
		}
		return sb.toString();
	}

	private static Map<Sentence, List<String>> processText(String text) {
		Map<Sentence, List<String>> wordMap = new LinkedHashMap<Sentence, List<String>>();
		// split the sentences to words
		String[] rawSentence = text.split("[.!?]\\s*");
		for (String raw : rawSentence) {
			String sentence = raw.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
			if (!sentence.isEmpty()) {
				// split the words from the space and adding into list
				List<String> words = Arrays.asList(sentence.split("\\s+"));
				Collections.sort(words,String.CASE_INSENSITIVE_ORDER);
				Sentence objSentence = new Sentence(sentence);
				wordMap.put(objSentence, words);
				logger.info("map value is" +wordMap.get(objSentence));
			}
		}
		
		return wordMap;
	}

	// write to CSV file
	public static void writeToCSV(Map<Sentence, List<String>> map, String fipePath) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fipePath));
		for (Map.Entry<Sentence, List<String>> entry : map.entrySet()) {
			writer.write("\"" + entry.getKey().getSentence() + "\"");
			writer.write(" , ");
			writer.write(String.join(",", entry.getValue()));
			writer.newLine();
		}
		writer.close();
	}

	// write to xml file
	private static void writeToXML(Map<Sentence, List<String>> map, String filePath)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element root = document.createElement("sentences");
		//appending child  element to root node
		document.appendChild(root);

		for (Map.Entry<Sentence, List<String>> entry : map.entrySet()) {
			Element sentenceElement = document.createElement("sentence");
			sentenceElement.setAttribute("text", entry.getKey().getSentence());

			for (String word : entry.getValue()) {
				Element wordElement = document.createElement("word");
				wordElement.appendChild(document.createTextNode(word));
				sentenceElement.appendChild(wordElement);
			}
			root.appendChild(sentenceElement);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "Yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}intdent-amount","2");

		DOMSource source = new DOMSource(document);
		StreamResult Streamresult = new StreamResult(new File(filePath));
		transformer.transform(source, Streamresult);

	}

}
