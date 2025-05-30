package com.nordea.filereading;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileprogramApplication implements CommandLineRunner{
	@Autowired
	FileWrapper filewrapper ;

	public static void main(String[] args) {
		SpringApplication.run(FileprogramApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(args.length== 0) {
			return ;
		}
		String filePath = args[0];
		filewrapper.loadFileDetails(filePath);
		
	}
	

}
