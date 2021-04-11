# cleanGps
gps定位点纠偏

本文参考https://blog.csdn.net/hello_json/article/details/79984081实现模型

清洗方法

```$xslt
 ExcelReader er = new ExcelReader();
        List<TraceLocation> list  = er.readExcel("原始文件路径");

        AMapUtil au = new AMapUtil();
        List<TraceLocation> cleanList = au.filterData(list);
        ExcelWriter ew = new ExcelWriter();
        ew.Writer("清洗后的文件路径",cleanList);
```
