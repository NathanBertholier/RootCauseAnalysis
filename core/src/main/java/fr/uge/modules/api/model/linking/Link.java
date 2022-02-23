package fr.uge.modules.api.model.linking;

import java.math.BigDecimal;

public record Link(long id_source, long id_target, BigDecimal proximity) {
}
