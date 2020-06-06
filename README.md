# COVID Stats 🇵🇹

![Snapshot](https://github.com/edualm/covid-stats-pt/workflows/Snapshot/badge.svg)

Telegram bot that parses the official COVID-19 data for Portugal and sends them daily to you by Telegram.

Published under the public domain.

## Dependencies

Dependencies are managed by Maven.

IntelliJ IDEA handled that for me. Maybe it will do the same for you if you ask nicely?

## Prerequisites

 - Working JDK with Maven (don't ask me how to set that up...)
 - Redis instance _or_ ThisDB account (https://thisdb.com) _or_ KVdb account (https://kvdb.io)
 - Telegram bot (https://core.telegram.org/bots)
 - Server exposed to the internet, so you can receive Telegram's webhook messages.

## Preparation

Rename `config.properties.sample` to `config.properties` and fill in the blanks. This file should be in the same folder as the `jar` in order for it to be correctly picked up.

Only one database type should be configured.

Telegram webhooks will be listened to at `http://<your server>:<port>/<telegram webhook secret>`. You should probably use a reverse proxy like `caddy` or `nginx`. Additionally, you should set up a Telegram bot that receives webhooks at that URL and is prepared to receive the following commands:

 - `/subscribe`
 - `/today`
 - `/unsubscribe`

## Bot Commands

### `/subscribe`

Subscribes you to receive a daily message whenever the COVID-19 report is updated, with detailed information.

### `/today`

Sends you a copy of the last report sent by the bot.

### `/unsubscribe`

Unsubscribes you from the daily messages.

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

## But I just want to use the bot...

Sure, just click [here](https://t.me/Covid19PortugalStatsBot)!

If you want to run your own instance, you can also just download the latest build from GitHub Actions.

## ⚠️ Disclaimer

I am not a Java developer - use this project (or read the code, even) at your own risk!
