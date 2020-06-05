# COVID Stats 🇵🇹

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

Rename `src/main/resources/config.properties.sample` to `src/main/resources/config.properties` and fill in the blanks.

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
🇵🇹 [COVID-19] Evolução Diária (referente a 05/06)

Norte: +15 / -5 (+16819 / -801)
Centro: +5 / -0 (+3770 / -240)
Lisboa e Vale do Tejo: +309 / -3 (+12137 / -383)
Alentejo: +2 / -0 (+262 / -1)
Algarve: +0 / -0 (+376 / -15)
Madeira: +0 / -0 (+90 / -0)
Açores: +0 / -0 (+138 / -15)

Portugal: +331 / ~244 / -8 (+33592 / ~20323 / -1455)

Regiões: + Casos / - Mortes
País: + Casos / ~ Recuperados / - Mortes
Estatísticas globais entre parênteses.
```

## But I just want to use the bot...

Sure, just click [here](https://t.me/Covid19PortugalStatsBot)!

## ⚠️ Disclaimer

I am not a Java developer - use this project (or read the code, even) at your own risk!
