import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Compilador {
    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    // static List<Token> tokenList = new ArrayList<Token>();
    static final String[] reservedWords = { "string", "const", "int", "char", "while", "if", "float", "else", "&&",
            "||", "!", "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}",
            "readln", "div", "write", "writeln", "mod", "[", "]" };

    static int lineCount = 1;
    static boolean pauseCompiling = false;
    static String fileStr = "";
    static Lexer lexer = new Lexer();
    static Parser parser = new Parser();
    static Token currentToken;

    static final int tokenId = 0;
    static final int tokenStr = 1;
    static final int tokenConst = 2;
    static final int tokenInt = 3;
    static final int tokenChar = 4;
    static final int tokenWhile = 5;
    static final int tokenIf = 6;
    static final int tokenFloat = 7;
    static final int tokenElse = 8;
    static final int tokenAnd = 9;
    static final int tokenOr = 10;
    static final int tokenNot = 11;
    static final int tokenAtrib = 12;
    static final int tokenEqual = 13;
    static final int tokenOpenPar = 14;
    static final int tokenClosePar = 15;
    static final int tokenLess = 16;
    static final int tokenGtr = 17;
    static final int tokenDif = 18;
    static final int tokenGtrEqual = 19;
    static final int tokenLessEqual = 20;
    static final int tokenComma = 21;
    static final int tokenPlus = 22;
    static final int tokenMinus = 23;
    static final int tokenMult = 24;
    static final int tokenDiv = 25;
    static final int tokenSemiColon = 26;
    static final int tokenOpenBra = 27;
    static final int tokenCloseBra = 28;
    static final int tokenRead = 29;
    static final int tokenDiv2 = 30;
    static final int tokenWrite = 31;
    static final int tokenWriteLn = 32;
    static final int tokenMod = 33;
    static final int tokenOpenSq = 34;
    static final int tokenCloseSq = 35;
    static final int tokenValue = 36;

    static class Symbol {
        String lexeme;
        int token;

        Symbol(String lex, int tk) {
            lexeme = lex;
            token = tk;
        }
    }

    static class Token {
        int token;
        String lexeme;
        String type;
        byte bsize;

        Token(String lex, int tk, String t) {
            lexeme = lex;
            token = tk;
            type = t;
        }
    }

    static class Lexer {
        static String lexeme = "";
        static int i = 0;
        static int currentState = 0;

        void throwError(String type) {
            pauseCompiling = true;

            System.out.println(lineCount);
            // invalid character
            if (type == "invalid_char") {
                System.out.println("caractere invalido.");
            }
            // non identified lexeme
            else if (type == "invalid_lexeme") {
                System.out.println("lexema nao identificado [" + lexeme + "].");
            }
            // unexpected EOF
            else if (type == "unexpected_eof") {
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        boolean isValid(char c) {
            if (Character.isDigit(c) || Character.isLetter(c) || c == ' ' || c == '_' || c == '.' || c == ';' | c == ','
                    || c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '+'
                    || c == '-' || c == '\"' || c == '\'' || c == '/' || c == '*' || c == '|' || c == '\\' || c == '&'
                    || c == '%' || c == '!' || c == '?' || c == '>' || c == '<' || c == '=' || c == '\n' || c == '#')
                return true;
            else
                return false;
        }

        boolean isHexa(char c) {
            if (Character.isDigit(c) || c == 'a' || c == 'A' || c == 'b' || c == 'B' || c == 'c' || c == 'C' || c == 'd'
                    || c == 'D' || c == 'e' || c == 'E' || c == 'f' || c == 'F')
                return true;
            else
                return false;
        }

        int state0(char c) {
            int nextState = 0;

            lexeme += c;

            if (Character.isLetter(c) || c == '_') {
                nextState = 1;
            } else if (Character.isDigit(c)) {
                if (c != '0')
                    nextState = 2;
                else
                    nextState = 16;
            } else if (c == '.') {
                nextState = 3;
            } else if (c == '<') {
                nextState = 5;
            } else if (c == '>') {
                nextState = 6;
            } else if (c == '!') {
                nextState = 7;
            } else if (c == '&') {
                nextState = 8;
            } else if (c == '|') {
                nextState = 9;
            } else if (c == '/') {
                nextState = 10;
            } else if (c == '\'') {
                nextState = 13;
            } else if (c == '\"') {
                nextState = 15;
            } else if (c == '=' || c == '*' || c == '+' || c == '-' || c == ',' || c == '(' || c == ')' || c == '{'
                    || c == '}' || c == '[' || c == ']') {
                nextState = 19;
            } else if (c == '#' || c == ';' || c == ' ' || c == '\n') {
                nextState = 20;
            }
            return nextState;
        }

        // id / reserved words
        int state1(char c) {
            int nextState = 1;

            if (Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;
            } else {
                Symbol symbol = symbolTable.get(lexeme);

                if (symbol != null) {
                    Token token = new Token(lexeme, symbol.token, "String");

                    currentToken = token;

                    System.out.println(token.lexeme);

                } else {
                    Symbol newSymbol = new Symbol(lexeme, tokenId);
                    symbolTable.put(lexeme, newSymbol);

                    Token token = new Token(lexeme, tokenId, "String");
                    currentToken = token;

                    System.out.println(token.lexeme);
                }

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // int
        int state2(char c) {
            int nextState = 2;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else if (c == '.') {
                lexeme += c;
                nextState = 3;
            } else {
                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // ?. (float)
        int state3(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else if (c == '#') {
                throwError("unexpected_eof");
            } else {
                lexeme += c;
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // ?.d float
        int state4(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else {
                Token token = new Token(lexeme, tokenValue, "Float");
                currentToken = token;

                System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // <
        int state5(char c) {
            int nextState = 20;

            if (c == '-' || c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            System.out.println(token.lexeme);

            return nextState;
        }

        // >
        int state6(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            System.out.println(token.lexeme);

            return nextState;
        }

        // !
        int state7(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            System.out.println(token.lexeme);

            return nextState;
        }

        // &
        int state8(char c) {
            int nextState = 20;

            if (c == '&') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // |
        int state9(char c) {
            int nextState = 20;

            if (c == '|') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // /
        int state10(char c) {
            int nextState = 20;

            if (c == '*') {
                nextState = 11;
                lexeme = "";
            } else {
                i--;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                System.out.println(token.lexeme);
            }

            return nextState;
        }

        // /* inside comment
        int state11(char c) {
            int nextState = 11;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '*')
                nextState = 12;

            return nextState;
        }

        // /* ? *
        int state12(char c) {
            int nextState = 12;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '/')
                nextState = 0;
            else if (c != '*')
                nextState = 11;

            return nextState;
        }

        // '
        int state13(char c) {
            int nextState = 14;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c == '\"') {
                throwError("invalid_lexeme");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 'c
        int state14(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c != '\'') {
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                System.out.println(token.lexeme);
            }

            return nextState;
        }

        // "
        int state15(char c) {
            int nextState = 15;

            if (c == '\n') {
                throwError("invalid_lexeme");
            } else if (c == '\"') {
                lexeme += "0" + c;
                nextState = 20;

                Token token = new Token(lexeme, tokenValue, "String");
                currentToken = token;

                System.out.println(token.lexeme);
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 0
        int state16(char c) {
            int nextState = 17;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == 'x' || c == 'X') {
                lexeme += c;
            } else {

                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // 0x
        int state17(char c) {
            int nextState = 18;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                throwError("invalid_lexeme");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 0x?
        int state18(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                System.out.println(token.lexeme);
            }

            return nextState;
        }

        // = * + - , ( ) { } [ ]
        int state19(char c) {
            int nextState = 20;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            System.out.println(token.lexeme);

            i--;

            return nextState;
        }

        Token getLexeme(String fileStr) {
            lexeme = "";
            char c;
            currentState = 0;

            while (currentState != 20 && !pauseCompiling) {
                if (i < fileStr.length()) {
                    c = fileStr.charAt(i);
                    i++;
                } else {
                    c = '#';
                }

                if (isValid(c)) {
                    if (c == '\n')
                        lineCount++;
                    switch (currentState) {
                        case 0:
                            // System.out.println("estado 0");
                            currentState = state0(c);
                            break;
                        case 1:
                            // System.out.println("estado 1");
                            currentState = state1(c);
                            break;
                        case 2:
                            // System.out.println("estado 2");
                            currentState = state2(c);
                            break;
                        case 3:
                            // System.out.println("estado 3");
                            currentState = state3(c);
                            break;
                        case 4:
                            // System.out.println("estado 4");
                            currentState = state4(c);
                            break;
                        case 5:
                            // System.out.println("estado 5");
                            currentState = state5(c);
                            break;
                        case 6:
                            // System.out.println("estado 6");
                            currentState = state6(c);
                            break;
                        case 7:
                            // System.out.println("estado 7");
                            currentState = state7(c);
                            break;
                        case 8:
                            // System.out.println("estado 8");
                            currentState = state8(c);
                            break;
                        case 9:
                            // System.out.println("estado 9");
                            currentState = state9(c);
                            break;
                        case 10:
                            // System.out.println("estado 10");
                            currentState = state10(c);
                            break;
                        case 11:
                            // System.out.println("estado 11");
                            currentState = state11(c);
                            break;
                        case 12:
                            // System.out.println("estado 12");
                            currentState = state12(c);
                            break;
                        case 13:
                            // System.out.println("estado 13");
                            currentState = state13(c);
                            break;
                        case 14:
                            // System.out.println("estado 14");
                            currentState = state14(c);
                            break;
                        case 15:
                            // System.out.println("estado 15");
                            currentState = state15(c);
                            break;
                        case 16:
                            // System.out.println("estado 16");
                            currentState = state16(c);
                            break;
                        case 17:
                            // System.out.println("estado 17");
                            currentState = state17(c);
                            break;
                        case 18:
                            // System.out.println("estado 18");
                            currentState = state18(c);
                            break;
                        case 19:
                            // System.out.println("estado 19");
                            currentState = state19(c);
                            break;
                        case 20:
                            break;
                    }
                } else {
                    throwError("invalid_char");
                }

            }
            return currentToken;
        }
    }

    static class Parser {

        void checkToken(int expectedToken) {
            if (expectedToken == currentToken.token)
                lexer.getLexeme(fileStr);
            else
                System.out.println("token n esperado");
        }

        void START() {
            if (currentToken.token == 1 || currentToken.token == 2 || currentToken.token == 3 || currentToken.token == 4
                    || currentToken.token == 7) {
                DECL();
            } else
                System.out.println("fodase");
        }

        void DECL() {
            if (currentToken.token == tokenStr) {
                checkToken(tokenStr);
                DECL1();
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    DECL1();
                }
            } else if (currentToken.token == tokenConst) {
                checkToken(tokenConst);
                if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (currentToken.token == tokenEqual) {
                        checkToken(tokenEqual);
                        TIPO_DECL();
                    }
                }
            } else if (currentToken.token == tokenInt) {
                checkToken(tokenInt);
                DECL1();
                while (currentToken.token == tokenComma) {
                    System.out.println("entrou");
                    checkToken(tokenComma);
                    DECL1();
                }
            } else if (currentToken.token == tokenChar) {
                checkToken(tokenChar);
                DECL1();
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    DECL1();
                }
            } else if (currentToken.token == tokenFloat) {
                checkToken(tokenFloat);
                DECL1();
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    DECL1();
                }
            }
        }

        void DECL1() {
            if (currentToken.token == tokenId) {
                checkToken(tokenId);

                if (currentToken.token == tokenAtrib) {
                    checkToken(tokenAtrib);
                    TIPO_DECL();
                }
            }
        }

        void TIPO_DECL() {
            if (currentToken.token == tokenMinus) {
                checkToken(tokenMinus);
                if (currentToken.token == tokenValue) {
                    checkToken(tokenValue);
                }
            } else if (currentToken.token == tokenValue) {
                checkToken(tokenValue);
            }
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 1);
            symbolTable.put(reservedWords[i], symbol);
        }

        try {
            File file = new File("programa.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                fileStr += scanner.nextLine() + '\n';
            }

            System.out.println("String lida:\n" + fileStr);

            lexer.getLexeme(fileStr);

            parser.START();

            if (!pauseCompiling)
                System.out.println(lineCount + " linhas compiladas.");

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
