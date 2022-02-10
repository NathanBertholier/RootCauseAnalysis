package fr.uge.modules.api.server.external.model;

import java.util.Arrays;

public record Proximity(long id_target, Link[] links) {
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Proximity proximity)) return false;
        else return proximity.id_target == id_target && Arrays.equals(proximity.links, links);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id_target) ^ Arrays.hashCode(links);
    }

    @Override
    public String toString() {
        return "Proximity{" +
                "id_target=" + id_target +
                ", links=" + Arrays.toString(links) +
                '}';
    }
}
