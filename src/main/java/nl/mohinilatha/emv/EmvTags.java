package nl.mohinilatha.emv;

import java.util.Map;

/** Names of common EMV tags (EMV Book 3, Annex A). */
public final class EmvTags {

    private EmvTags() {
    }

    private static final Map<String, String> NAMES = Map.ofEntries(
            Map.entry("4F", "Application Identifier (AID) - card"),
            Map.entry("50", "Application Label"),
            Map.entry("57", "Track 2 Equivalent Data"),
            Map.entry("5A", "Application PAN"),
            Map.entry("5F20", "Cardholder Name"),
            Map.entry("5F24", "Application Expiration Date"),
            Map.entry("5F25", "Application Effective Date"),
            Map.entry("5F28", "Issuer Country Code"),
            Map.entry("5F2A", "Transaction Currency Code"),
            Map.entry("5F2D", "Language Preference"),
            Map.entry("5F34", "PAN Sequence Number"),
            Map.entry("61", "Application Template"),
            Map.entry("6F", "File Control Information (FCI) Template"),
            Map.entry("70", "READ RECORD Response Template"),
            Map.entry("77", "Response Message Template Format 2"),
            Map.entry("80", "Response Message Template Format 1"),
            Map.entry("82", "Application Interchange Profile (AIP)"),
            Map.entry("84", "Dedicated File (DF) Name"),
            Map.entry("87", "Application Priority Indicator"),
            Map.entry("88", "Short File Identifier (SFI)"),
            Map.entry("8A", "Authorisation Response Code"),
            Map.entry("8C", "CDOL1"),
            Map.entry("8D", "CDOL2"),
            Map.entry("8E", "Cardholder Verification Method (CVM) List"),
            Map.entry("8F", "CA Public Key Index"),
            Map.entry("90", "Issuer Public Key Certificate"),
            Map.entry("91", "Issuer Authentication Data"),
            Map.entry("94", "Application File Locator (AFL)"),
            Map.entry("95", "Terminal Verification Results (TVR)"),
            Map.entry("9A", "Transaction Date"),
            Map.entry("9B", "Transaction Status Information (TSI)"),
            Map.entry("9C", "Transaction Type"),
            Map.entry("A5", "FCI Proprietary Template"),
            Map.entry("BF0C", "FCI Issuer Discretionary Data"),
            Map.entry("9F02", "Amount, Authorised"),
            Map.entry("9F03", "Amount, Other"),
            Map.entry("9F07", "Application Usage Control"),
            Map.entry("9F08", "Application Version Number - card"),
            Map.entry("9F09", "Application Version Number - terminal"),
            Map.entry("9F0D", "Issuer Action Code - Default"),
            Map.entry("9F0E", "Issuer Action Code - Denial"),
            Map.entry("9F0F", "Issuer Action Code - Online"),
            Map.entry("9F10", "Issuer Application Data"),
            Map.entry("9F1A", "Terminal Country Code"),
            Map.entry("9F1E", "Interface Device Serial Number"),
            Map.entry("9F26", "Application Cryptogram"),
            Map.entry("9F27", "Cryptogram Information Data (CID)"),
            Map.entry("9F33", "Terminal Capabilities"),
            Map.entry("9F34", "CVM Results"),
            Map.entry("9F35", "Terminal Type"),
            Map.entry("9F36", "Application Transaction Counter (ATC)"),
            Map.entry("9F37", "Unpredictable Number"),
            Map.entry("9F38", "PDOL"),
            Map.entry("9F41", "Transaction Sequence Counter"),
            Map.entry("9F6E", "Form Factor Indicator / Third Party Data")
    );

    public static String name(String tag) {
        return NAMES.getOrDefault(tag.toUpperCase(), "Unknown tag");
    }
}
