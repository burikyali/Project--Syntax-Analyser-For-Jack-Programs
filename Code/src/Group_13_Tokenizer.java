import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Group_13_Tokenizer {
     int cursor;
     boolean insideComment;
     String fileContent;
     String currentToken;
     HashMap<String, String> map; // the token and its type.

     File file;
     Scanner scan;
    //private FileWriter writer;
    Group_13_Tokenizer(String filename)
            throws IOException {
        insideComment = false;
        file = new File(filename);
        scan = new Scanner(file);
        currentToken = "";
        fileContent = "";
        cursor = 0;
        map = new HashMap<>();
        
        while (scan.hasNext()) {
            String temp = scan.nextLine();
            if(temp.length() > 2 && temp.substring(0,2).equals("//")){
                continue;
            }else if(temp.contains("//")){
                temp = temp.substring(0,temp.indexOf("//"));
            }
            fileContent = fileContent.concat(temp);
        }
        scan = new Scanner(new File(Group_13_Constants.TABLE_SRC));
        while (scan.hasNext()) {
            String token = scan.next();
            String type = scan.next();
            map.put(token, type);
        }
        
    }

    public boolean hasMoreTokens() throws IOException {
        return currentToken.trim().length() > 0;
    }

    public String getToken() { return currentToken.trim(); }

    public void advance() throws IOException {
        currentToken = "";
        boolean insideString = false;
        while (cursor < fileContent.length()) {
            
            if (!insideComment && fileContent.charAt(cursor) == '/'
                    && fileContent.charAt(cursor + 1) == '*') {
                insideComment = true;
                cursor += 2;
                continue;
            } else if (insideComment && fileContent.charAt(cursor) == '*'
                    && fileContent.charAt(cursor + 1) == '/') {
                insideComment = false;
                cursor += 2;
                continue;
            } else if (insideComment) {
                cursor++;
                continue;
            }
            if (!insideString && fileContent.charAt(cursor) == '"') {
                //if(!currentToken.equals("")) break;
                insideString = true;
                currentToken += fileContent.charAt(cursor);
                cursor ++;
                continue;
            }else if (insideString && fileContent.charAt(cursor) == '"') {
                insideString = false;
                currentToken += fileContent.charAt(cursor);
                cursor ++;
                break;
            }else if (insideString) {
                currentToken += fileContent.charAt(cursor);
                cursor++;
                continue;
            }
            if (fileContent.charAt(cursor) == ' '
                    || fileContent.charAt(cursor) == '\t') {
                cursor++;
                continue;
            }
            char tmp = fileContent.charAt(cursor);
            if (map.containsKey(String.valueOf(tmp))) { // symbol
                if (map.get(String.valueOf(tmp)).equals("symbol")
                        && !currentToken.equals("")) {
                    break;
                } else if (map.get(String.valueOf(tmp)).equals("symbol")) {
                    currentToken = currentToken + String.valueOf(tmp);
                    cursor++;
                    break;
                }
            }
            currentToken = currentToken + String.valueOf(tmp);
            if (map.containsKey(currentToken)) {
                if (map.get(currentToken).equals("keyword")) {
                    cursor++;
                    break;
                }
            }
            cursor++;
            if(cursor < fileContent.length() && fileContent.charAt(cursor) == ' '){
                break;
            }
        }

    }

    private boolean isNumeric(String token) {
        token = token.trim();
        for (int i = 0; i < token.length(); i++) {
            if (token.charAt(i) < '0' || token.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public int tokenType() {
        currentToken = currentToken.trim();
        int sz = currentToken.length();
        if (map.containsKey(currentToken)) {
            if (map.get(currentToken).equals("keyword")) {
                return Group_13_Constants.KEYWORD;
            } else if (map.get(currentToken).equals("symbol")) {
                return Group_13_Constants.SYMBOL;
            }
        } else if (isNumeric(currentToken)) {
            return Group_13_Constants.INT_CONST;
        } else if (currentToken.charAt(0) == '"'
                && currentToken.charAt(sz - 1) == '"') {
            return Group_13_Constants.STRING_CONST;
        }
        
        return Group_13_Constants.IDENTIFIER;
    }
    
    public int keyword(){
        if (currentToken.equals("class")) { return Group_13_Constants.CLASS; }
        else if (currentToken.equals("method")) { return Group_13_Constants.METHOD; }
        else if (currentToken.equals("function")) { return Group_13_Constants.FUNCTION; }
        else if (currentToken.equals("constructor")) { return Group_13_Constants.CONSTRUCTOR; }
        else if (currentToken.equals("int")) { return Group_13_Constants.INT; }
        else if (currentToken.equals("boolean")) { return Group_13_Constants.BOOLEAN; }
        else if (currentToken.equals("char")) { return Group_13_Constants.CHAR; }
        else if (currentToken.equals("void")) { return Group_13_Constants.VOID; }
        else if (currentToken.equals("var")) { return Group_13_Constants.VAR; }
        else if (currentToken.equals("static")) { return Group_13_Constants.STATIC; }
        else if (currentToken.equals("field")) { return Group_13_Constants.FIELD; }
        else if (currentToken.equals("let")) { return Group_13_Constants.LET; }
        else if (currentToken.equals("do")) { return Group_13_Constants.DO; }
        else if (currentToken.equals("if")) { return Group_13_Constants.IF; }
        else if (currentToken.equals("else")) { return Group_13_Constants.ELSE; }
        else if (currentToken.equals("while")) { return Group_13_Constants.WHILE; }
        else if (currentToken.equals("return")) { return Group_13_Constants.RETURN; }
        else if (currentToken.equals("true")) { return Group_13_Constants.TRUE; }
        else if (currentToken.equals("false")) { return Group_13_Constants.FALSE; }
        else if (currentToken.equals("null")) { return Group_13_Constants.NULL; }
        else if (currentToken.equals("this")) { return Group_13_Constants.THIS; }
        else { return -1; }
    }
    
    public String symbol(){
        if (tokenType() != Group_13_Constants.SYMBOL) { return "Error"; }
        if (currentToken.equals("<")) { return "&lt;"; }
        else if (currentToken.equals(">")) { return "&gt;"; }
        else if (currentToken.equals("&")) { return "&amp;"; }
        else { return new String(currentToken); }
    }
    
    public String stringVal(){
        if (tokenType() != Group_13_Constants.STRING_CONST) { return "ERROR"; }
        return currentToken.replace("\"", "");
    }
    
    public String identifier(){
        if (tokenType() != Group_13_Constants.IDENTIFIER) { return "ERROR"; }
        return currentToken;
    }
    
    private String getTypeText(){
        if(tokenType() == Group_13_Constants.KEYWORD) return "keyword";
        else if(tokenType() == Group_13_Constants.IDENTIFIER) return "identifier";
        else if(tokenType() == Group_13_Constants.SYMBOL) return "symbol";
        else if(tokenType() == Group_13_Constants.INT_CONST) return "integerConstant";
        else if(tokenType() == Group_13_Constants.STRING_CONST) return "stringConstant";
        return "Error";
    }
    
    public String getTag(){
        String temp = currentToken;
        if(getTypeText().equals("stringConstant")){
            temp = currentToken.substring(1,currentToken.length() - 1);
        }else if(getTypeText().equals("symbol")){
            temp = this.symbol();
        }
        String text = ("<" + getTypeText() + "> " + temp + " </" + getTypeText() + ">");
        return text;
    }
    
    public void close() throws IOException{
        scan.close();
    }
}

