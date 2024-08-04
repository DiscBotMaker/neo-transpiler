package net.noerlol.neotrans.transpiler;

import net.noerlol.neotrans.Main;
import net.noerlol.neotrans.api.lsp.LSPOnly;
import net.noerlol.neotrans.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Tokenizer {
    // Settings
    private int TAB_LENGTH = 4;
    private final String fileName;

    // stuff
    private String TOKENIZED = "";
    private int lineNumber = 1;
    private int errors = 0;
    private int warnings = 0;

    // Finals
    private final Logger LOGGER = LoggerFactory.getLogger(Tokenizer.class);
    private final String[] SYSTEM_IMPORTS = {
            "dbm.bot.BotProvider",
            "dbm.bot.Bot",
            "dbm.bot.Context",
            "dbm.bot.Command",
            "java.lang.String", // Replaced because the preprocessor replaces dbm.lang* with java.lang*
            "dbm.util.Project"
    }; // Todo: Package Manager stuffs


    // Maps
    private Map<String, Map<String, String>> variables = new HashMap<>();
    private Map<String, String> functions = new HashMap<>();

    // Buffers
    private String b_FunctionName = "";
    private String b_FunctionCode = "";
    private ArrayList<Map<String, String>> b_FunctionParameters = new ArrayList<>();

    private String b_IfElseElseIfCode = "";

    private boolean expectingIfElseElseIf = false;
    private boolean expectingFunction = false;

    private String handleMacro(String macro) {
        if (macro.equals("__stdversion__")) {
            return Version.STDVERSION;
        } else if (macro.equals("__operating_system__")) {
            return "\"" + System.getProperty("os.name") + "\"";
        } else if (macro.equals("__J_classpath__")) {
            return "\"" + "I need to fix this!!!!" + "\"";
        }

        else {
            return macro;
        }
    }

    public Tokenizer(int TAB_LENGTH, String fileName) {
        this.TAB_LENGTH = TAB_LENGTH;
        this.fileName = fileName;
    }

    public TokenizedCode parse(String[] lines) {
        for (String line : lines) {
            // Preprocessor
            line = line.replace("__stdversion__", handleMacro("__stdversion__"));
            line = line.replace("__operating_system__", handleMacro("__operating_system__"));
            line = line.replace("__J_classpath__", handleMacro("__J_classpath__"));

            line = line.replace("string", "String");
            line = line.replace("dbm.lang", "java.lang");

            // Finally,
            parseLine(line, System.err);
        }
        return parseEnd();
    }

    public TokenizedCode parseEnd() {
        PrintStream errorWriter = System.err;
        if (expectingFunction || expectingIfElseElseIf) {
            InlineErrorFixSuggestion.fix("}", '}', errorWriter, lineNumber);
            errorWriter.println("error: " + "not found" + "\n");
            errors++;
        } if (!functions.containsKey("fn main() {")) {
            InlineErrorFixSuggestion.fix("fn main() { ... }", "fn main() { ... }", errorWriter, lineNumber);
            errorWriter.println("error: " + "not found" + "\n");
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

        return new TokenizedCode(TOKENIZED, TAB_LENGTH);
    }
    @LSPOnly
    public TokenizedCode NULL_ParseEnd() {
        PrintStream errorWriter = NullOutputStream.getNull();
        if (expectingFunction || expectingIfElseElseIf) {
            InlineErrorFixSuggestion.fix("}", '}', errorWriter, lineNumber);
            errorWriter.println("error: " + "not found" + "\n");
            errors++;
        } if (!functions.containsKey("fn main() {")) {
            InlineErrorFixSuggestion.fix("fn main() { ... }", "fn main() { ... }", errorWriter, lineNumber);
            errorWriter.println("error: " + "not found" + "\n");
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

        return new TokenizedCode(TOKENIZED, TAB_LENGTH);
    }

    @LSPOnly
    public String findReplaceMacro(String line) {
        return line; // Todo: figure this out
    }

    @LSPOnly
    public void parseLine(String str, PrintStream errorWriter) {
        boolean functionMake = false, variable = false, statement = false, functionUse = false,
                whitespace = false, scopeEnd = false, comment = false, println = false,
                inputln = false, _if = false, _elseIf = false, _else = false, _import = false,
                export = false, stringconcat = false;

        String modifiedStr = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\t') {
                modifiedStr += " ".repeat(TAB_LENGTH);
            } else {
                modifiedStr += str.charAt(i);
            }
        }
        str = modifiedStr.trim();

        // Checks
        if (str.startsWith("fn ")) {
            functionMake = true;
        } else if (str.startsWith("var ")) {
            variable = true;
        } else if (str.trim().isEmpty()) {
            whitespace = true;
        } else if (str.contains("}")) {
            scopeEnd = true;
        } else if (str.startsWith("//")) {
            comment = true;
        } else if (str.contains("println")) {
            println = true;
        } else if (str.contains("inputln")) {
            inputln = true;
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
        } else {
            statement = true;
        }

        if (println || inputln) {
            if (Main.args.isEnabled("Cno-stdlib", true)) {
                InlineErrorFixSuggestion.fix(str, str, errorWriter, lineNumber);
                errorWriter.println("error: " + "functions from stdlib with options [ -Cno-stdlib ]" + "\n");
            }
        }

        if (!comment) {
            if (lineNumber == 1) {
                if (!export) {
                    InlineErrorFixSuggestion.fix(str, str, errorWriter, lineNumber);
                    errorWriter.println("error: " + "expected export path.main" + "\n");
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
                    InlineErrorFixSuggestion.fix(str + "\"", '"', errorWriter, lineNumber);
                    errorWriter.println("error: " + "unmatched \"" + "\n");
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
                    InlineErrorFixSuggestion.fix(str + "'", '\'', errorWriter, lineNumber);
                    errorWriter.println("error: " + "unmatched '" + "\n");
                    errors++;
                }
            }

            if (!functionMake && !scopeEnd && !whitespace) {
                if (!str.endsWith(";")) {
                    InlineErrorFixSuggestion.fix(str + ";", ';', errorWriter, lineNumber);
                    errorWriter.println("error: " + "not found" + "\n");
                    errors++;
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
                    InlineErrorFixSuggestion.fix(str + ")", ')', errorWriter, lineNumber);
                    errorWriter.println("error: " + "unmatched (" + "\n");
                    errors++;
                }
            }

            if (scopeEnd) {
                if (!expectingFunction && !expectingIfElseElseIf) {
                    InlineErrorFixSuggestion.fix(str, '}', errorWriter, lineNumber);
                    errors++;
                }
                if (expectingFunction) {
                    functions.put(b_FunctionName, b_FunctionCode);

                    // Cleanup
                    expectingFunction = false;
                    b_FunctionName = "";
                    b_FunctionCode = "";
                    b_FunctionParameters.clear();
                } else if (expectingIfElseElseIf) {
                    expectingIfElseElseIf = false;
                    b_IfElseElseIfCode = "";
                }
            }

            if (functionMake) {
                if (expectingFunction) {
                    InlineErrorFixSuggestion.fix(str, "fn", errorWriter, lineNumber);
                    errors++;
                }
                b_FunctionName = str;
                String b_str = str;
                b_str = b_str.replace("fn ", "").replace(" {", "").split("\\(")[1].replace(")", ""); // str as, str at
                String[] parameters = b_str.split(",");
                for (String parameter : parameters) {
                    parameter = parameter.trim(); // str: as,str: ad
                    // str: as// oposite
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
                        errorWriter.println("error: " + "expected :<Type> at line: " + lineNumber);
                        errors++;
                    }

                    String[] name_typeMap = var_valueMap[0].split(":");
                    if (variables.containsKey(name_typeMap[0])) {
                        errorWriter.println("error: " + "variable already defined at line: " + lineNumber);
                        errors++;
                    } else {
                        v_PutKey(name_typeMap[0], name_typeMap[1], var_valueMap[1]);
                        // name, type, value
                    }
                }
                str = "var " + str; // re-added 'var '
            }

            if (_if) {
                expectingIfElseElseIf = true;
            }

            if (_import) {
                String mod = str.replace("import", "").trim().replace(';', ' ').trim();
                if (!(mod.startsWith("dbm") && Main.args.isEnabled("Cno-stdlib", true))) {
                    if (!(new ArrayList<>(List.of(SYSTEM_IMPORTS)).contains(mod))) { // Todo: use package manager verification
                        ArrayList<File> dirs = new FileUtils().listFiles(new File("."));
                        for (File file : dirs) {
                            if (!file.getPath().contains(mod.split("\\.")[0])) {
                                InlineErrorFixSuggestion.fix(str, mod, errorWriter, lineNumber);
                                errorWriter.println("error: " + "illegal import" + "\n");
                                errors++;
                            }
                        }
                    }
                } else {
                    InlineErrorFixSuggestion.fix(str, mod, errorWriter, lineNumber);
                    System.out.println("error: " + "import from stdlib not allowed with javac flag [ -Cno-stdlib ]" + "\n");
                    errors++;
                }
            }

            if (stringconcat) {
                if (str.contains("\"") && Pattern.compile("\\d").matcher(str).find()) {
                    String nums = Pattern.compile("[a-zA-Z]").matcher(str).replaceAll("");
                    InlineErrorFixSuggestion.fix(str, nums, errorWriter, lineNumber);
                    errorWriter.println("error: string concatenation with number");
                    errors++;
                }
            }

            // Finalizers
            if (expectingFunction && !statement) {
                b_FunctionCode += str + "\n";
            }

            if (!whitespace && !statement) {
                TOKENIZED += str + "\n";
            }
        }
        lineNumber++;
    }

    private void v_PutKey(String var1, String var2, String var3) {
        variables.computeIfAbsent(var1, ignored -> new HashMap<>()).put(var2, var3);
    }
}
