#!/bin/bash
#
# check se esistono i file principali
if [[ ! -f /tmp/info.txt ]]
then
    cp info.txt /tmp
fi
if [[ ! -f /tmp/lavori_descrizione.txt ]]
then
    cp lavori_descrizione.txt /tmp
fi
if [[ ! -f /tmp/lavori.txt ]]
then
    cp lavori.txt /tmp
fi
if [[ ! -f /tmp/nome_device.txt ]]
then
    cp nome_device.txt /tmp
fi
if [[ ! -f /tmp/setup_lan.txt ]]
then
    cp setup_lan.txt /tmp
fi
if [[ ! -f /tmp/setup_wifi.txt ]]
then
    cp setup_wifi.txt /tmp
fi
if [[ ! -f /tmp/warning.txt ]]
then
    cp warning.txt /tmp
fi

java -jar JRivitScreen.jar