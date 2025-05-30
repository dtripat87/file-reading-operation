package com.nordea.filereading.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.nordea.filereading.FileWrapper;

public class FileWrapperTest {
	@Autowired
	FileWrapper fileWrapper;
	@Test
	void testFileRFeader() {
		FileWrapper wrapper= mock(FileWrapper.class) ;
		String filepath = "C:\\Users\\HP\\Desktop\\Debasish\\small.txt";
		wrapper.loadFileDetails(filepath);
		

	}

}
