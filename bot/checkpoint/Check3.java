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

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * bot.checkpoint.ExampleCheck1
 *
 * Example check
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class Check3 extends AbstractCheck {

    private HashMap<String, String> emailRecord = new HashMap<>();

    public Check3(int id) {
        super(id);
    }

    @Override
    public String getDescription() {
        return "Rejects repeated transaction by different emails";
    }

    @Override
    public boolean approveRecord(RiskSystemState state) {
        String regexe1 = "@.*$";        // pattern to be matched. (any character after @)
        String regexe2 = "[^a-zA-Z0-9]";
        String replacement = "";  // replacement pattern

        PaymentRecord record = state.getCurrentRecord();

        System.err.println("Check3: Checking record " + record.getData("txid"));

        // reject transaction where multiple email is associated with one card

        String card_number_hash = record.getData("card_number_hash");
        String shopper_email = record.getData("shopper_email");

        Pattern pattern = Pattern.compile(regexe1, Pattern.CASE_INSENSITIVE);
        String sanitized_addr  = pattern.matcher(shopper_email).replaceAll(replacement);
        pattern = Pattern.compile(regexe2, Pattern.CASE_INSENSITIVE);
        sanitized_addr = pattern.matcher(sanitized_addr).replaceAll(replacement);

        if (emailRecord.get(card_number_hash) == null) {
            emailRecord.put(card_number_hash, sanitized_addr);
        }else{
            return emailRecord.get(card_number_hash).equals(sanitized_addr);
        }

        // If fraud, return false

        return true;
    }
}
