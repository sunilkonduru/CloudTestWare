package edu.uci.testclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class ZipFileCreator {

	public void zip(String[] args, UUID testId) {

		String testJarPath = args[1];
		String[] dependentJarPaths = new String[args.length - 2];
		for (int i = 2, j = 0; i < args.length; i++, j++) {
			dependentJarPaths[j] = args[i];
		}
		
		createTempDirectoryStructureForZip(testJarPath, dependentJarPaths);
		
		createZipFile(testId, dependentJarPaths);
		
		deleteTempDirectoryStructure();
		
	}
	
	private void deleteTempDirectoryStructure() {
		try {
			FileUtils.deleteDirectory(new File("ZipContents"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createZipFile(UUID testId, String[] dependentJarPaths) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(testId + ".zip");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			ZipOutputStream zos = new ZipOutputStream(fileOutputStream);
			addToZipFile("ZipContents/test.jar" , zos);
			for (String string : dependentJarPaths) {
				String[] stringSplit = string.split("/");
				String fileName = stringSplit[stringSplit.length - 1];
				addToZipFile("ZipContents/dependent/" + fileName, zos);
			}
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createTempDirectoryStructureForZip(String testJarPath,
			String[] dependentJarPaths) {

		if(!new File("ZipContents").mkdir())
			new Exception("Directory Creation Failed");
		try {
			FileUtils.copyFile(new File(testJarPath), new File("ZipContents/test.jar"));
			for (String string : dependentJarPaths) {
				String[] stringSplit = string.split("/");
				String fileName = stringSplit[stringSplit.length - 1];
				FileUtils.copyFile(new File(string), new File("ZipContents/dependent/" + fileName));
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
	}

	private void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		File fileToBeAdded = new File(fileName);
		FileInputStream fileStream = new FileInputStream(fileToBeAdded);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fileStream.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fileStream.close();
	}
}
