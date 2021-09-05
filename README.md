# COVID Stats 🇵🇹

![Snapshot](https://github.com/edualm/covid-stats-pt/workflows/Snapshot/badge.svg)

Telegram bot that parses the official COVID-19 data for Portugal and sends it daily to you via Telegram.

Published under the public domain.

An instance of the bot is live, and you can use it by clicking [here](https://t.me/Covid19PortugalStatsBot)!

If you want to run your own instance, you can compile it or download the latest build from GitHub Actions.
Make sure to read on though, so you know how to set it up.

## Dependencies

Dependencies are managed by Maven.

IntelliJ IDEA handled that for me. Maybe it will do the same for you if you ask nicely?

## Prerequisites

 - Working JDK with Maven (don't ask me how to set that up...);
 - A key-value store to use as a database. One of:
   - Redis instance (recommended) _or_ 
   - ThisDB account (https://thisdb.com) _or_ 
   - KVdb account (https://kvdb.io)
 - Telegram bot (https://core.telegram.org/bots);
 - Server exposed to the internet, so you can receive Telegram's webhook messages.

To build, use your favorite IDE or run `mvn -B package --file pom.xml`.

## Preparation

 - Prepare `config.properties` by renaming the sample file.
 
```
$ mv config.properties.sample config.properties
```

This file should be in the same folder as the `jar` in order for it to be correctly picked up. 
If you are running the project from an IDE, the default path should be fine.

- Fill in the blanks on the `config.properties` file with your configurations.

Only one database type should be set up.

Telegram webhooks will be listened to at `http://<your server>:<port>/<telegram webhook secret>`. 
You should probably use a reverse proxy like `caddy` or `nginx`. 
The secret key can be just a random string, but be sure to set it.

Additionally, you should set up a Telegram bot that receives webhooks at that URL and that has the commands' configuration
as such:

```
today - Get the latest information.
subscribe - Start receiving daily updates.
unsubscribe - Stop receiving daily updates.
about - About this bot.
```

## Bot Commands

### `/subscribe`

Subscribes you to receive a daily message whenever the COVID-19 report is updated, with detailed information.

### `/today`

Sends you a copy of the last report sent by the bot.

### `/unsubscribe`

Unsubscribes you from the daily messages.

### `/about`

Returns a friendly message about the bot and its programmer(s).

## Example Message

The message is in Portuguese, as the bot is directed at Portuguese users.

```
🇵🇹 [COVID-19] Evolução a 26/07

🇵🇹 Portugal
Novos: 🦠 1610 casos, 🟢 1802 recuperados, 🔴 -201 ativos, 💀 9 mortes
Total: 🦠 954669 casos, 🟢 883372 recuperados, 🔴 53996 ativos, 💀 17301 mortes

🏙️ Norte
Novos: 🦠 688 casos, 💀 5 mortes
Total: 🦠 370347 casos, 💀 5408 mortes

🏙️ Centro
Novos: 🦠 74 casos, 💀 0 mortes
Total: 🦠 128316 casos, 💀 3039 mortes

🏙️ Lisboa e Vale do Tejo
Novos: 🦠 519 casos, 💀 3 mortes
Total: 🦠 373452 casos, 💀 7382 mortes

🏙️ Alentejo
Novos: 🦠 51 casos, 💀 0 mortes
Total: 🦠 33194 casos, 💀 980 mortes

🏙️ Algarve
Novos: 🦠 177 casos, 💀 0 mortes
Total: 🦠 31658 casos, 💀 384 mortes

🏙️ Madeira
Novos: 🦠 43 casos, 💀 0 mortes
Total: 🦠 10472 casos, 💀 71 mortes

🏙️ Açores
Novos: 🦠 58 casos, 💀 1 mortes
Total: 🦠 7230 casos, 💀 37 mortes

Máximo de 🦠 casos: 16432 (28/01)
Máximo de 💀 mortes: 303 (28/01)

📝 Report DGS: https://covid19.min-saude.pt/wp-content/uploads/2021/07/511_DGS_boletim_20210726.pdf
```

## ⚠️ Disclaimer

I am not a Java developer - use this project (or read the code, even) at your own risk!
