package com.lox;

import java.util.ArrayList;
import java.util.List;

class Scanner2 {
	private int line = 1;

	// Start index of the current lexeme.
	private int start = 0;

	// Index of the first unconsumed character in the source file.
	private int current = 0;
	private String source;

	private List<Token> tokens;

	Scanner2(String source) {
		this.source = source;
		this.tokens = new ArrayList<>();
	}

	public List<Token> scanTokens() {
		while(!isAtEnd()) {
			char c = advance();
			switch (c) {
				case '(': addToken(TokenType.LEFT_PAREN); break;
				case ')': addToken(TokenType.RIGHT_PAREN); break;
				case '{': addToken(TokenType.LEFT_BRACE); break;
				case '}': addToken(TokenType.RIGHT_BRACE); break;
				case ',': addToken(TokenType.COMMA); break;
				case '.': addToken(TokenType.DOT); break;
				case '-': addToken(TokenType.MINUS); break;
				case '+': addToken(TokenType.PLUS); break;
				case ';': addToken(TokenType.SEMICOLON); break;
				case '*': addToken(TokenType.STAR); break;
				default:
					  Lox.error(line, "Invalid character.");
			}
		}
		addToken(TokenType.EOF);
		return this.tokens;
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private char advance() {
		start = current;
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String lexeme = source.substring(start, current);
		tokens.add(new Token(type, lexeme, literal, line));
	}
}
