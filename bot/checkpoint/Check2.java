/*
 * Copyright 2018 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package bot.checkpoint;

import bot.RiskSystemState;
import bot.data.PaymentRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * bot.checkpoint.ExampleCheck1
 *
 * Example check
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Check2 extends AbstractCheck {

    private HashMap<String, LocalDateTime> timeRecord = new HashMap<>();
    // block transaction on same card within the time limit below
    private long minuteThreshold = 3;

    public Check2(int id) {
        super(id);
    }

    @Override
    public String getDescription() {
        return "Rejects repeated transaction by same card";
    }

    @Override
    public boolean approveRecord(RiskSystemState state) {
        PaymentRecord record = state.getCurrentRecord();

        System.err.println("Check2: Checking record " + record.getData("txid"));

        //# reject repeated transactions by same card, defined as transaction within 3 minutes of another

        // create map with card number as key, transaction time as values
        // if record does not exist. add to map and allow to pass
        // if record exists, add time to list and check the time, if last transaction was 3 min ago. reject transaction


        String card_number_hash = record.getData("card_number_hash");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime creation_dateTime = LocalDateTime.parse(record.getData("creation_date"), formatter);

        if (timeRecord.get(card_number_hash) == null) {
            timeRecord.put(card_number_hash, creation_dateTime);
        } else {
            // practically the record will be organized by time
            // however the grading system sends in the record one by one so records will come in random order
            // this will not block transaction that have gap days inserted in between

            if (timeRecord.get(card_number_hash).minusMinutes(minuteThreshold).isBefore(creation_dateTime)  &&
                    timeRecord.get(card_number_hash).plusMinutes(minuteThreshold).isAfter(creation_dateTime)){

                timeRecord.put(card_number_hash, creation_dateTime);
                return false;
            }
        }
        // If fraud, return false

        return true;
    }

}
