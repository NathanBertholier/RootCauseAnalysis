package fr.uge.modules.api.model;

/**
 * Record used to spell out a Tokens request, containing the specific type of requested tokens and their values
 */
public record TokenModel(String token_type, String token_value) {
}
