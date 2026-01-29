package com.lox;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class Scanner {
	private String source;
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private List<Token> tokens = new ArrayList<>();
	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("and", TokenType.AND);
		keywords.put("class", TokenType.CLASS);
		keywords.put("else", TokenType.ELSE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("fun", TokenType.FUN);
		keywords.put("for", TokenType.FOR);
		keywords.put("if", TokenType.IF);
		keywords.put("nil", TokenType.NIL);
		keywords.put("or", TokenType.OR);
		keywords.put("print", TokenType.PRINT);
		keywords.put("return", TokenType.RETURN);
		keywords.put("super", TokenType.SUPER);
		keywords.put("this", TokenType.THIS);
		keywords.put("true", TokenType.TRUE);
		keywords.put("var", TokenType.VAR);
		keywords.put("while", TokenType.WHILE);
	}

	public Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}
		tokens.add(new Token(TokenType.EOF, "", null, line));
		return tokens;
	}

	private void scanToken() {
		char c = advance();
		switch(c) {
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
			case '=':
				 addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
				  break;
			case '>':
				 addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
				  break;
			case '<':
				 addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
				  break;
			case '"':
				  string();
				  break;
			case ' ':
			case '\t':
			case '\r':
				  break;
			case '\n': line++; break;
			default:
				   if (isNumeric(c)) {
					   number();
				   } else if (isAlphaNumeric(c)) {
					   identifier();
				   } else {
					   Lox.error(line, "Unexpected character: " + c);
				   }
				   break;
		}
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

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private boolean match(char c) {
		if (peek() != c) return false;
		advance();
		return true;
	}

	private char peek() {
		if (isAtEnd()) return '\0';
		return source.charAt(current);
	}

	private char peekNext() {
		if (isAtEnd()) return '\0';
		if (current + 1 >= source.length()) return '\0';
		return source.charAt(current + 1);
	}

	private void string() {
		while (peek() != '"' && !isAtEnd()) {
			if(advance() == '\n') ++line;
		}

		if (isAtEnd()) {
			Lox.error(line, "Unterminated string.");
			return;
		}
		advance();
		String literal = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, literal);
	}

	private boolean isNumeric(char c) {
		return c >= '0' && c <= '9';
	}

	private void number() {
		while (isNumeric(peek()) && !isAtEnd()) {
			advance();
		}

		if (peek() == '.' && isNumeric(peekNext())) {
			advance();
			while (isNumeric(peek()) && !isAtEnd())
				advance();
		}

		String lexeme = source.substring(start, current);
		double literal = Double.parseDouble(lexeme);
		addToken(TokenType.NUMBER, literal);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			(c >= 'A' && c <= 'Z') ||
			(c == '_');
	}

	private boolean isAlphaNumeric(char c) {
		return isAlpha(c) || isNumeric(c);
	}

	private void identifier() {
		while (isAlphaNumeric(peek()) && !isAtEnd()) {
			advance();
		}

		String lexeme = source.substring(start, current);
		TokenType type = keywords.get(lexeme);
		if (type == null) type = TokenType.IDENTIFIER;
		addToken(type);
	}
}
