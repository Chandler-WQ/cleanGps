public class TraceLocation implements Cloneable {
    private double latitude;
    private double longitude;
    private long Time; //秒
    private double speed;
    private double cleanPro;
    private int type;//类型

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCleanPro() {
        return cleanPro;
    }

    public void setCleanPro(double cleanPro) {
        this.cleanPro = cleanPro;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return null;
    }

    @Override
    public String toString() {
        return "TraceLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", Time=" + Time +
                ", speed=" + speed +
                ", cleanPro=" + cleanPro +
                '}';
    }
}

