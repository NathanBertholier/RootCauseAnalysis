package fr.uge.modules.api.model.report;

/**
 * Record representing a report parameters generation.
 * This object is used when receiving a generation request, defining which kind of report a user wants,
 * deserialized from the /report body json object.
 * A ReportParameter contains the following parameters:
 * - Expanded: generated report will or will not contain detailed proximity computations
 * - Delta: time variable defining the amount of time from which the logs fetch will start
 * - Proximity_limit: the minimum proximity value of the related logs. Every log related to the targeted one that is below this value will not appear in the generated report.
 * - Network_size: the maximum amount of logs contained in the final generated report
 */
public record ReportParameter(boolean expanded, long delta, float proximity_limit, int network_size) {

}
