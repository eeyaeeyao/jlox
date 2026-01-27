package com.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
	private static StringBuilder indent = new StringBuilder();

	private static void addIndent() {
		indent.append("\t");
	}

	private static void removeIndent() {
		if (indent.length() == 0) return;
		indent.deleteCharAt(indent.length() - 1);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) { System.err.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}
		String outputDir = args[0];
		defineAst(
			outputDir,
			"Expr",
			Arrays.asList(
			"Binary    : Expr left, Token operator, Expr right",
			"Grouping  : Expr expression",
			"Literal   : Object value",
			"Unary     : Token operator, Expr right"));
	}

	public static void defineAst(String outputDir, String baseName, List<String> types)
	throws IOException {
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		print(writer, "package com.lox;");
		print(writer, "");
		print(writer, "import java.util.List;");
		print(writer, "");
		print(writer, "abstract class " + baseName + " {");

		addIndent();

		defineVisitor(writer, baseName, types);

		for (String type : types) {
			String[] typeString = type.split(":");
			String className = typeString[0].trim();
			String fields = typeString[1].trim();
			defineType(writer, baseName, className, fields);
		}

		print(writer, "abstract <R> R accept(Visitor<R> visitor);");
		removeIndent();
		print(writer, "}");
		writer.close();
	}

	private static void defineType(
	PrintWriter writer, String baseName, String className, String fieldList) throws IOException {
		print(writer, "static class " + className + " extends " + baseName + " {");
		addIndent();
		String[] fields = fieldList.split(", ");

		for (String field : fields) {
			print(writer, "final " + field + ";");
		}

		print(writer, "");
		print(writer, className + "(" + fieldList + ") {");
		addIndent();
		for (String field : fields) {
			String name = field.split(" ")[1];
			print(writer, "this." + name + " = " + name + ";");
		}
		removeIndent();
		print(writer, "}");

		// Visitor pattern
		print(writer, "");
		print(writer, "@Override");
		print(writer, "<R> R accept(Visitor<R> visitor) {");
		addIndent();
		print(writer, "return visitor.visit" + className + baseName + "(this);");
		removeIndent();
		print(writer, "}");

		removeIndent();
		print(writer, "}");
		print(writer, "");
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		print(writer, "interface Visitor<R> {");
		addIndent();
		for (String type: types) {
			String[] typeString = type.split(":");
			String typeName = typeString[0].trim();
			print(writer, "R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}
		removeIndent();
		print(writer, "}");
		print(writer, "");
	}

	private static void print(PrintWriter writer, String str) {
		writer.print(indent.toString());
		writer.println(str);
	}
}
