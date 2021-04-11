import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ExcelReader er = new ExcelReader();
        List<TraceLocation> list  = er.readExcel("./excel/GPS1.xls");

        AMapUtil au = new AMapUtil();
        List<TraceLocation> cleanList = au.filterData(list);
        ExcelWriter ew = new ExcelWriter();
        ew.Writer("./excel/CleanGPS1.xls",cleanList);

        ExcelReader er1 = new ExcelReader();
        List<TraceLocation> list1 = er1.readExcel("./excel/GPS2.xls");

        AMapUtil au1 = new AMapUtil();
        List<TraceLocation> cleanList1 = au1.filterData(list1);
        ExcelWriter ew1 = new ExcelWriter();
        ew1.Writer("./excel/CleanGPS2.xls",cleanList1);
    }
}
