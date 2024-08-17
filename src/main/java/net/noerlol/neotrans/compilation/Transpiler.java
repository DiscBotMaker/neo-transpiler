package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.compilation.dynamic.DynamicImports;
import net.noerlol.neotrans.compilation.dynamic.JavaImport;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.TranspiledCode;

public class Transpiler {
    private static String transpiledCode = "";
    private static int lineNumber = 1;

    public static TranspiledCode transpile(TokenizedCode tc) {
        for (String str : tc.getCode().split("\\n")) {
            boolean functionMake = false, variable = false, statement = false, functionUse = false, whitespace = false, funEnd = false, comment = false, println = false, inputln = false, print = false, _if = false, _elseIf = false, _else = false, scope_end = false, _import = false, printlnerr = false, printerr = false, exit = false;
            if (str.startsWith("fn ")) {
                functionMake = true;
            } else if (str.startsWith("var ")) {
                variable = true;
            } else if (str.isEmpty()) {
                whitespace = true;
            } else if (str.startsWith("}")) {
                funEnd = true;
            } else if (str.contains("println") && !str.contains("printlnerr")) {
                println = true;
            } else if (str.contains("inputln")) {
                inputln = true;
            } else if (str.contains("print") && !str.contains("println") && !str.contains("printerr")) {
                print = true;
            } else if (str.contains("printlnerr")) {
                printlnerr = true;
            } else if (str.contains("printerr") && !str.contains("printlnerr")) {
                printerr = true;
            } else if (str.contains("exit")) {
                exit = true;
            }

            else if (str.startsWith("import")) {
                _import = true;
            } else if ((str.contains("(") && str.contains(")")) && !str.startsWith("fn")) {
                functionUse = true;
            } else if (str.contains("if") && !str.contains("else")) {
                _if = true;
            } else if (str.contains("if") && str.contains("else")) {
                _elseIf = true;
            } else if (!str.contains("if") && str.contains("else")) {
                _else = true;
            }

            if (inputln) {
                str = str.replaceAll("inputln", ""); // ("hello, wrld!");
                str = str.replaceAll("\\(", ""); // "hello, wrld!");
                str = str.replaceAll("\\)", ""); // "hello, wrld!";
                str = str.replaceAll(";", ""); // "hello, wrld!"
                if (!str.isEmpty()) {
                    transpiledCode += "System.print(" + str + ");";
                }
                transpiledCode += "new Scanner(java.lang.System.in).nextLine();";
            } else {
                if (print) {
                    str = str.replaceAll("print", ""); // ("hello, wrld!");
                    str = str.replaceAll("\\(", ""); // "hello, wrld!");
                    str = str.replaceAll("\\)", ""); // "hello, wrld!";
                    str = str.replaceAll(";", ""); // "hello, wrld!"
                    transpiledCode += "System.print(" + str + ");";
                } else if (println) {
                    str = str.replaceAll("println", ""); // ("hello, wrld!");
                    str = str.replaceAll("\\(", ""); // "hello, wrld!");
                    str = str.replaceAll("\\)", ""); // "hello, wrld!";
                    str = str.replaceAll(";", ""); // "hello, wrld!"
                    if (!str.isEmpty()) {
                        transpiledCode += "System.print(" + str + ");System.println();";
                    } else {
                        transpiledCode += "System.println();";
                    }
                } else if (printlnerr) {
                    str = str.replace("printlnerr", ""); // ("hello, wrld!");
                    str = str.replaceAll("\\(", ""); // "hello, wrld!");
                    str = str.replaceAll("\\)", ""); // "hello, wrld!";
                    str = str.replaceAll(";", ""); // "hello, wrld!"
                    if (!str.isEmpty()) {
                        transpiledCode += "System.printErr(" + str + ");System.printlnErr();";
                    } else {
                        transpiledCode += "System.printlnErr();";
                    }
                } else if (printerr) {
                    str = str.replace("printerr", ""); // ("hello, wrld!");
                    str = str.replaceAll("\\(", ""); // "hello, wrld!");
                    str = str.replaceAll("\\)", ""); // "hello, wrld!";
                    str = str.replaceAll(";", ""); // "hello, wrld!"
                    transpiledCode += "System.printErr(" + str + ");";
                }
            }

            if (exit) {
                str = str.replace("exit", ""); // ("hello, wrld!");
                str = str.replaceAll("\\(", ""); // "hello, wrld!");
                str = str.replaceAll("\\)", ""); // "hello, wrld!";
                str = str.replaceAll(";", ""); // "hello, wrld!"
                transpiledCode += "java.lang.System.exit(" + str + ");";
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

                    if (!value.isEmpty()) {
                        transpiledCode += "System.print(" + value + ");";
                    }
                    transpiledCode += type + " " + name + " = " + "new Scanner(java.lang.System.in).nextLine();";
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
                    if (str.charAt(str.indexOf('(') + 1) != ')') {
                        transpiledCode += "public static void " + str.substring(0, str.indexOf('(')) + "(";
                        String b_str = str;
                        b_str = b_str.replace("fn ", "").replace(" {", "").split("\\(")[1].replace(")", ""); // str as, str at
                        String[] parameters = b_str.split(",");
                        for (String parameter : parameters) {
                            parameter = parameter.trim(); // str: as,str: ad
                            // str: as// oposite
                            String[] p = new String[2];
                            for (int i = 0; i < parameter.split(":").length; i++) {
                                p[i] = parameter.split(":")[i];
                            }
                            transpiledCode += p[1] + " " + p[0] + ",";
                        }
                        transpiledCode = transpiledCode.substring(0, transpiledCode.length() - 1) + ") {";
                    } else {
                        transpiledCode += "public static void " + str + " {";
                    }
                }
            }

            if (functionUse) {
                transpiledCode += str + ";";
            }

            if (funEnd) {
                transpiledCode += "}";
            }

            if (_if || _elseIf || _else) {
                transpiledCode += str;
            }
            lineNumber++;
        }
        JavaImport[] imports = DynamicImports.getImportsFromCode(transpiledCode);
        String str_Imports = "";
        for (JavaImport _import : imports) {
            str_Imports += "import " +  _import.getPackageName() + "." + _import.getClassName() + ";";
        }
        return new TranspiledCode(transpiledCode, str_Imports, tc);
    }
}
