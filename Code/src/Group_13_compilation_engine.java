import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Group_13_compilation_engine {
    private Group_13_Tokenizer tokenizer;
    private File file;
    private FileWriter output;
    private String indentation;
    Group_13_compilation_engine(String filename)
            throws IOException {
        file = new File(filename);
        output = new FileWriter(file);
        tokenizer = new Group_13_Tokenizer(filename.split("[.]")[0] + ".jack");
        indentation = "";
    }

    void compileClass() throws IOException {
        tokenizer.advance();
        if (tokenizer.tokenType() == Group_13_Constants.KEYWORD
                && tokenizer.keyword() == Group_13_Constants.CLASS) {
            output.write(indentation + "<class>\n");
            indentation += "  ";   // increase indentation
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if (tokenizer.tokenType() == Group_13_Constants.IDENTIFIER) {
                output.write(this.indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            } else {
                System.out.println("Error in class name");
                return;
            }
            if (tokenizer.tokenType() == Group_13_Constants.SYMBOL) {
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            } else {
                System.out.println("Error in bracket of class");
                return;
            }
            
            compileClassVariables();
            compileMethods();
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            indentation = indentation.substring(0, indentation.length() - 2);  
                                            // decrease indentation
            output.write(indentation + "</class>\n");
        }else {
            System.out.println("There's Error\n");
            return ;
        }
    }
    private void compileClassVariables() throws IOException{
        
        while(tokenizer.keyword() == Group_13_Constants.STATIC
                || tokenizer.keyword() == Group_13_Constants.FIELD){
            output.write(indentation + "<classVarDec>\n");
            indentation += "  ";
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if (tokenizer.keyword() == Group_13_Constants.BOOLEAN 
                    || tokenizer.keyword() == Group_13_Constants.INT 
                    || tokenizer.keyword() == Group_13_Constants.CHAR
                    || tokenizer.tokenType() == Group_13_Constants.IDENTIFIER) {
                output.write(indentation + tokenizer.getTag() + "\n");
            } else {
                System.out.println("ERROR in data type");
                return;
            }
            tokenizer.advance();
            if (tokenizer.tokenType() == Group_13_Constants.IDENTIFIER) {
                output.write(indentation + tokenizer.getTag() + "\n");
            } else {
                System.out.println("ERROR in variable name");
                return;
            }
            tokenizer.advance(); // handling ; and ,
            while (tokenizer.tokenType() == Group_13_Constants.SYMBOL) {
                if(tokenizer.getToken().equals(";")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    break;
                }else if(tokenizer.getToken().equals(",")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                        output.write(indentation + tokenizer.getTag() + "\n");
                    }else{
                        System.out.println("ERROR in variable name");
                        return;
                    }
                }
                else {
                    System.out.println("ERROR in semicolon");
                    return;
                }
                tokenizer.advance();
            }    
            
            indentation = indentation.substring(0, indentation.length() - 2);
            output.write(indentation + "</classVarDec>\n");
        }
        
    }
    private void compileMethods() throws IOException{
        while(tokenizer.keyword() == Group_13_Constants.FUNCTION
                || tokenizer.keyword() == Group_13_Constants.METHOD
                || tokenizer.keyword() == Group_13_Constants.CONSTRUCTOR){
            output.write(indentation + "<subroutineDec>\n");
            indentation += "  ";
            
            // header of function
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER
                    || tokenizer.tokenType() == Group_13_Constants.KEYWORD){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error invalid function return type");
                return ;
            }
            if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error invalid function name");
                return ;
            }
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol (");
                return ;
            }
            // parameters of function
            output.write(indentation + "<parameterList>\n");
            indentation += "  ";
            while(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER
                || tokenizer.tokenType() == Group_13_Constants.KEYWORD){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
                if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                }else{
                    System.out.println("Error invalid parameter");
                    return ;
                }
                if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                    if(tokenizer.symbol().equals(")")){
                        break;
                    }
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                }else{
                    System.out.println("Error invalid parameter");
                    return ;
                }
            }
            indentation = indentation.substring(0,indentation.length() - 2);
            output.write(indentation + "</parameterList>\n");
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol )");
                return ;
            }
            output.write(indentation + "<subroutineBody>\n");
            indentation += "  ";
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol {");
                return ;
            }
            compileVarDecs();
            compileStatements();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error ,there's no symbol }");
                return ;
            }
            indentation = indentation.substring(0,indentation.length() - 2);
            output.write(indentation + "</subroutineBody>\n");
            indentation = indentation.substring(0,indentation.length() - 2);
            output.write(indentation + "</subroutineDec>\n");
        }
        System.out.println("there is no more functions" + tokenizer.keyword());
    }
    void compileVarDecs() throws IOException{
        while(tokenizer.keyword() == Group_13_Constants.VAR){
            output.write(indentation + "<varDec>\n");
            indentation += "  ";
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if(tokenizer.keyword() == Group_13_Constants.CHAR
            || tokenizer.keyword() == Group_13_Constants.BOOLEAN
            || tokenizer.tokenType() == Group_13_Constants.IDENTIFIER
            || tokenizer.keyword() == Group_13_Constants.INT){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error in the datatype inside function");
                return ;
            }
            if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error in the name of a function");
                return ;
            }
            while (tokenizer.tokenType() == Group_13_Constants.SYMBOL) {
                if(tokenizer.getToken().equals(";")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    break;
                }else if(tokenizer.getToken().equals(",")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                        output.write(indentation + tokenizer.getTag() + "\n");
                    }else{
                        System.out.println("ERROR in variable name");
                        return;
                    }
                }
                else {
                    System.out.println("ERROR in semicolon");
                    return;
                }
                tokenizer.advance();
            }  
            indentation = indentation.substring(0, indentation.length() - 2);
            output.write(indentation + "</varDec>\n");
        }
    }
    void compileStatements() throws IOException{
        
        output.write(indentation + "<statements>\n");
        indentation += "  ";
        while(tokenizer.keyword() == Group_13_Constants.RETURN
        || tokenizer.keyword() == Group_13_Constants.DO
        || tokenizer.keyword() == Group_13_Constants.LET
        || tokenizer.keyword() == Group_13_Constants.IF
        || tokenizer.keyword() == Group_13_Constants.WHILE){
            
            if(tokenizer.keyword() == Group_13_Constants.RETURN){
                compileReturnStatement();
            }else if(tokenizer.keyword() == Group_13_Constants.DO){
                compileDoStatement();
            }else if(tokenizer.keyword() == Group_13_Constants.LET){ // let statement
                compileLetStatement();
            }else if(tokenizer.keyword() == Group_13_Constants.IF){
                compileIfStatement();
            }else if(tokenizer.keyword() == Group_13_Constants.WHILE){
                compileWhileStatement();
            }else {
                System.out.println("Invalid statement");
                return ;
            }
        }
                
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</statements>\n");
    }
    private void compileWhileStatement() throws IOException{
        output.write(indentation + "<whileStatement>\n");
        indentation += "  ";
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol (");
            return ;
        }
        compileExpression();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol )");
            return ;
        }
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        compileStatements();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</whileStatement>\n");
    }
    private void compileIfStatement() throws IOException{
        output.write(indentation + "<ifStatement>\n");
        indentation += "  ";
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol (");
            return ;
        }
        compileExpression();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol )");
            return ;
        }
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        compileStatements();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
        // else statement
        if(tokenizer.keyword() == Group_13_Constants.ELSE){
            compileElseStatement();
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</ifStatement>\n");
    }
    private void compileElseStatement() throws IOException{
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
            && tokenizer.symbol().equals("{")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol {");
            return ;
        }
        compileStatements();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
            && tokenizer.symbol().equals("}")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol }");
            return ;
        }
    }
    private void compileDoStatement() throws IOException{
        output.write(indentation + "<doStatement>\n");
        indentation += "  ";
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the name of the first indentifier");
            return ;
        }
        if(tokenizer.symbol().equals("[")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileExpression();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
            && tokenizer.symbol().equals("]")){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error invalid parameter");
                return ;
            }
        }
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            String symb = new String(tokenizer.symbol());
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if(symb.equals(".")){
                if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                }else{
                    System.out.println("Error in the name of a fun");
                    return ;
                }
                if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                }else{
                    System.out.println("Error invalid parameter");
                    return ;
                }
                
            }else if(!symb.equals("(")){
                System.out.println("Error invalid parameter");
                return ;
            }
        }else{
            System.out.println("Error in the semicolon inside function");
            return ;
        }
        
        compileExpressionList();
        
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol )");
            return ;
        }
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the symbol ;");
            return ;
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</doStatement>\n");
    }
    private void compileLetStatement() throws IOException{
        output.write(indentation + "<letStatement>\n");
        indentation += "  ";
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("the identifier is invalid");
            return ;
        }
        if(tokenizer.symbol().equals("[")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileExpression();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
            && tokenizer.symbol().equals("]")){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error invalid parameter");
                return ;
            }
        }
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the semicolon inside function");
            return ;
        }
        compileExpression();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
                && tokenizer.symbol().equals(";")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else{
            System.out.println("Error in the semicolon inside function");
            return ;
        }
        indentation = indentation.substring(0, indentation.length() - 2);
        output.write(indentation + "</letStatement>\n");
    }
    private void compileReturnStatement() throws IOException{
        output.write(indentation + "<returnStatement>\n");
        indentation += "  ";
        output.write(indentation + tokenizer.getTag() + "\n");
        tokenizer.advance();
        if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER
                || tokenizer.tokenType() == Group_13_Constants.KEYWORD){
            compileExpression();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{
                System.out.println("Error in the semicolon inside return");
                return ;
            }
        }else{
            System.out.println("Error in the semicolon inside return");
            return ;
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</returnStatement>\n");
    }
    private void compileExpressionList() throws IOException{
        output.write(indentation + "<expressionList>\n");
        indentation += "  ";
        while(tokenizer.tokenType() != Group_13_Constants.SYMBOL
                || tokenizer.symbol().equals("(")){
            compileExpression();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                if(tokenizer.symbol().equals(")")){
                    break;
                }
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
                
            }else{
                System.out.println("Error in the semicolon inside function");
                return ;
            }
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</expressionList>\n");
    }
    private void compileExpression() throws IOException{
        output.write(indentation + "<expression>\n");
        indentation += "  ";
        compileTerm();
        while (tokenizer.symbol().equals("+") || tokenizer.symbol().equals("-") 
            || tokenizer.symbol().equals("*") || tokenizer.symbol().equals("/") ||
            tokenizer.symbol().equals("&amp;") || tokenizer.symbol().equals("|") || 
            tokenizer.symbol().equals("&lt;") || tokenizer.symbol().equals("&gt;") ||
            tokenizer.symbol().equals("=")) {
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileTerm();
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</expression>\n");
    }
    private void compileTerm() throws IOException{
        output.write(indentation + "<term>\n");
        indentation += "  ";
        if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER
            || tokenizer.tokenType() == Group_13_Constants.KEYWORD){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            if(tokenizer.tokenType() == Group_13_Constants.SYMBOL ){
                String symb = new String(tokenizer.symbol());
                if(symb.equals(".")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    if(tokenizer.tokenType() == Group_13_Constants.IDENTIFIER){
                        output.write(indentation + tokenizer.getTag() + "\n");
                        tokenizer.advance();
                    }else{
                        System.out.println("Error in the name of a fun");
                        return ;
                    }
                    if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                        output.write(indentation + tokenizer.getTag() + "\n");
                        tokenizer.advance();
                    }else{
                        System.out.println("Error invalid parameter");
                        return ;
                    }
                    compileExpressionList();
                    if(tokenizer.tokenType() == Group_13_Constants.SYMBOL){
                        output.write(indentation + tokenizer.getTag() + "\n");
                        tokenizer.advance();
                    }else{
                        System.out.println("Error invalid parameter");
                        return ;
                    }
                }else if(symb.equals("[")){
                    output.write(indentation + tokenizer.getTag() + "\n");
                    tokenizer.advance();
                    compileExpression();
                    if(tokenizer.symbol().equals("]")){
                        output.write(indentation + tokenizer.getTag() + "\n");
                        tokenizer.advance();
                    }else{
                        System.out.println("Error in ] ");
                        return ;
                    }
                }
            }
        }else if(tokenizer.tokenType() == Group_13_Constants.STRING_CONST){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Group_13_Constants.INT_CONST){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
        }else if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
                && tokenizer.symbol().equals("(")){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileExpression();
            if(tokenizer.symbol().equals(")")){
                output.write(indentation + tokenizer.getTag() + "\n");
                tokenizer.advance();
            }else{ 
                System.out.println("Error in )");
                return ;
            }
        }else if(tokenizer.tokenType() == Group_13_Constants.SYMBOL
                && (tokenizer.symbol().equals("-") || tokenizer.symbol().equals("~"))){
            output.write(indentation + tokenizer.getTag() + "\n");
            tokenizer.advance();
            compileTerm();
        }else{
            System.out.println("Error in the name of a function");
            return ;
        }
        indentation = indentation.substring(0,indentation.length() - 2);
        output.write(indentation + "</term>\n");
    }
    
    void close() throws IOException{
        tokenizer.close();
        output.close();
    }
}