package fr.uge.modules.api.model;

import java.util.Objects;

public record TokenModel(String token_type, String token_value) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenModel that = (TokenModel) o;
        return Objects.equals(token_type, that.token_type) && Objects.equals(token_value, that.token_value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token_type, token_value);
    }
}
