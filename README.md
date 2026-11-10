# EMV TLV Analyzer

![Unit tests](https://github.com/Mohinilatha31/emv-tlv-analyzer/actions/workflows/tests.yml/badge.svg)

A small Java tool that reads EMV chip card data and explains it in plain words. I built it while learning EMV payment testing, to practise what a test analyst does when a card transaction fails: read the trace, find the key tags, and work out why the card and terminal decided what they did.

## What it does

- **Parses BER-TLV data**, the format EMV uses for card responses and for chip data in ISO 8583 field 55. It handles multi-byte tags (e.g. `9F26`, `BF0C`), nested (constructed) templates (e.g. `6F`, `A5`) and long-form lengths (`81 xx`).
- **Names the common EMV tags** (AID, AIP, CVM list, ATC, cryptogram, and more).
- **Decodes the tags testers check first:**
  - `95` Terminal Verification Results (TVR): which checks failed, bit by bit
  - `9B` Transaction Status Information (TSI): which steps were performed
  - `9F27` Cryptogram Information Data (CID): did the card approve offline (TC), go online (ARQC) or decline (AAC)
- **Reports clear errors** for broken input: cut-off data, wrong lengths, non-hex characters.

## Run it

Needs Java 21+ and Maven.

```bash
mvn package
java -jar target/emv-tlv-analyzer-1.0.0.jar                       # built-in example
java -jar target/emv-tlv-analyzer-1.0.0.jar 95050000008000 9F270180  # your own hex
```

## Example output

The built-in example is chip data as it would travel in field 55 of an authorisation request (made-up values):

```
TLV structure
-------------
9F26 (Application Cryptogram) len=8 value=C2C12B098F3DA6E3
9F27 (Cryptogram Information Data (CID)) len=1 value=80
...
95 (Terminal Verification Results (TVR)) len=5 value=0000008000
9B (Transaction Status Information (TSI)) len=2 value=E800

Decoded
-------
Cryptogram (9F27): ARQC - Authorisation Request Cryptogram: the card asks to go online to the issuer
TVR (95):
  - Transaction exceeds floor limit (byte 4 bit 8)
TSI (9B) steps performed:
  - Offline data authentication was performed
  - Cardholder verification was performed
  - Card risk management was performed
  - Terminal risk management was performed
```

**Reading it as a tester:** the amount was above the terminal's floor limit, so terminal risk management set a TVR bit. The terminal therefore asked for an online decision, and the card answered with an ARQC. The transaction went online to the issuer, which is the expected behaviour.

## Tests

19 JUnit 5 tests cover:

- real card responses (PSE and contactless PPSE `SELECT` responses)
- nested templates, long-form lengths, spaces and lower-case input
- TVR, TSI and CID decoding, including several TVR bits set at once
- negative tests for malformed data

The tests run automatically on every push with GitHub Actions.

## Project structure

| Class | Role |
| --- | --- |
| `TlvParser` | Reads tag, length and value; recurses into constructed tags |
| `EmvTags` | Tag names (EMV Book 3, Annex A) |
| `TvrDecoder`, `TsiDecoder`, `CidDecoder` | Bit-level decoding (EMV Book 3, Annex C) |
| `Analyzer` | Builds the readable report |
| `Main` | Command line entry point |

## References

- EMVCo, *EMV Integrated Circuit Card Specifications for Payment Systems*, Book 3 (Application Specification): Annex A (data elements), Annex B (TLV coding), Annex C (TVR, TSI)
- EMVCo, *A Guide to EMV Chip Technology*
- [EMVLab TLV decoder](https://emvlab.org/tlvutils/), used to cross-check results
