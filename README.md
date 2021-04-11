# cleanGps
gps定位点纠偏

本文主要实现https://blog.csdn.net/hello_json/article/details/79984081模型

```$xslt
 ExcelReader er = new ExcelReader();
        List<TraceLocation> list  = er.readExcel("原始文件路径");

        AMapUtil au = new AMapUtil();
        List<TraceLocation> cleanList = au.filterData(list);
        ExcelWriter ew = new ExcelWriter();
        ew.Writer("清洗后的文件路径",cleanList);
```
