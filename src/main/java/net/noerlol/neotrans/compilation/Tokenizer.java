package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.Main;
import net.noerlol.neotrans.api.APIOnly;
import net.noerlol.neotrans.utils.*;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

public class Tokenizer {
    // Settings
    private int TAB_LENGTH = 4;
    private final String fileName;

    // stuff
    private StringBuilder TOKENIZED = new StringBuilder();
    private int lineNumber = 1;
    private int errors = 0;
    private int warnings = 0;

    private final Preprocessor preprocessor = new Preprocessor();

    // Finals
    private final String[] SYSTEM_IMPORTS = {
            "dbm.bot.BotProvider",
            "dbm.bot.Bot",
            "dbm.bot.Context",
            "dbm.bot.Command",
            "java.lang.String", // Replaced because the preprocessor replaces dbm.lang* with java.lang*
            "dbm.util.Project",
            "dbm."
    }; // Todo: Package Manager stuffs


    // Maps
    private Map<String, Map<String, String>> variables = new HashMap<>();
    private Map<String, String> functions = new HashMap<>();

    // Buffers
    private String b_FunctionName = "";
    private String b_FunctionCode = "";
    private ArrayList<Map<String, String>> b_FunctionParameters = new ArrayList<>();

    private boolean expectingIfElseElseIf = false;
    private boolean expectingFunction = false;
    private boolean expectingCommentBlock = false;

    private Stack<String> scopeStack = new Stack<>();

    public Tokenizer(int TAB_LENGTH, String fileName) {
        this.TAB_LENGTH = TAB_LENGTH;
        this.fileName = fileName;
    }

    public TokenizedCode parse(String[] lines) {
        return parse(lines, System.err);
    }

    /**
     * Compiles code into Tokenized Code
     * @param lines The lines of code
     * @param errorWriter The error writer to write errors to
     * @return Tokenized Code
     */
    public TokenizedCode parse(String[] lines, PrintStream errorWriter) {
        for (String line : lines) {
            line = preprocessor.processLine(line, errorWriter, fileName);

            // Finally,
            parseLine(line, errorWriter);
        }
        return parseEnd(errorWriter);
    }

    /**
     * Only used by API users & this class
     * @param errorWriter The PrintStream to be used to write errors to
     * @return The Tokenized code
     */
    @APIOnly
    public TokenizedCode parseEnd(PrintStream errorWriter) {
        if (expectingFunction || expectingIfElseElseIf) {
            ErrorFixSuggester.fix("}", '}', errorWriter, lineNumber, fileName);
            errorWriter.println("syntax-error: " + "closing brace not found" + System.lineSeparator());
            errors++;
        } if (!functions.containsKey("fn main() {")) {
            ErrorFixSuggester.fix("fn main() { ... }", "fn main() { ... }", errorWriter, lineNumber, fileName);
            errorWriter.println("syntax-error: " + "main function not found" + System.lineSeparator());
            errors++;
        } if (errors > 0 || warnings > 0) {
            errorWriter.println("errors generated: " + errors);
            errorWriter.println("warnings generated: " + warnings);
            if (Main.args.isEnabled("Cexit-on-warn", true)) {
                System.exit(2);
            }
        } if (errors > 0) {
            if (!Main.args.isEnabled("Cno-exit-on-error", true)) {
                System.exit(1);
            }
        }

        return new TokenizedCode(TOKENIZED.toString(), TAB_LENGTH);
    }

    @APIOnly
    public String findReplaceMacro(String line, PrintStream errorWriter, String fileName) {
        return preprocessor.processLine(line, errorWriter, fileName);
    }

