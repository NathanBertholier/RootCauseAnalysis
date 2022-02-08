package fr.uge.modules.data.report;

public class ReportParameter {

    private int delta;
    private int proximityLimit;
    private int networkSize = 15;

    public ReportParameter(int delta, int proximityLimit, int networkSize) {
        this.delta = delta;
        this.proximityLimit = proximityLimit;
        this.networkSize = networkSize;
    }

    public ReportParameter(int delta, int proximityLimit) {
        this.delta = delta;
        this.proximityLimit = proximityLimit;
    }

    public int getDelta(){
        return delta;
    }

    public int getProximityLimit() {
        return proximityLimit;
    }

    public int getNetworkSize() {
        return networkSize;
    }
}

