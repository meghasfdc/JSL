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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Iterator;

public class ReadCSV {

    public static void main(String[] args) throws FileNotFoundException {

        test1();

    }

    public static void test2() throws FileNotFoundException{

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
}
