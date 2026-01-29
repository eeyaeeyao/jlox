package com.lox;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

class Lox {
	private static boolean hasError = false;

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox [script]");
			System.exit(1);
		} else if (args.length == 1) {
			String path = args[0];
			runFile(path);
			if (hasError) System.exit(1);
		} else {
			runPrompt();
		}
	}

	private static void runFile(String path) throws IOException {
		byte[] source = Files.readAllBytes(Paths.get(path));
		run(new String(source, Charset.defaultCharset()));
	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		for(;;) {
			System.out.print("> ");
			String line = reader.readLine();
			if (line == null) break;
			run(line);
			hasError = false;
		}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();
		for (Token token: tokens) {
			System.out.println(token);
		}
	}

	public static void error(int line, String message) {
		System.err.println("[line " + line + "] Error: " + message);
		hasError = true;
	}
}
