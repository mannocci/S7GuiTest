# JControl-main

## Descrizione ver. 1.0 27 marzo 2023

### Main

Avvia un Socket in ascolto sulla porta 1111 sia su localhost, utilizzata dai processi locali,
che sull'IP 0.0.0.0 (cioè l'indirizzo IP che gli verrà assegnato dal DHCP o in modo statico
durante la configurazione)
Apre una connessione sul DB, condividendo con i metodi l'oggetto Connection per consentirne l'uso


### Metodo SocketClient

E' il metodo che si avvia al collegamento di un client, rimane in ascolto per le necessità del client
Per accedere con sicurezza ?
  Occorre avere un IP compatibile con la rete LAN
  Occorre avere una chiave ( registrata sul DB )per decriptare/criptare il messaggio in base al nome utente
  L'unico messaggio che non è decriptato inizia con HELLO <nome utente> se il dialogo non
  inizia così la comunicazione viene chiusa
  Se il nome utente non è registrato sul DB la comunicazione viene chiusa
  
Per fare delle richieste o impostare dei comandi ?
  Viene ideato un protocollo
  
