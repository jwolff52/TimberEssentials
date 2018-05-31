package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.TimberEssentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TFileReader {

	@SuppressWarnings("resource")
	public static ArrayList<String> readFile(File f) {
		if (!f.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			TimberEssentials.logger
					.warning("Error reading the file at location: "
							+ f.getName() + "\n" + e.toString());
		}
		ArrayList<String> buffer = new ArrayList<>();
		Scanner scanner = new Scanner(fis);
		while (scanner.hasNext()) {
			buffer.add(scanner.nextLine());
		}
		return buffer;
	}
}