package com.yea.com.survive.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FileOperation {

	private static final Logger logger = LogManager.getLogger(FileOperation.class);

	public  void writeFile(String path, String sentence) {
		try {
			Files.write(Paths.get(path), sentence.getBytes(),Files.exists(Paths.get(path)) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			logger.info("File wrote to : " + path);
		} catch (IOException e) {
			logger.error("Error while writing file !", e);
		}
	}

	public  List<String> readFile(String path) {
		List<String> lines = new ArrayList<>();
		try {
			lines = Files.readAllLines(Paths.get(path));
			logger.info("File read from :" + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
	
	public void deleteFileIfExist(String path) {
		try {
			Files.deleteIfExists(Paths.get(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
