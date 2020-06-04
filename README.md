# COVID Stats 🇵🇹

Telegram bot that parses the official COVID-19 data for Portugal and sends them daily to you by Telegram.

Published under the public domain.

## Dependencies

 - JSoup (https://jsoup.org/)
 - Tabula (https://github.com/tabulapdf/tabula-java)
 
These are included as `JAR`s in this repository, under the `libs` directory.

## Prerequisites

 - Working JDK
 - KVDb account (https://kvdb.io/)
 - Telegram bot (https://core.telegram.org/bots)

## Preparation

Rename `src/io/edr/covidstatspt/Secrets.java.sample` to `src/io/edr/covidstatspt/Secrets.java` and fill in the blanks.

The bot requires a list of `chat_id`s. They should be stored in the `recipients` key of the database, comma-separated.

### ⚠️ Disclaimer

I am not a Java developer - use this project (or read the code, even) at your own risk!
