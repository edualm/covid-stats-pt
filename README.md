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

 - Working JDK with Maven (don't ask me how to set that up...)
 - Redis instance _or_ ThisDB account (https://thisdb.com) _or_ KVdb account (https://kvdb.io)
 - Telegram bot (https://core.telegram.org/bots)
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

Additionally, you should set up a Telegram bot that receives webhooks at that URL and that has the commands configuration
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

The message is in Portuguese.

![Deal with it.](https://i.pinimg.com/564x/a1/96/16/a1961629de94ef8fe2cee1c50015ee5e.jpg)

```
🇵🇹 [COVID-19] Evolução a 05/06

 🏙️ Norte
Novos: 🦠 15 casos, 💀 2 mortes
Cumulativo: 🦠 16834 casos, 💀 803 mortes

 🏙️ Centro
Novos: 🦠 19 casos, 💀 4 mortes
Cumulativo: 🦠 3789 casos, 💀 244 mortes

 🏙️ Lisboa e Vale do Tejo
Novos: 🦠 336 casos, 💀 4 mortes
Cumulativo: 🦠 12473 casos, 💀 387 mortes

 🏙️ Alentejo
Novos: 🦠 1 casos, 💀 0 mortes
Cumulativo: 🦠 263 casos, 💀 1 mortes

 🏙️ Algarve
Novos: 🦠 4 casos, 💀 0 mortes
Cumulativo: 🦠 380 casos, 💀 15 mortes

 🏙️ Madeira
Novos: 🦠 0 casos, 💀 0 mortes
Cumulativo: 🦠 90 casos, 💀 0 mortes

 🏙️ Açores
Novos: 🦠 2 casos, 💀 0 mortes
Cumulativo: 🦠 140 casos, 💀 15 mortes

 🇵🇹 Portugal:
Novos: 🦠 377 casos, 🟢 203 recuperados, 💀 10 mortes
Cumulativo: 🦠 33969 casos, 🟢 20526 recuperados, 💀 1465 mortes
```

## ⚠️ Disclaimer

I am not a Java developer - use this project (or read the code, even) at your own risk!
