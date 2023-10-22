public class workTopic {
    private String name;
    private double numHours;
    private double hoursDone;

    public workTopic(String name, double numHours) {
        this.name = name;
        this.numHours = numHours;
        hoursDone = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getNumHours() {
        return numHours;
    }

    public void setNumHours(double numHours) {
        this.numHours = numHours;
    }

    public double getHoursDone() {
        return hoursDone;
    }

    public void setHoursDone(double hoursDone) {
        this.hoursDone = hoursDone;
    }

    public double getProgress() {
        return hoursDone / numHours;
    }
}
