package fr.uge.modules.linking.strategy;

import fr.uge.modules.api.model.linking.Computation;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Strategy used to define proximity according to the average computations ones
 */
public class AverageStrategy implements ProximityStrategy {
    @Override
    public double computeProximity(List<Computation> computations) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);
        format.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(format.format(computations.stream().mapToDouble(Computation::proximity).sum() / computations.size()).replace(",", "."));
    }
}
