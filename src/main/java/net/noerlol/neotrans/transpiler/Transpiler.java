package net.noerlol.neotrans.transpiler;

import net.noerlol.neotrans.utils.DynamicImports;
import net.noerlol.neotrans.utils.Import;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.TranspiledCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transpiler {
    private static String transpiledCode = "";
    private static int lineNumber = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(Transpiler.class);

    public static TranspiledCode transpile(TokenizedCode tc) {
        for (String str : tc.getCode().split("\\n")) {
            str = str;
            boolean functionMake = false, variable = false, statement = false, functionUse = false, whitespace = false, funEnd = false, comment = false, println = false, inputln = false, _if = false, _elseIf = false, _else = false, scope_end = false, _import = false;
            if (str.startsWith("fn ")) {
                functionMake = true;
            } else if (str.startsWith("var ")) {
                variable = true;
            } else if (str.isEmpty()) {
                whitespace = true;
            } else if (str.startsWith("}")) {
                funEnd = true;
            } else if (str.contains("println")) {
                println = true;
            } else if (str.contains("inputln")) {
                inputln = true;
            } else if (str.startsWith("import")) {
                _import = true;
            } else if (str.contains("()") && !str.startsWith("fn")) {
                functionUse = true;
            } else if (str.contains("if") && !str.contains("else")) {
                _if = true;
            } else if (str.contains("if") && str.contains("else")) {
                _elseIf = true;
            } else if (!str.contains("if") && str.contains("else")) {
                _else = true;
            }

            if (println) {
                str = str.replaceAll("println", ""); // ("hello, wrld!");
                str = str.replaceAll("\\(", ""); // "hello, wrld!");
                str = str.replaceAll("\\)", ""); // "hello, wrld!";
                str = str.replaceAll(";", ""); // "hello, wrld!"
                transpiledCode += "System.out.println(" + str + ");";
            } else if (inputln) {
                str = str.replaceAll("inputln", ""); // ("hello, wrld!");
                str = str.replaceAll("\\(", ""); // "hello, wrld!");
                str = str.replaceAll("\\)", ""); // "hello, wrld!";
                str = str.replaceAll(";", ""); // "hello, wrld!"
                transpiledCode += "System.out.print(" + str + ");";
                transpiledCode += "new Scanner(System.in).nextLine();";
            }

            if (variable) {
                str.replaceAll("var ", "");
                String[] nameType_Value = str.split("=");
                nameType_Value[0] = nameType_Value[0];
                nameType_Value[1] = nameType_Value[1];

                String value = nameType_Value[1];
                String type = nameType_Value[0].split(":")[1];
                String name = nameType_Value[0].split(":")[0];

                if (type.equalsIgnoreCase("string")) {
                    type = "String";
                }
                if (value.startsWith("inputln")) {
                    value = value.replaceAll("inputln", ""); // ("hello, wrld!");
                    value = value.replaceAll("\\(", ""); // "hello, wrld!");
                    value = value.replaceAll("\\)", ""); // "hello, wrld!";
                    value = value.replaceAll(";", ""); // "hello, wrld!"

                    transpiledCode += "System.out.print(" + value + ");";
                    transpiledCode += type + " " + name + " = " + "new Scanner(System.in).nextLine();";
                } else {
                    transpiledCode += type + " " + name + " = " + value + "; ";
                }
                transpiledCode = transpiledCode.replace("var ", "").replace(":" + type, "");
            }

            if (functionMake) {
                str = str.substring(3); // remove 'fn'
                str = str.substring(0, str.length() - 2); // remove ()
                if (str.startsWith("main")) {
                    transpiledCode += "public static void " + str.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\{", "") + "(String[] args) {";
                } else {
                    transpiledCode += "public static void " + str + " {";
                }
            }

            if (functionUse) {
                transpiledCode += str + ";";
            }

            if (funEnd) {
                // DEBUG INJECTION
                transpiledCode += "System.out.println(System.getProperty(\"java.class.path\"));System.out.println(LibraryVersion.VERSION);";


                transpiledCode += "}";
            }

            if (_if || _elseIf || _else) {
                transpiledCode += str;
            }
            lineNumber++;
        }
        Import[] imports = DynamicImports.getImportsFromCode(transpiledCode);
        String str_Imports = "";
        for (Import _import : imports) {
            str_Imports += "import " +  _import.getPackageName() + "." + _import.getClassName() + ";";
        }
        return new TranspiledCode(transpiledCode, str_Imports);
    }
}
