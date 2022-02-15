package fr.uge.modules.linking.token;

import fr.uge.modules.linking.token.type.TokenType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(value, token.value) && Objects.equals(type, token.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
