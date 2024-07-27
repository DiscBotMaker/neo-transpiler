package net.noerlol.neotrans.transpiler;

import net.noerlol.neotrans.utils.FileUtils;
import net.noerlol.neotrans.utils.InlineErrorFixSuggestion;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    private final String[] VALID_IMPORTS = {
            "dbm.bot.BotProvider",
            "dbm.bot.Bot",
            "dbm.bot.Context",
            "dbm.bot.Command",
            "dbm.lang.String",
            "dbm.util.Project"
    }; // Todo: Package Manager stuffs


    // Maps
    private Map<String, Map<String, String>> variables = new HashMap<>();
    private Map<String, String> functions = new HashMap<>();

    // Buffers
    private String b_FunctionName = "";
    private String b_FunctionCode = "";
    private String b_IfElseElseIfCode = "";

    private boolean expectingIfElseElseIf = false;
    private boolean expectingFunction = false;

    private String handleMacro(String macro) {
        if (macro.equals("__stdversion__")) {
            return Version.STDVERSION;
        } else if (macro.equals("__operating_system__")) {
            return System.getProperty("os.name");
        }

        else {
            return macro;
        }
    }

    public Tokenizer(int TAB_LENGTH, String fileName) {
        this.TAB_LENGTH = TAB_LENGTH;
        this.fileName = fileName;
    }

    public TokenizedCode parse(ArrayList<String> lines) {
        for (String line : lines) {
            if (line.contains("__stdversion__")) {
                line = line.replace("__stdversion__", handleMacro("__stdversion__"));
            } else if (line.contains("__operating_system__")) {
                line = line.replace("__operating_system__", handleMacro("__operating__system"));
            }
            parseLine(line);
        }
        return parseEnd();
    }

    private TokenizedCode parseEnd() {
        if (expectingFunction || expectingIfElseElseIf) {
            InlineErrorFixSuggestion.fix("}", '}', System.err);
            System.err.println("error: " + "not found" + "\n");
            System.exit(1);
        } if (!functions.containsKey("fn main() {")) {
            InlineErrorFixSuggestion.fix("fn main() { ... }", "fn main() { ... }", System.err);
            System.err.println("error: " + "not found" + "\n");
            System.exit(1);
        } if (errors > 0 || warnings > 0) {
            System.err.println("errors generated: " + errors);
            System.err.println("warnings generated: " + warnings);
        } if (errors > 0) {
            System.exit(1);
        }

        return new TokenizedCode(TOKENIZED, TAB_LENGTH);
    }

    private void parseLine(String str) {
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
        } else if (str.contains("()")) {
            functionUse = true;
        } else if (str.startsWith("export")) {
            export = true;
        } else if (Pattern.compile("\\+").matcher(str).matches()) {
            stringconcat = true;
        } else {
            statement = true;
        }

        if (lineNumber == 1) {
            if (!export) {
                InlineErrorFixSuggestion.fix(str, str, System.err);
                System.err.println("error: " + "expected export path.main" + "\n");
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
                InlineErrorFixSuggestion.fix(str + "\"", '"', System.err);
                System.err.println("error: " + "unmatched \"" + "\n");
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
                InlineErrorFixSuggestion.fix(str + "'", '\'', System.err);
                System.err.println("error: " + "unmatched '" + "\n");
                errors++;
            }
        }

        if (!functionMake && !scopeEnd && !whitespace) {
            if (!str.endsWith(";")) {
                InlineErrorFixSuggestion.fix(str + ";", ';', System.err);
                System.err.println("error: " + "not found" + "\n");
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
                InlineErrorFixSuggestion.fix(str + ")", ')', System.err);
                System.err.println("error: " + "unmatched (" + "\n");
                errors++;
            }
        }

        if (scopeEnd) {
            if (!expectingFunction && !expectingIfElseElseIf) {
                InlineErrorFixSuggestion.fix(str, '}', System.err);
                errors++;
            }
            if (expectingFunction) {
                expectingFunction = false;
                functions.put(b_FunctionName, b_FunctionCode);
                b_FunctionName = ""; b_FunctionCode = "";
            } else if (expectingIfElseElseIf) {
                expectingIfElseElseIf = false;
                b_IfElseElseIfCode = "";
            }
        }

        if (functionMake) {
            if (expectingFunction) {
                InlineErrorFixSuggestion.fix(str, "fn", System.err);
                errors++;
            }
            b_FunctionName = str;
            expectingFunction = true;
        }

        if (variable) {
            str = str.substring(4); // remove 'var '
            str = str.replaceAll(" ", "");
            String[] var_valueMap = str.split("=");
            if (!functionUse) {
                if (!(var_valueMap[0].contains(":"))) {
                    System.err.println("error: " + "expected :<Type> at line: " + lineNumber);
                    errors++;
                }

                String[] name_typeMap = var_valueMap[0].split(":");
                if (variables.containsKey(name_typeMap[0])) {
                    System.err.println("error: " + "variable already defined at line: " + lineNumber);
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
            if (!(new ArrayList<>(List.of(VALID_IMPORTS)).contains(mod))) { // Todo: use package manager verification
                InlineErrorFixSuggestion.fix(str, mod, System.err);
                System.err.println("error: " + "import not found" + "\n");
                errors++;
            }
        }

        if (stringconcat) {
            if (str.contains("\"") && Pattern.compile("\\d").matcher(str).find()) {
                String nums = Pattern.compile("[a-zA-Z]").matcher(str).replaceAll("");
                InlineErrorFixSuggestion.fix(str, nums, System.err);
                System.err.println("error: string concatenation with number");
                errors++;
            }
        }

        // Finalizers
        if (expectingFunction && !statement) {
            b_FunctionCode += str + "\n";
        }

        if (!whitespace && !comment && !statement) {
            TOKENIZED += str + "\n";
        }
        lineNumber++;
    }

    private void v_PutKey(String var1, String var2, String var3) {
        variables.computeIfAbsent(var1, k -> new HashMap<>()).put(var2, var3);
    }
}
