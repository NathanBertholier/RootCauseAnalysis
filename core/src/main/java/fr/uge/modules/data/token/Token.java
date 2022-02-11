package fr.uge.modules.data.token;

import fr.uge.modules.data.token.type.TokenType;

public class Token {
    private String value = null;
    private final TokenType type;

    public Token(String token, TokenType type) {
        this.value = token;
        this.type = type;
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public void setValue(String token){
        value = token;
    }

    public String getValue(){
        return value;
    }
    public TokenType getType(){
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("Token type : ").append(type.getName());
        sb.append("; Value : ").append(value);
        return sb.toString();
    }

}
