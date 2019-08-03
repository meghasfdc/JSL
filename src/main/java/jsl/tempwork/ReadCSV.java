/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package jsl.tempwork;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jsl.utilities.reporting.JSL;
import jsl.utilities.reporting.TextIO;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ReadCSV {


    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final DateTimeFormatter dateFormat8 = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //test1();
        //test2();
       // test3();
       // test4();
        test5();

    }

    public static void test2() throws FileNotFoundException{

        Path filePath = JSL.ExcelDir.resolve("DLA_SKU_DB_public_sku.csv");
        TextIO.readFile(filePath.toString());
        for (int i=1;i<=10;i++){
            String s = TextIO.getln();
            System.out.println(s);
        }
    }

    public static void test1() throws FileNotFoundException {
        //Path filePath = JSL.ExcelDir.resolve("DDDE_DEMAND_2014.csv");
        //Path filePath = JSL.ExcelDir.resolve("newDDDE.csv");
        //Path filePath = JSL.ExcelDir.resolve("newfile.csv");

        Path filePath = JSL.ExcelDir.resolve("temp_demand.csv");

        CsvToBean<Demand> csvToBean = new CsvToBeanBuilder(new FileReader(filePath.toFile())).withType(Demand.class).build();

        Iterator<Demand> iterator = csvToBean.iterator();

        int max = 10;
        int j = 1;
        while (iterator.hasNext()){
            if (j == max){
                break;
            } else {
                j++;
                Demand d = iterator.next();
                System.out.println(d);
            }
        }
    }

    public static void test3() throws FileNotFoundException, IOException {

        LocalDate day = LocalDate.of(2014,1,1);
        System.out.println(day);
        for (int i=1;i<=10;i++){
            day = day.plusDays(1);
            System.out.println(day);
        }
    }

    public static void test4() throws FileNotFoundException, IOException {
        PrintWriter writer = JSL.makePrintWriter("DLAOutput", "csv");
        Path filePath = JSL.ExcelDir.resolve("DLA_SKU_DB_public_sku.csv");
        //Path filePath = JSL.ExcelDir.resolve("DLA_SKU_DB.csv");
        TextIO.readFile(filePath.toString());
        System.out.println("Started ...");
        int n = 0;
        boolean flag = false;
        while(!TextIO.eof()){
            LocalDate day = LocalDate.of(2014,1,1);
            String niin = TextIO.getln();
            for (int i=1;i<=2013;i++){
                writer.printf("%s,DDDE,%s,%d %n",niin, day, 0);
                if (niin.equals("000000059")){
                    JSL.out.printf("%s,DDDE,%s,%d %n",niin, day, 0);
                }
                day = day.plusDays(1);
            }
            n++;
            if (n % 1000 == 0){
                System.out.printf("Completed %d ninn = %s %n", n, niin);
            }
            if (niin.equals("000000059")){
                flag = true;
            }
        }
        if (flag){
            System.out.println("read in niin 000000059");
        }
        System.out.printf("Completed %d niins %n", n);

        System.out.println("Done!");
        assert(n == 2458743);
    }

    public static void test5() throws FileNotFoundException, IOException {
        PrintWriter writer = JSL.makePrintWriter("DLAOutput1", "csv");
        Path filePath = JSL.ExcelDir.resolve("DLA_SKU_DB_public_sku.csv");
        //Path filePath = JSL.ExcelDir.resolve("DLA_SKU_DB.csv");
        TextIO.readFile(filePath.toString());
        System.out.println("Started ...");
        int n = 0;
        boolean flag = false;
        int k = 1;
        while(!TextIO.eof()){
            LocalDate day = LocalDate.of(2014,1,1);
            String niin = TextIO.getln();
            n++;
            if (n % 20000 == 0){
                writer.flush();
                writer.close();
                k++;
                String f = "DLAOutput"+k;
                System.out.println("Created file = " + f);
                writer = JSL.makePrintWriter(f,"csv");
            }
            for (int i=1;i<=2013;i++){
                writer.printf("%s,DDDE,%s,%d %n",niin, day, 0);
                day = day.plusDays(1);
            }
            if (n % 10000 == 0){
                System.out.printf("Completed %d ninn = %s %n", n, niin);
            }
        }
        writer.flush();
        writer.close();
        System.out.printf("Completed %d niins %n", n);

        System.out.println("Done!");
        //assert(n == 203499);
    }

}