    @APIOnly
    public void parseLine(String str, PrintStream errorWriter) {
        boolean functionMake = false, variable = false, statement = false, functionUse = false,
                whitespace = false, scopeEnd = false, comment = false, stdlib_function = false,
                _if = false, _elseIf = false, _else = false, _import = false, export = false,
                stringconcat = false, scopeStart = false, commentBlock = false, commentBlockEnd = false,
                ignore = false;

        str = str.replace("\t", " ").trim();

        // Checks
        if (str.startsWith("fn ")) {
            functionMake = true;
        } else if (str.startsWith("var ")) {
            variable = true;
        } else if (str.isEmpty()) {
            whitespace = true;
        } else if (str.contains("}")) {
            scopeEnd = true;
        } else if (str.startsWith("//")) {
            comment = true;
        } else if (str.startsWith("/*")) {
            commentBlock = true;
        } else if (str.endsWith("*/")) {
            commentBlockEnd = true;
        } else if (str.contains("println") || str.contains("inputln") || str.contains("print") || str.contains("printerr") || str.contains("printlnerr")) {
            stdlib_function = true;
        } else if (str.startsWith("import")) {
            _import = true;
        } else if (str.contains("if") && !str.contains("else")) {
            _if = true;
        } else if (str.contains("if") && str.contains("else")) {
            _elseIf = true;
        } else if (!str.contains("if") && str.contains("else")) {
            _else = true;
        } else if (str.contains("(") && str.contains(")")) {
            functionUse = true;
        } else if (str.startsWith("export")) {
            export = true;
        } else if (Pattern.compile("\\+").matcher(str).matches()) {
            stringconcat = true;
        } else if (str.endsWith("{")) {
            scopeStart = true;
        } else if (str.startsWith("_")) {
            ignore = true;
        } else {
            statement = true;
        }

        if (stdlib_function) {
            if (Main.args.isEnabled("Cno-stdlib", true)) {
                ErrorFixSuggester.fix(str, str, errorWriter, lineNumber, fileName);
                errorWriter.println("stdlib-error: " + "functions from stdlib with options [ -Cno-stdlib ]" + System.lineSeparator());
            }
        }

        if (commentBlock) {
            expectingCommentBlock = true;
            commentBlockEnd = false;
        }

        if (commentBlockEnd) {
            expectingCommentBlock = false;
            commentBlockEnd = false;
        }
        if (comment || expectingCommentBlock || ignore) {
            return;
        }
        
        if (lineNumber == 1) {
            if (!export) {
                ErrorFixSuggester.fix(str, str, errorWriter, lineNumber, fileName);
                errorWriter.println("export-error: " + "expected export path.main" + System.lineSeparator());
                errors++;
            }
        }

        if (str.contains("\"")) {
            int quotes = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '"') {
                    quotes++;
                }
            }
            if ((quotes % 2) != 0) {
                ErrorFixSuggester.fix(str + "\"", '"', errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: " + "unmatched \"" + System.lineSeparator());
                errors++;
            }
        } else if (str.contains("'")) {
            int quotes = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '\'') {
                    quotes++;
                }
            }
            if ((quotes % 2) != 0) {
                ErrorFixSuggester.fix(str + "'", '\'', errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: " + "unmatched '" + System.lineSeparator());
                errors++;
            }
        }

        if (!scopeStart && !scopeEnd && !whitespace) {
            if (str.endsWith(";")) {
                ErrorFixSuggester.fix(str, ';', errorWriter, lineNumber, fileName);
                errorWriter.println("warn: " + "found semicolon" + System.lineSeparator());
                warnings++;
            }
        }

        if (str.contains("(")) {
            int brackets = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '(') {
                    brackets++;
                } else if (str.charAt(i) == ')') {
                    brackets--;
                }
            }
            if ((brackets % 2) != 0) {
                ErrorFixSuggester.fix(str + ")", ')', errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: " + "unmatched (" + System.lineSeparator());
                errors++;
            }
        }

        if (scopeEnd) {
            if (scopeStack.isEmpty()) {
                ErrorFixSuggester.fix(str, '}', errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: " + "stray closing brace" + System.lineSeparator());
                errors++;
            } else {
                if (scopeStack.lastElement().equals("if") || scopeStack.lastElement().equals("elseif") || scopeStack.lastElement().equals("else")) {
                    scopeStack.pop();
                    expectingIfElseElseIf = false;
                } else if (scopeStack.lastElement().equals("function")) {
                    scopeStack.pop();
                    functions.put(b_FunctionName, b_FunctionCode);

                    // Cleanup
                    expectingFunction = false;
                    b_FunctionName = "";
                    b_FunctionCode = "";
                    b_FunctionParameters.clear();
                }
            }
        }

        if (functionMake) {
            if (expectingFunction) {
                ErrorFixSuggester.fix(str, "fn", errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: " + "nested functions are not supported" + System.lineSeparator());
                errors++;
            } if (functions.containsKey(str)) {
                ErrorFixSuggester.fix(str, str, errorWriter, lineNumber, fileName);
                errorWriter.println("already-defined-error: " + "function already defined" + System.lineSeparator());
                errors++;
            }
            scopeStack.push("function");
            b_FunctionName = str;
            String b_str = str;
            b_str = b_str.replace("fn ", "").replace(" {", "").split("\\(")[1].replace(")", ""); // as: string, at: string
            String[] parameters = b_str.split(",");
            for (String parameter : parameters) {
                parameter = parameter.trim();
                Map<String, String> b = new HashMap<>();
                String[] p = new String[2];
                for (int i = 0; i < parameter.split(":").length; i++) {
                    p[i] = parameter.split(":")[i];
                }
                b.put(p[0], p[1]);
                b_FunctionParameters.add(b);
            }
            expectingFunction = true;
        }

        if (variable) {
            str = str.substring(4); // remove 'var '
            str = str.replaceAll(" ", "");
            String[] var_valueMap = str.split("=");
            if (!functionUse) {
                if (!(var_valueMap[0].contains(":"))) {
                    errorWriter.println("syntax-error: " + "expected :<Type> at line: " + lineNumber + System.lineSeparator());
                    errors++;
                }

                String[] name_typeMap = var_valueMap[0].split(":");
                if (variables.containsKey(name_typeMap[0])) {
                    errorWriter.println("already-defined-error: " + "variable already defined at line: " + lineNumber + System.lineSeparator());
                    errors++;
                } else {
                    v_PutKey(name_typeMap[0], name_typeMap[1], var_valueMap[1]);
                    // name, type, value
                }
            }
            str = "var " + str; // re-added 'var '
        }

        if (_if || _elseIf || _else) {
            expectingIfElseElseIf = true;
            if (_if) {
                scopeStack.push("if");
            } else if (_elseIf) {
                scopeStack.push("elseif");
            } else if (_else) {
                scopeStack.push("else");
            }
        }

        if (_import) {
            String mod = str.replace("import", "").trim().replace(';', ' ').trim();
            if (!(mod.startsWith("dbm") && Main.args.isEnabled("Cno-stdlib", true))) {
                if (!(new ArrayList<>(List.of(SYSTEM_IMPORTS)).contains(mod))) {
                    ArrayList<File> dirs = new FileUtils().listFiles(new File("."));
                    for (File file : dirs) {
                        if (!file.getPath().contains(mod.split("\\.")[0])) {
                            ErrorFixSuggester.fix(str, mod, errorWriter, lineNumber, fileName);
                            errorWriter.println("import-error: " + "illegal import" + System.lineSeparator());
                            errors++;
                        }
                    }
                }
            } else {
                ErrorFixSuggester.fix(str, mod, errorWriter, lineNumber, fileName);
                System.out.println("import-error: " + "import from stdlib not allowed with javac flag [ -Cno-stdlib ]" + System.lineSeparator());
                errors++;
            }
        }

        if (stringconcat) {
            if (str.contains("\"") && Pattern.compile("\\d").matcher(str).find()) {
                String nums = Pattern.compile("[a-zA-Z]").matcher(str).replaceAll("");
                ErrorFixSuggester.fix(str, nums, errorWriter, lineNumber, fileName);
                errorWriter.println("syntax-error: string concatenation with number");
                errors++;
            }
        }

        if (statement) {
            ErrorFixSuggester.fix(str, str, errorWriter, lineNumber, fileName);
            errorWriter.println("syntax-error: unknown symbol(s)");
        }

        // Finalizers
        if (expectingFunction && !statement) {
            b_FunctionCode += str + System.lineSeparator();
        }

        if (!whitespace && !statement) {
            TOKENIZED.append(str).append(System.lineSeparator());
        }

        lineNumber++;
    }

    private void v_PutKey(String var1, String var2, String var3) {
        variables.computeIfAbsent(var1, ignored -> new HashMap<>()).put(var2, var3);
    }
}
