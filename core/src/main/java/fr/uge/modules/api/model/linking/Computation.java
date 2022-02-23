package fr.uge.modules.api.model.linking;

import java.math.BigDecimal;

public record Computation(String token_type, String value_log_first, String value_log_second, BigDecimal proximity) {
}
