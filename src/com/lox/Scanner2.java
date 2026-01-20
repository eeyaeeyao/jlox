package com.lox;

import java.util.ArrayList;
import java.util.List;

class Scanner2 implements IScanner {
	private int line = 1;

	// Start index of the current lexeme.
	private int start = 0;

	// Index of the first unconsumed character in the source string.
	private int current = 0;
	private String source;

	private List<Token> tokens;

	Scanner2(String source) {
		this.source = source;
		this.tokens = new ArrayList<>();
	}

	public List<Token> scanTokens() {
		start = current;
		while(!isAtEnd()) {
			scanToken();
		}
		tokens.add(new Token(TokenType.EOF, "", null, line));
		return tokens;
	}

	private void scanToken() {
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
			case '!':
				addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
				  break;
			case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
			case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
			case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
			case '"': string(); break;
			default:
				  if (isDigit(c)) {
					  number();
				  }else if (isAlphaNumeric(c)) {
					  identifier();
				  } else {
					  Lox.error(line, "Invalid character.");
				  }
		}
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private char advance() {
		return source.charAt(current++);
	}

	private void addToken(TokenType type) {
		addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String lexeme = source.substring(start, current);
		tokens.add(new Token(type, lexeme, literal, line));
	}

	private boolean match(char c) {
		if (isAtEnd() || (c != peek())) return false;
		advance();
		return true;
	}

	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	private void string() {
		while(peek() != '"') {
			if (peek() == '\n') ++line;
			advance();
		}

		if (peek() != '"' && isAtEnd())
			Lox.error(line, "Unterminated string.");

		advance();
		String literal = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, literal);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private void number() {
		while (isDigit(peek())) advance();

		if (peek() == '.' && isDigit(peekNext())) {
			advance();

			while(isDigit(peek)) advance();
		}

		String value = source.substring(start, current);
		double value_double = Double.parseDouble(value);
		addToken(NUMBER, value_double);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			(c >= 'A' && c <= 'Z') ||
			c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isDigit(c);
	}

	private void identifer() {
		while (isAlphaNumeric(c))
	}
}
