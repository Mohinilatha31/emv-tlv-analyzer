package nl.mohinilatha.emv;

/**
 * Command line entry point.
 * Usage: java -jar emv-tlv-analyzer-1.0.0.jar <hex>
 * With no argument, runs a built-in example (a field 55 style authorisation request).
 */
public final class Main {

    // Example chip data as an acquirer would send it in ISO 8583 field 55 (made-up values).
    static final String EXAMPLE =
            "9F2608C2C12B098F3DA6E3" // Application Cryptogram
            + "9F270180"             // CID: ARQC (go online)
            + "9F100706010A03A0A000" // Issuer Application Data
            + "9F37045F2A9C11"       // Unpredictable Number
            + "9F36020042"           // ATC
            + "95050000008000"       // TVR: transaction exceeds floor limit
            + "9A03261110"           // Transaction Date 2026-11-10
            + "9C0100"               // Transaction Type: purchase
            + "9F0206000000001250"   // Amount 12.50
            + "5F2A020978"           // Currency: EUR
            + "82023C00"             // AIP
            + "9F1A020528"           // Terminal Country: Netherlands
            + "9F3403420300"         // CVM Results: online PIN
            + "9B02E800";            // TSI

    public static void main(String[] args) {
        String input = args.length > 0 ? String.join("", args) : EXAMPLE;
        if (args.length == 0) {
            System.out.println("No input given, analysing the built-in example:\n" + EXAMPLE + "\n");
        }
        try {
            System.out.print(new Analyzer().analyze(input));
        } catch (IllegalArgumentException | TlvException e) {
            System.err.println("Could not parse input: " + e.getMessage());
            System.exit(1);
        }
    }
}
