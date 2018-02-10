
package cn.neil.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * @author hzxiaozhikun
 */
public class GenHiveSql {
    private static final String DEFAULT_ENCODE = "UTF-8";
    
    private static final String tableFlag = "表名,类型,注释";
    
    private static final String DEFAULT_PARTITION = "PARTITIONED BY ( `ds` string COMMENT '日期')";
    

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws Exception {
//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\03 mid层整理 渠道\\渠道公共维.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\03 mid层整理 渠道\\";


        /**
         * mid层建表
         */
//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\mid层建表_原表.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\";

//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\mid层建表_原表_供应商系统.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\";

//          String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\mid层建表_原表_权限中心.xlsx";
//          String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-07-05 模块 实体 设计\\01 mid层整理 采购模块\\";

        /**
         * dw层
         */
        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-08-08 主题域\\2 供应商主题域\\dw层 建表sql等资料\\dw供应商主题域建表_原表.xlsx";
        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-08-08 主题域\\2 供应商主题域\\dw层 建表sql等资料\\";

//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-09-26  供应商评级\\供应商评级_dm_建表.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-09-26  供应商评级\\";


//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-09-15 供应商系统结算款项需求\\供应商系统结算款项需求_调整_确认_2017-10-26\\供应商系统结算款项需求_调整_确认_2017-10-26.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-09-15 供应商系统结算款项需求\\供应商系统结算款项需求_调整_确认_2017-10-26\\";

//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-10-15  po单审批流&调价流2017.11.15\\采购单审批流_dw_建表.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-10-15  po单审批流&调价流2017.11.15\\";
//        String srcFile = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-12-01 严选采购执行流数据主题域开发\\质检部分\\采购单审批流_dw_建表.xlsx";
//        String dstDir = "E:\\肖智坤\\MyWork_201607\\工作\\20170620 猛犸数据平台\\2017-12-01 严选采购执行流数据主题域开发\\质检部分\\";

        genHiveSql(srcFile, dstDir, 1, DEFAULT_ENCODE);

    }

