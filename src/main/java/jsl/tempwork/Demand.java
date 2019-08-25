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

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class Demand {

    @CsvBindByName
    private String NIIN;
    @CsvBindByName
    private String DMDPLANT;
    @CsvBindByName
    private int qty;
    @CsvBindByName
    @CsvDate("yyyy-mm-dd")
    private Date order_creation_dt;

    public String getNIIN() {
        return NIIN;
    }

    public void setNIIN(String NIIN) {
        this.NIIN = NIIN;
    }

    public String getDMDPLANT() {
        return DMDPLANT;
    }

    public void setDMDPLANT(String DMDPLANT) {
        this.DMDPLANT = DMDPLANT;
    }

    public Date getOrder_creation_dt() {
        return order_creation_dt;
    }

    public void setOrder_creation_dt(Date order_creation_dt) {
        this.order_creation_dt = order_creation_dt;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Demand{");
        sb.append("NIIN='").append(NIIN).append('\'');
        sb.append(", DMDPLANT='").append(DMDPLANT).append('\'');
        sb.append(", qty=").append(qty);
        sb.append(", order_creation_dt=").append(order_creation_dt);
        sb.append('}');
        return sb.toString();
    }
}
