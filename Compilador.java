/* 
    *   Trabalho Prático - Compiladores 2021/2
    *   GRUPO 9
    *   Bernardo Cerqueira de Lima      928061
    *   Henrique Dornas Mendes          1201827
    *   Matheus Teixeira Alves          636132
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class Compilador {

    /*
     * Declarações iniciais: 
     * symbolTable      -> Tabela de Símbolos 
     * reservedWords    -> Palavras reservadas da linguagem 
     * lineCount        -> Contador de linhas
     * pauseCompiling   -> Pausa compilação ao encontrar erro 
     * fileStr          -> String que compoe arquivo fonte 
     * lexer, parser    -> Analisador Lexico e Sintático
     * lineSeparator    -> Separador de linha dependendo do OS 
     * Tokens           -> Tokens da linguagem
     */
    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    static final String[] reservedWords = { "string", "const", "int", "char", "while", "if", "float", "else", "&&",
            "||", "!", "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}",
            "readln", "div", "write", "writeln", "mod", "[", "]" };

    static int lineCount = 1;
    static boolean pauseCompiling = false;
    static String fileStr = "";
    static Lexer lexer = new Lexer();
    static Parser parser = new Parser();
    static Token currentToken = null;
    static char lineSeparator = System.lineSeparator().charAt(0);

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

    // Classe dos Elementos da Tabela de Símbolos
    static class Symbol {
        String lexeme;
        int token;

        Symbol(String lex, int tk) {
            lexeme = lex;
            token = tk;
        }
    }

    // Classe dos Tokens encontrados
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

    /*
        Classe do Analisador Lexico 
    
        Variáveis locais: 
            lexeme          -> lexema da formação do token
            i               -> posição no arquivo fonte
            currentState    -> estado atual
    */
    static class Lexer {
        static String lexeme = "";
        static int i = 0;
        static int currentState = 0;

        /* 
            Metodo ThrowError -> Encerra o programa ao encontrar erro lexico
        */
        void throwError(String type) {
            pauseCompiling = true;
            currentToken = new Token("ERRO", 666, "ERRO");

            System.out.println(lineCount);

            // invalid character
            if (type == "invalid_char") {
                System.out.println("caractere invalido.");
            }
            // non identified lexeme
            else if (type == "invalid_lexeme") {
                if (lexeme.charAt(lexeme.length() - 1) == lineSeparator || lexeme.charAt(lexeme.length() - 1) == ';')
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                System.out.println("lexema nao identificado [" + lexeme + "].");
            }
            // unexpected EOF
            else if (type == "unexpected_eof") {
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        /*  
            Método isValid -> Verifica se caracter é válido
        */
        boolean isValid(char c) {
            if (Character.isDigit(c) || isLetter(c) || c == ' ' || c == '_' || c == '.' || c == ';' | c == ','
                    || c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '+'
                    || c == '-' || c == '\"' || c == '\'' || c == '/' || c == '*' || c == '|' || c == '\\' || c == '&'
                    || c == '%' || c == '!' || c == '?' || c == '>' || c == '<' || c == '=' || c == lineSeparator
                    || c == '#')
                return true;
            else
                return false;
        }

        /*  
            Método isLetter -> Verifica se caracter é letra
        */
        boolean isLetter(char c) {
            if ((c >= 'A' && c <= 'Z') || (c >= 97 && c <= 122))
                return true;
            else
                return false;
        }

        /*  
            Método isHexa -> Verifica se caracter é Hexadecimal
        */
        boolean isHexa(char c) {
            if (Character.isDigit(c) || (c >= 'A' && c <= 'F'))
                return true;
            else
                return false;
        }

        /*  
            Estado Inicial do Automato: Com base no primeiro caracter lido, define proximo estado.
        */
        int state0(char c) {
            int nextState = 0;

            lexeme += c;

            if (isLetter(c) || c == '_') {
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
            } else if (c == '#') {
                nextState = 20;
                currentToken = new Token("EOF", 667, "EOF");
            } else if (c == ';') {
                currentToken = new Token(lexeme, tokenSemiColon, "String");
                nextState = 20;
            } else if (c == ' ') {
                lexeme = "";
            } else if (c == lineSeparator) {
                lexeme = "";

                if (currentToken != null) {
                    if (currentToken.token == tokenSemiColon || currentToken.token == tokenOpenBra
                            || currentToken.token == tokenCloseBra)
                        lineCount++;
                } else {
                    lineCount++;
                }
            } else {
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 1 -> Trata de identificadores e palavras reservadas.
            Continua no mesmo estado enquanto for um caracter valido para estes tipos e length <= 32.
            Caso contrario, salva e vai pro estado final.
        */
        int state1(char c) {
            int nextState = 1;

            if (isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;

                if (lexeme.length() > 32)
                    throwError("invalid_lexeme");
            } else {
                Symbol symbol = symbolTable.get(lexeme);

                if (symbol != null) {
                    Token token = new Token(lexeme, symbol.token, "String");

                    currentToken = token;

                    // System.out.println(token.lexeme);

                } else {
                    Symbol newSymbol = new Symbol(lexeme, tokenId);
                    symbolTable.put(lexeme, newSymbol);

                    Token token = new Token(lexeme, tokenId, "String");
                    currentToken = token;

                    // System.out.println(token.lexeme);
                }

                nextState = 20;
                i--;
            }

            return nextState;
        }

        /*  
            Estado 2 -> Trata de inteiros.
            Continua no mesmo estado enquanto for digito.
            Caso leia . , vai pro estado 3.
            Caso contrario, salva e vai pro estado final.
        */
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

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        /*  
            Estado 3 -> ?. Trata de floats com so um ponto
            Caso leia digito, vai pro estado 4.
            Caso contrario, da erro de eof ou lexema invalido.
        */
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

        /*  
            Estado 4 -> ?.d Trata de floats com ponto e 1 digito
            Continua no mesmo estado enquanto c for digito
            Caso length > 7, erro de lexema invalido
            Caso contrario, salva e vai pro estado final.
        */
        int state4(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;

                if (lexeme.length() > 7)
                    throwError("invalid_lexeme");
            } else {
                Token token = new Token(lexeme, tokenValue, "Float");
                currentToken = token;

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        /*  
            Estado 5 -> Trata de <
            Estado final se c = - ou = ou diferente.
        */
        int state5(char c) {
            int nextState = 20;

            if (c == '-' || c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        /*  
            Estado 6 -> Trata de >
            Estado final se = ou diferente.
        */
        int state6(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        /*  
            Estado 7 -> Trata de !
            Estado final se = ou diferente.
        */
        int state7(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        /*  
            Estado 8 -> Trata de &
            Estado final se c = &, caso contrario erro de lexema invalido.
        */
        int state8(char c) {
            int nextState = 20;

            if (c == '&') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        /*  
            Estado 9 -> Trata de |
            Estado final se c = |, caso contrario erro de lexema invalido.
        */
        int state9(char c) {
            int nextState = 20;

            if (c == '|') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        /*  
            Estado 10 -> Trata de /
            Caso c = *, vai pro estado 11 e reinicia lexema. (comentario n eh token)
            Estado final se c != *
        */
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

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        /*  
            Estado 11 -> Trata de comentario /* 
            Caso EOF, erro
            else Caso c =  *, estado 12
        
            caso c = \n, aumenta contagem de linha
        */
        int state11(char c) {
            int nextState = 11;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '*')
                nextState = 12;

            if (c == lineSeparator)
                lineCount++;

            return nextState;
        }

        /*  
            Estado 12 -> Trata de comentario /* ? *
            Caso EOF, erro
            else Caso c = /, estado 0
            else caso c != *, estado 11
        
            caso c = \n, aumenta contagem de linha
        */
        int state12(char c) {
            int nextState = 12;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '/')
                nextState = 0;
            else if (c != '*')
                nextState = 11;

            if (c == lineSeparator)
                lineCount++;

            return nextState;
        }

        /*  
            Estado 13 -> Trata de ' (char)
            Caso EOF, erro
            else estado 14
        */
        int state13(char c) {
            int nextState = 14;

            if (c == '#') {
                throwError("unexpected_eof");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        /*  
            Estado 14 -> Trata de '? (char)
            Caso EOF, erro
            else Caso c != ', lexema invalido
            else estado final
        */
        int state14(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c != '\'') {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        /*  
            Estado 15 -> Trata de " (string)
            Caso \n, erro de lexema invalido
            Caso EOF, erro de eof
            else Caso c != ", continua no estado 15
            else Caso c = ", encerra string e estado final
        */
        int state15(char c) {
            int nextState = 15;

            if (c == lineSeparator) {
                throwError("invalid_lexeme");
            } else if (c == '#') {
                throwError("unexpected_eof");
            } else if (c == '\"') {
                lexeme += "0" + c;
                nextState = 20;

                Token token = new Token(lexeme, tokenValue, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                lexeme += c;
                if (lexeme.length() > 256)
                    throwError("invalid_lexeme");
            }

            return nextState;
        }

        /*  
            Estado 16 -> Trata de 0
            Caso c = digito, estado 2
            else c = x, estado 17
            else salva 0 e estado final
        */
        int state16(char c) {
            int nextState = 17;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == 'x') {
                lexeme += c;
            } else {
                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        /*  
            Estado 17 -> Trata de 0x (hexadecimal)
            Caso EOF, erro de eof
            else Caso c != Hexa, erro de lexema invalido
            else Estado 18
        */
        int state17(char c) {
            int nextState = 18;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        /*  
            Estado 18 -> Trata de 0xD (hexadecimal)
            Caso EOF, erro de eof
            else Caso c != Hexa, erro de lexema invalido
            else salva e estado final
        */
        int state18(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        /*  
            Estado 19 -> Trata de tokens = * + - , ( ) { } [ ]
            Salva e vai pro estado final.
        */
        int state19(char c) {
            int nextState = 20;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            i--;

            return nextState;
        }

        /* 
            Metodo getLexeme -> Roda o analisador léxico para encontrar um token
            Quando é chamado, se inicia no estado 0 e com lexema vazio.
            Continua tratando próximo caracter enquanto o estado é diferente de 20 (final) e não tenha erros.
            Caso o caracter seja invalido, acusa erro de caracter invalido.
            
            Caso chegue no estado final, retorna token global currentToken.
        
            ----- OBSERVAÇÃO -----
            Por convenção adotamos # como EOF, visto que java não possui EOF e # não é caracter válido.
        */
        Token getLexeme(String fileStr) {
            lexeme = "";
            char c;
            currentState = 0;

            while (currentState != 20 && !pauseCompiling) {
                if (i < fileStr.length()) {
                    c = fileStr.charAt(i);
                    i++;
                    if (c == '#') {
                        throwError("invalid_char");
                        break;
                    }
                } else {
                    c = '#';
                }

                if (isValid(c)) {
                    switch (currentState) {
                        case 0:
                            currentState = state0(c);
                            break;
                        case 1:
                            currentState = state1(c);
                            break;
                        case 2:
                            currentState = state2(c);
                            break;
                        case 3:
                            currentState = state3(c);
                            break;
                        case 4:
                            currentState = state4(c);
                            break;
                        case 5:
                            currentState = state5(c);
                            break;
                        case 6:
                            currentState = state6(c);
                            break;
                        case 7:
                            currentState = state7(c);
                            break;
                        case 8:
                            currentState = state8(c);
                            break;
                        case 9:
                            currentState = state9(c);
                            break;
                        case 10:
                            currentState = state10(c);
                            break;
                        case 11:
                            currentState = state11(c);
                            break;
                        case 12:
                            currentState = state12(c);
                            break;
                        case 13:
                            currentState = state13(c);
                            break;
                        case 14:
                            currentState = state14(c);
                            break;
                        case 15:
                            currentState = state15(c);
                            break;
                        case 16:
                            currentState = state16(c);
                            break;
                        case 17:
                            currentState = state17(c);
                            break;
                        case 18:
                            currentState = state18(c);
                            break;
                        case 19:
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

    /* 
        Classe do Analisador Sintático
    
        Apesar de não possuir variáveis locais, utiliza as seguintes variáveis globais:
            currentToken    -> Token atual no escopo global do programa
            pauseCompiling  -> Flag de erro global do Compilador
            token?          -> Tokens da linguagem
    
        ----- OBSERVAÇÃO -----
        Por convenção, EOF entra no analisador sintático como token 667.
        Na prática EOF não é token, mas nesta implementação utilizamos essa estratégia para identificar o tipo de erro sintático.
    
        O Parser segue a seguinte Gramática:
        (na implementação, simbolos não terminais foram traduzidos para INGLÊS para seguir a mesma convenção do restante do código)
    
            INÍCIO-> 	{D | C} eof
        
            DECLARAÇÃO-> 	(int | float | string | char) DECL1 {,DECL1};	|
                            const id = TIPO_DECL;
        
            DECL1-> 		id [<- TIPO_DECL ]
            TIPO_DECL-> 	[-]num | string | hexa | caractere
        
            COMANDO->	id ["[" EXP "]"] <- EXP;                    |
                        while EXP TIPO_CMD				            |
                        if EXP TIPO_CMD [else TIPO_CMD]	            |
                        readln "(" id ")";				            |
                        (write | writeln) "(" LISTA_EXP ")";		|
                        ;
        
            TIPO_CMD->	    COMANDO | "{" {COMANDO}+ "}"
            LISTA_EXP->	    EXP {, EXP}
            OPERADOR->    	= | != | < | > | <= | >=
        
            EXP-> 		EXP1 {OPERADOR EXP1}
            EXP1->		[-] EXP2 { (+ | - | "||") EXP2 }
            EXP2->		EXP3 { ("*" | && | / | div | mod) EXP3 }
            EXP3->		{!} EXP4
            EXP4->		(int | float) "(" EXP ")" | EXP5
            EXP5->     	"(" EXP ")" | id ["[" EXP "]"] | num
    */
    static class Parser {

        /* 
            Metodo throwParserError -> Acusa erro sintático e pausa compilador.
            Caso 667 (EOF), fim de arquivo nao esperado.
            Else token nao esperado.
        */
        void throwParserError() {
            if (currentToken.token != 667) {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("token nao esperado [" + currentToken.lexeme + "].");
            } else {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        /* 
            Metodo checkToken (CasaToken) -> Verifica se token atual é o token esperado.
            Caso iguais, roda analisador léxico para pegar próximo token.
            Caso diferentes, erro.
        */
        void checkToken(int expectedToken) {
            if (expectedToken == currentToken.token)
                lexer.getLexeme(fileStr);
            else {
                throwParserError();
            }
        }

        /* 
            Na gramática: INÍCIO-> 	{D | C} eof
        
            Metodo START -> Símbolo não terminal inicial da gramática. 
            Aceita declaração ou comando até EOF ou erro.
        */
        void START() {
            while (currentToken.token != 667 && !pauseCompiling) {
                if (currentToken.token == tokenStr || currentToken.token == tokenConst || currentToken.token == tokenInt
                        || currentToken.token == tokenChar || currentToken.token == tokenFloat) {
                    DECL();
                } else
                    COMMAND();
            }

        }

        /* 
            Na gramática: DECLARAÇÃO-> 	(int | float | string | char) DECL1 {,DECL1};	|
                                        const id = TIPO_DECL;
        
            Metodo DECL -> Símbolo não terminal de Declaração da gramática. 
            Caso inicio com (int | float | string | char), vai para DECL1 e pode rodar ,DECL1 0 ou + vezes depois.
            Caso inicio com const, proximos tokens são um identificador, token de igual e vai para TIPO_DECL.
        */
        void DECL() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenStr) {
                    checkToken(tokenStr);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenConst) {
                    checkToken(tokenConst);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenId) {
                        checkToken(tokenId);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenEqual) {
                            checkToken(tokenEqual);
                            if (pauseCompiling)
                                return;
                            DECL_TYPE();
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenInt) {
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenChar) {
                    checkToken(tokenChar);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenFloat) {
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else
                    throwParserError();

                if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                } else {
                    lineCount++;
                    throwParserError();
                }
            }
        }

        /* 
            Na gramática: DECL1-> 	id [<- TIPO_DECL ]
        
            Metodo DECL1 -> Símbolo não terminal auxiliar 1 para Declaração
            Le token identificador e opcionalmente pode ter uma atribuição <- TIPO_DECL
        */
        void DECL1() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;
                        DECL_TYPE();
                        if (pauseCompiling)
                            return;
                    }
                }
            }
        }

        /* 
            Na gramática: TIPO_DECL-> 	[-]num | string | hexa | caractere
        
            Metodo TIPO_DECL -> Símbolo não terminal para valores de variáveis de Declaração
            Lê opcionalmente um menos e depois é necessário um valor válido (num | string | hexa | caractere)
        */
        void DECL_TYPE() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                if (currentToken.token == tokenValue) {
                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        /* 
            Na gramática:
            COMANDO->	id ["[" EXP "]"] <- EXP;		            |
        	            while EXP TIPO_CMD				            |
                        if EXP TIPO_CMD [else TIPO_CMD]	            |
                        readln "(" id ")";				            |
                        (write | writeln) "(" LISTA_EXP ")";		|
                        ;
        
            Metodo COMANDO -> Símbolo não terminal para Comandos da linguagem
        
            1. Caso leia um token identificador, opcionalmente podera ler [EXP]. Em seguida, sera necessario um token de atribuicao, vai chamar EXP e finalmente um token de ponto e virgula.
            2. Caso leia um token while, sera chamado EXP e depois TIPO_CMD.
            3. Caso leia um token if, sera chamado EXP e depois TIPO_CMD. Opcionalmente pode-se ter um token else seguido de uma chamada TIPO_CMD.
            4. Caso leia um token readln, deverao aparecer os tokens (identificador), seguido de token ponto e virgula.
            5. Caso leia token write ou writeln, sera chamado LISTA_EXP dentro de tokens ( e ), seguido de token ponto e virgula.
            6. Caso leia token ponto e virgula, so chama o CasaToken mesmo.
            7. Caso contrario, erro.
        */
        void COMMAND() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenSq) {
                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenSemiColon) {
                            checkToken(tokenSemiColon);
                            if (pauseCompiling)
                                return;
                        } else {
                            lineCount++;
                            throwParserError();
                        }
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWhile) {
                    checkToken(tokenWhile);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenIf) {
                    checkToken(tokenIf);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                    // if (currentToken.token == tokenSemiColon) {
                    //     checkToken(tokenSemiColon);
                    //     if (pauseCompiling)
                    //         return;
                    // }
                    if (currentToken.token == tokenElse) {
                        checkToken(tokenElse);
                        if (pauseCompiling)
                            return;
                        CMD_TYPE();
                        if (pauseCompiling)
                            return;
                    }
                    // else
                    //     throwParserError();
                } else if (currentToken.token == tokenRead) {
                    checkToken(tokenRead);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenId) {
                            checkToken(tokenId);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenClosePar) {
                                checkToken(tokenClosePar);
                                if (pauseCompiling)
                                    return;
                                if (currentToken.token == tokenSemiColon) {
                                    checkToken(tokenSemiColon);
                                    if (pauseCompiling)
                                        return;
                                } else {
                                    lineCount++;
                                    throwParserError();
                                }
                            } else
                                throwParserError();
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWrite) {
                    checkToken(tokenWrite);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP_LIST();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenSemiColon) {
                                checkToken(tokenSemiColon);
                                if (pauseCompiling)
                                    return;
                            } else {
                                lineCount++;
                                throwParserError();
                            }
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWriteLn) {
                    checkToken(tokenWriteLn);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP_LIST();
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenSemiColon) {
                                checkToken(tokenSemiColon);
                                if (pauseCompiling)
                                    return;
                            } else {
                                lineCount++;
                                throwParserError();
                            }
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                    if (pauseCompiling)
                        return;
                } else {
                    throwParserError();
                }
            }
        }

        /* 
            Na gramática: TIPO_CMD->	C | "{" {C}+ "}"
        
            Metodo TIPO_CMD -> Símbolo não terminal para comando ou bloco de comandos.
            Caso token = {, devera rodar "{" {COMANDO}+ "}". (bloco)
            Caso contrario, so roda COMANDO.
        */
        void CMD_TYPE() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenOpenBra) {
                    checkToken(tokenOpenBra);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token != tokenCloseBra) {
                        COMMAND();
                        if (pauseCompiling)
                            return;
                    }
                    checkToken(tokenCloseBra);
                    if (pauseCompiling)
                        return;
                } else {
                    COMMAND();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: LISTA_EXP->	EXP {, EXP}
        
            Metodo LISTA_EXP -> Símbolo não terminal para lista de expressao.
            Caso token = {, devera rodar "{" {COMANDO}+ "}". (bloco)
            Caso contrario, so roda COMANDO.
        */
        void EXP_LIST() {
            if (!pauseCompiling) {
                EXP();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: OPERADOR->   = | != | < | > | <= | >=
        
            Metodo OPERADOR -> Símbolo não terminal para operadores.
            Caso token = (= | != | < | > | <= | >=), continua
            Caso seja diferente, erro
        */
        void OPERATOR() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenEqual) {
                    checkToken(tokenEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenDif) {
                    checkToken(tokenDif);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLess) {
                    checkToken(tokenLess);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtr) {
                    checkToken(tokenGtr);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLessEqual) {
                    checkToken(tokenLessEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtrEqual) {
                    checkToken(tokenGtrEqual);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        /* 
            Na gramática: EXP-> EXP1 {OPERADOR EXP1}
        
            Metodo EXP -> Símbolo não terminal para expressoes.
            Chama metodo EXP1 e pode rodar OPERADOR EXP1 opcionalmente, quantas vezes quiser.
        */
        void EXP() {
            if (!pauseCompiling) {
                EXP1();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenEqual || currentToken.token == tokenDif
                        || currentToken.token == tokenLess || currentToken.token == tokenGtr
                        || currentToken.token == tokenLessEqual || currentToken.token == tokenGtrEqual) {
                    OPERATOR();
                    if (pauseCompiling)
                        return;
                    EXP1();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP1-> [-] EXP2 { (+ | - | "||") EXP2 }
        
            Metodo EXP1 -> Símbolo não terminal auxiliar 1 para expressoes.
            Opcionalmente pode iniciar com token de menos. Chama EXP2 e pode opcionalmente rodar (+ | - | "||") EXP2, quantas vezes quiser.
        */
        void EXP1() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                EXP2();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenPlus || currentToken.token == tokenMinus
                        || currentToken.token == tokenOr) {
                    if (currentToken.token == tokenPlus) {
                        checkToken(tokenPlus);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenMinus) {
                        checkToken(tokenMinus);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenOr) {
                        checkToken(tokenOr);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                    EXP2();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP2-> EXP3 { ("*" | && | / | div | mod) EXP3 }
        
            Metodo EXP2 -> Símbolo não terminal auxiliar 2 para expressoes.
            Chama EXP3 e pode opcionalmente rodar ("*" | && | / | div | mod) EXP3, quantas vezes quiser.
        */
        void EXP2() {
            if (!pauseCompiling) {
                EXP3();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenMult || currentToken.token == tokenAnd
                        || currentToken.token == tokenDiv || currentToken.token == tokenMod) {
                    if (currentToken.token == tokenMult) {
                        checkToken(tokenMult);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenAnd) {
                        checkToken(tokenAnd);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenDiv) {
                        checkToken(tokenDiv);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenMod) {
                        checkToken(tokenMod);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                    EXP3();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP3-> {!} EXP4
        
            Metodo EXP3 -> Símbolo não terminal auxiliar 3 para expressoes.
            Pode iniciar com token !, e enquanto o proximo for igual a !, continua nesse loop. Depois chama EXP4.
        */
        void EXP3() {
            if (!pauseCompiling) {
                while (currentToken.token == tokenNot) {
                    checkToken(tokenNot);
                    if (pauseCompiling)
                        return;
                }
                EXP4();
                if (pauseCompiling)
                    return;
            }
        }

        /* 
            Na gramática: EXP4-> (int | float) "(" EXP ")" | EXP5
        
            Metodo EXP4 -> Símbolo não terminal auxiliar 4 para expressoes.
            Caso inicia com token int ou float, precisa de token (, depois chama EXP e volta para verificar token ). 
            Caso contrario, chama EXP5.
        */
        void EXP4() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenInt) {
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenFloat) {
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else {
                    EXP5();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP5->  "(" EXP ")" | id ["[" EXP "]"] | num
        
            Metodo EXP5 -> Símbolo não terminal auxiliar 5 para expressoes.
            Caso inicia com token = (, executa EXP e fecha o parenteses com token = ).
            Caso inicia com token identificador, pode opcionalmente ter tambem "[" EXP "]".
            Por ultimo, pode ser tambem um valor. 
            Else, erro.
        */
        void EXP5() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenOpenPar) {
                    checkToken(tokenOpenPar);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenClosePar) {
                        checkToken(tokenClosePar);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenSq) {
                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                } else if (currentToken.token == tokenValue) {
                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }
    }

    /* 
        Metodo main
        Inicia a tabela de simbolos e aloca as palavras reservadas com seus respectivos numeros de tokens.
        Inicia um scanner e le o arquivo fonte por linha, acrescentando \n no final de cada uma.
        Roda o analisador lexico uma vez para encontrar primeiro token.
        Roda o analisador sintatico, iniciando do estado inicial.
        Caso a compilacao obtenha sucesso, printa a mensagem de sucesso.
    */
    public static void main(String[] args) {

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 1);
            symbolTable.put(reservedWords[i], symbol);
        }

        // try {
        //     File file = new File("programa.in");
        //     Scanner scanner = new Scanner(file);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            fileStr += scanner.nextLine() + lineSeparator;
        }

        lexer.getLexeme(fileStr);

        parser.START();

        if (!pauseCompiling && lineCount != 1)
            System.out.println(lineCount + " linhas compiladas.");

        scanner.close();
        // } catch (FileNotFoundException e) {
        //     e.printStackTrace();
        // }
    }

}
