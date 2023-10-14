#!/bin/bash
#
# check se esistono i file principali
# Potremmo anche montare sshfs e poi iniziare il ciclo di copia
# dei file. Come parametro gli possiamo passare user:ip dove montare
# così replica il nome del folder dove monta la risosra in /tmp
# 
PATHMOUNT="/adminsb/sshmount/"
#Leggere l'elenco dei file da trattare (copiare dal Mount SSH in /tmp
#IFS=$'\n' read -d '' -r -a linee < /home/adminsb/elenco_files.txt
if [[ "$#" -eq 0 ]];
then
    echo $0 -u[url to mount] -f[folder to copy files] -k[ssh key] -l[localhost[yes/no]]
    exit -1
fi

readarray linee  < /home/adminsb/elenco_files.txt # more easy
i=0
cat /home/adminsb/elenco_files.txt | while read alinee

while getopts u:f:k:l: flag # tipo -u adminsb@ip:/home/...
do
    case "${flag}" in
        u) url=${URLSSH};; #compresa la cartella da montare
        f) folder=${FOLDER};;
        k) keyssh=${KSSH};;
        l) localh=${$LOCALHOST};;
    esac
done

if [[ $LOCALHOST == "no" ]];
then
echo sshfs -i /home/adminsb/keys/$KSSH $URLSSH %PATHMOUNT$FOLDER
echo $URLSSH
echo $FOLDER
echo $KSSH
echo $LOCALHOST

exit 0
    if [ $? == 0 ];
    then
        echo  " Ok Mount $URLSSH"
    else
        echo "Error Mount $URLSSH !"
    exit -1
    fi

    if [[ ! -d /tmp/$FOLDER ]];
    then
       mkdir /tmp/$FOLDER
    fi

fi

if [[ ! -f /tmp/info.txt ]];
then
    cp info.txt /tmp
fi

if [[ ! -f /tmp/lavori_descrizione.txt ]];
then
    cp lavori_descrizione.txt /tmp
fi
if [[ ! -f /tmp/lavori.txt ]];
then
    cp lavori.txt /tmp
fi
if [[ ! -f /tmp/nome_device ]];
then
    cp nome_device /tmp
fi
if [[ ! -f /tmp/setup_lan.txt ]];
then
    cp setup_lan.txt /tmp
fi
if [[ ! -f /tmp/setup_wifi.txt ]];
then
    cp setup_wifi.txt /tmp
fi

if [[ ! -f /tmp/warning.txt ]];
then
    cp warning.txt /tmp
fi

#java -jar JRivitScreen.jar