    /**
     *
     * @param srcFile
     * @param dstDir
     * @param sheetNum  sheet号(默认为0)
     * @param encode
     * @throws Exception
     */
    public static void genHiveSql(String srcFile, String dstDir, int sheetNum, String encode)
        throws Exception {
        if(StringUtils.isEmpty(srcFile)){
            System.out.println("源文件不存在.");
        }
        File sourceFile = new File(srcFile);
        if(!sourceFile.exists()){
             System.out.println("源文件不存在.");
        }
        if(StringUtils.isEmpty(dstDir)){
            System.out.println("目标文件夹不存在.");
        }
        
        if(StringUtils.isEmpty(encode)){
            encode = DEFAULT_ENCODE;
        }
        
        Workbook book = null;
        book = getExcelWorkbook(srcFile);
        Sheet sheet = getSheetByNum(book, sheetNum);

        int lastRowNum = sheet.getLastRowNum();
        if(lastRowNum < 1){
            System.out.println("表信息不全!");
            return;
        }

        List<TableEntity> tables = new ArrayList<TableEntity>();
        TableEntity table = null;
        int f = 0;
        for (int i = 0; i <= lastRowNum; i++) {
            Row row = null;
            row = sheet.getRow(i);
            if (row != null) {
                int lastCellNum = row.getLastCellNum();
                Cell cell = null;
                if(lastCellNum < 3){
                    continue;
                }
                StringBuilder sbuil = new StringBuilder();
                for (int j = 0; j < 3; j++) {
                    cell = row.getCell(j);
                    String cellValue = null;
                    if (cell != null) {
                        cellValue = cell.getStringCellValue().trim();
                    } else {
                        cellValue = " ";
                    }
                    if(j > 0){
                        sbuil.append(",");
                    }
                    sbuil.append(cellValue);
                }
                
                if(tableFlag.equals(sbuil.toString())){
                    if(table != null){
                        tables.add(table);
                    }
                    table = new TableEntity();
                    f = 0;
                } else {
                    if(f == 1 && table != null){
                        //表名 + 表注释
                        String[] columns = sbuil.toString().split(",");
                        table.setTableName(columns[0]);
                        table.setTableCmt(columns[2]);
                    } else if(f > 2){
                        //字段 + 类型 + 注释
                        String[] columns = sbuil.toString().toLowerCase().split(",");
                        try {
                            if(columns.length == 0){
                                System.out.println("第" + i + "行为空行:" + columns.length + "表名:" + table.getTableName());
                            }else if(columns.length == 2){

                                table.addFields(new FieldEntity(columns[0], columns[1], columns[0]));
                            } else {
                                table.addFields(new FieldEntity(columns[0], columns[1], columns[2]));
                            }
                        } catch (Exception e) {
                            System.out.println("table name:" + table.getTableName() + ", column length:" + columns.length + "sbuil:" + sbuil);
                            e.printStackTrace();
                        }
                    }
                    
                    if(i == lastRowNum){
                        if(table != null){
                            tables.add(table);
                        }
                    } 
                }
                
                f++;
            }
        }
        String fName = sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf("."));
        write2LocalFile(tables, dstDir + fName + ".sql", encode);

    }
    
    private static void write2LocalFile(List<TableEntity> tables, String dstFile, String encode) throws Exception {
        if(tables == null || tables.isEmpty()){
            System.out.println("没有表信息");
            return;
        }
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFile), encode));
            System.out.println("-------------- 共计:" + tables.size() + "张表  ---------------");
            for (TableEntity table: tables) {
                //重复了列名的输出
                Map<String, Integer> fieldsMap = new HashMap<String, Integer>();

                List<FieldEntity> fields = table.getFields();
                if(fields == null || fields.isEmpty()){
                    System.out.println("表:" + table.getTableName() + " 没有字段.");
                    continue;
                }
                
                bw.write(" CREATE TABLE `" + table.getTableName() + "` (");
                bw.newLine();
                int index = 0;
                for (FieldEntity fieldEntity: fields) {
                    if(index > 0){
                       bw.append(",");
                       bw.newLine();
                    }
                    Integer times = fieldsMap.get(fieldEntity.getField());
                    if(times == null){
                        fieldsMap.put(fieldEntity.getField(), 1);
                    } else {
                        fieldsMap.put(fieldEntity.getField(), times+1);
                    }

                    StringBuilder sbuil = new StringBuilder("`");
                    sbuil.append(fieldEntity.getField()).append("` ");
                    sbuil.append(fieldEntity.getFieldType()).append(" COMMENT '");
                    sbuil.append(fieldEntity.getFieldCmt()).append("'");
                    bw.write(sbuil.toString());
                    index ++;
                }
                bw.newLine();
                bw.write(") COMMENT '" + table.getTableCmt() + "'");
                bw.newLine();
                //分区
                bw.write(DEFAULT_PARTITION);
                bw.newLine();
                bw.write("ROW FORMAT SERDE 'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe'");
                bw.newLine();
                bw.write("STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat'");
                bw.newLine();
                bw.write("OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'; ");
                bw.newLine();
                bw.newLine();
                bw.flush();

                System.out.println("输出重复的列名,表名:" + table.getTableName());
                for (Map.Entry<String,Integer> entry:fieldsMap.entrySet()) {
                    Integer value = entry.getValue();
                    if(value.intValue() > 1) {
                        System.out.println("            " + entry.getKey());
                    }
                }
                System.out.println("                     --                                    ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(bw != null){
                bw.close();
            }
        }

    }

    private static Workbook getExcelWorkbook(String filePath)
        throws IOException {
        Workbook book = null;
        File file = null;
        FileInputStream fis = null;

        try {
            file = new File(filePath);
            if (!file.exists()) {
                throw new RuntimeException("源文件不存在");
            } else {
                fis = new FileInputStream(file);
                book = WorkbookFactory.create(fis);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return book;
    }
    
    private static Sheet getSheetByNum(Workbook book,int number){  
        Sheet sheet = null;  
        try {  
            sheet = book.getSheetAt(number);  
        } catch (Exception e) {  
            throw new RuntimeException(e.getMessage());  
        }  
        return sheet;  
    }  
    
    static class TableEntity {
        private String tableName;
        private String tableCmt;
        private List<FieldEntity> fields = new ArrayList<FieldEntity>();
        /**
         * @return the tableName
         */
        public String getTableName() {
            return tableName;
        }
        /**
         * @param tableName the tableName to set
         */
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }
        /**
         * @return the tableCmt
         */
        public String getTableCmt() {
            return tableCmt;
        }
        /**
         * @param tableCmt the tableCmt to set
         */
        public void setTableCmt(String tableCmt) {
            this.tableCmt = tableCmt;
        }
        /**
         * @return the fields
         */
        public List<FieldEntity> getFields() {
            return fields;
        }
        /**
         * @param field the field to set
         */
        public void addFields(FieldEntity field) {
            this.fields.add(field);
        }
        
        
    }
    
    static class FieldEntity {
        private String field;
        private String fieldType;
        private String fieldCmt;
        
        FieldEntity(String field, String fieldType, String fieldCmt){
            this.field = field;
            this.fieldType = fieldType;
            this.fieldCmt = fieldCmt;
        }

        /**
         * @return the field
         */
        public String getField() {
            return field;
        }

        /**
         * @param field the field to set
         */
        public void setField(String field) {
            this.field = field;
        }

        /**
         * @return the fieldType
         */
        public String getFieldType() {
            return fieldType;
        }

        /**
         * @param fieldType the fieldType to set
         */
        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        /**
         * @return the fieldCmt
         */
        public String getFieldCmt() {
            return fieldCmt;
        }

        /**
         * @param fieldCmt the fieldCmt to set
         */
        public void setFieldCmt(String fieldCmt) {
            this.fieldCmt = fieldCmt;
        }
        
        
    }

}
