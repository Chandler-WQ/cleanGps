import java.util.ArrayList;
import java.util.List;

public class AMapUtil {
    /**
     * 地球半径,单位 km
     */
    private static final double EARTH_RADIUS_KM = 6378.137;
    private static final double CLEAN_PRO = 0.75;
    private static final double CLEAN100 = 1.00;

    /**
     * 根据经纬度，计算两点间的距离
     *
     * @param longitude1 第一个点的经度
     * @param latitude1  第一个点的纬度
     * @param longitude2 第二个点的经度
     * @param latitude2  第二个点的纬度
     * @return 返回距离 单位米
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 纬度
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        // 经度
        double lng1 = Math.toRadians(longitude1);
        double lng2 = Math.toRadians(longitude2);
        // 纬度之差
        double a = lat1 - lat2;
        // 经度之差
        double b = lng1 - lng2;
        // 计算两点距离的公式
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        // 弧长乘地球半径, 返回单位: 米
        s = s * EARTH_RADIUS_KM * 1000;
        return s;
    }

    private static Boolean isFirst;     // 是否是第一次定位点
    private static int w1Count;  // 统计w1Count所统计过的点数
    private static double WALK_MAX_SPEED = 2.000; // 2m/s
    private static TraceLocation weight1;        // 权重点1
    private static TraceLocation weight2;  // 权重点2

    public static List filterData(List<TraceLocation> list) {
        //初始静态参数
        isFirst = true;
        w1Count = 0;
        weight1 = new TraceLocation();
        weight2 = null;
        List<TraceLocation> mListPoint = new ArrayList<>();
        // 权重点2
        List<TraceLocation> w1TempList = new ArrayList<>();     // w1的临时定位点集合
        List<TraceLocation> w2TempList = new ArrayList<>();     // w2的临时定位点集合

        //根据权重偏移做清洗
        int i = 0;
        for (; i < list.size(); i++) {
            filterPos(list, mListPoint, w1TempList, w2TempList, i);
        }

        //把遗留的概率较大的保留，较小的丢弃
        if (w2TempList.size() > w1TempList.size()) {
            mListPoint.addAll(w2TempList);
        } else if (w2TempList.size() == w1TempList.size()) {
            mListPoint.addAll(w1TempList);
            mListPoint.addAll(w2TempList);
        } else {
            mListPoint.addAll(w1TempList);
        }
        w1TempList.clear();
        w2TempList.clear();

        //优化根据海拔时间等做清洗
        List<TraceLocation> cleanListPoint = new ArrayList<>();
        for (TraceLocation location : mListPoint) {
            if (location.getCleanPro() < CLEAN_PRO) {
                cleanListPoint.add(location);
            }
        }

        return cleanListPoint;
    }

    /**
     * 过滤数据 将符合要求的点放在 mListPoint
     *
     * @param list
     * @param mListPoint
     * @param w1TempList
     * @param w2TempList
     */
    private static void filterPos(List<TraceLocation> list, List<TraceLocation> mListPoint, List<TraceLocation> w1TempList, List<TraceLocation> w2TempList, int index) {
        // 获取的第一个定位点不进行过滤
        TraceLocation aMapLocation = list.get(index);
        if (isFirst) {
            isFirst = false;
            weight1 = aMapLocation;
            // 将得到的第一个点存储入w1的缓存集合
            TraceLocation tem = (TraceLocation)weight1.clone();
            w1TempList.add(tem);
            w1Count++;
            return;
        }

        TraceLocation aPreMapLocation = list.get(index - 1);
        //如果与前一个点重复则标记即将删除
        if (aMapLocation.getLatitude() == aPreMapLocation.getLatitude() && aMapLocation.getLongitude() == aPreMapLocation.getLongitude()) {
            aMapLocation.setCleanPro(CLEAN100);
        }
        if (aMapLocation.getTime() == aPreMapLocation.getTime()) {
            aMapLocation.setCleanPro(CLEAN100);
            return;
        }

        if (weight2 == null) {
            // 计算w1与当前定位点p1的时间差并得到最大偏移距离D
            long offsetTimeMils = aMapLocation.getTime() - weight1.getTime();
            long offsetTimes = offsetTimeMils; //这是秒
            double MaxDistance = offsetTimes * WALK_MAX_SPEED;
            double distance = AMapUtil.getDistance(weight1.getLatitude(), weight1.getLongitude(),
                    aMapLocation.getLatitude(), aMapLocation.getLongitude());

            if (distance > MaxDistance) {
                // 将设置w2位新的点，并存储入w2临时缓存
                weight2 = aMapLocation;
                TraceLocation tem = (TraceLocation)weight2.clone();
                w2TempList.add(tem);
            } else {
                // 将p1加入到做坐标集合w1TempList
                TraceLocation tem = (TraceLocation)aMapLocation.clone();
                w1TempList.add(tem);
                w1Count++;
                // 更新w1权值点
                weight1.setLatitude(weight1.getLatitude() * 0.2 + aMapLocation.getLatitude() * 0.8);
                weight1.setLongitude(weight1.getLongitude() * 0.2 + aMapLocation.getLongitude() * 0.8);
                weight1.setTime(new Double(aMapLocation.getTime() * 0.8 + weight1.getTime() * 0.2).longValue());
                if (w1Count > 3) {
                    mListPoint.addAll(w1TempList);
                    w1TempList.clear();
                }
            }
            return;
        }

        // 计算w2与当前定位点p1的时间差并得到最大偏移距离D
        long offsetTimeMils = aMapLocation.getTime() - weight2.getTime();
        long offsetTimes = offsetTimeMils;
        double MaxDistance = offsetTimes * WALK_MAX_SPEED;
        double distance = AMapUtil.getDistance(
                weight2.getLatitude(), weight2.getLongitude(), aMapLocation.getLatitude(), aMapLocation.getLongitude());

        if (distance > MaxDistance) {
            w2TempList.clear();
            // 将设置w2位新的点，并存储入w2临时缓存
            weight2 = aMapLocation;
            TraceLocation tem = (TraceLocation)weight2.clone();
            w2TempList.add(tem);
            return;
        }

        // 将p1加入到做坐标集合w2TempList
        TraceLocation tem = (TraceLocation)aMapLocation.clone();
        w2TempList.add(tem);

        // 更新w2权值点
        weight2.setLatitude(weight2.getLatitude() * 0.2 + aMapLocation.getLatitude() * 0.8);
        weight2.setLongitude(weight2.getLongitude() * 0.2 + aMapLocation.getLongitude() * 0.8);
        weight2.setTime(new Double(aMapLocation.getTime() * 0.8 + weight2.getTime() * 0.2).longValue());

        if (w2TempList.size() > 2) {
            // 判断w1所代表的定位点数是否>2,小于说明w1之前的点为从一开始就有偏移的点
            if (w1TempList.size() > 2) {
                mListPoint.addAll(w1TempList);
            } else {
                w1TempList.clear();
            }
            // 将w2TempList集合中数据放入finalList中
            mListPoint.addAll(w2TempList);
            // 1、清空w2TempList集合 2、更新w1的权值点为w2的值 3、将w2置为null
            w2TempList.clear();
            weight1 = weight2;
            weight2 = null;
        }
    }
}