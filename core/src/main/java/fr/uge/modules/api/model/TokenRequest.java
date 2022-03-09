package fr.uge.modules.api.model;

import java.util.List;

/**
 * Record representing a Tokens request according to the given parameters.
 * A TokenRequest contains the following parameters:
 * - init_datetime, a string representing the start time of the search
 * - end_datetime, a string representing the end time of the search
 * - id, a long value in the case the user wants to search tokens of a specific log
 * - tokens, a list a tokenmodel used to spell out the request
 * - rows, an int value representing the maximum number of the database queried logs
 */
public record TokenRequest(String init_datetime, String end_datetime, long id, List<TokenModel> tokens, int rows) {
}
