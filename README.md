# Progetto di Laboratorio di Programmazione III

## Introduzione
Il progetto di laboratorio consiste nello sviluppo di due applicazioni distinte:

1. **Mail Server**: gestisce le caselle di posta elettronica degli utenti registrati.
2. **Mail Client**: permette agli utenti di leggere e inviare email, comunicando con il server tramite socket Java.

Entrambe le applicazioni sono sviluppate come applicazioni **JavaFXML** basate sul pattern **MVC**, organizzate in package per garantire modularità.

---

## Specifiche Generali

- **Comunicazione**:
    - Le applicazioni client e server comunicano esclusivamente tramite socket Java trasmettendo dati testuali.
    - Non è prevista alcuna comunicazione diretta tra viste e model: ogni interazione deve passare attraverso il controller o essere gestita tramite il pattern **Observer Observable** di JavaFX.
- **Parallelismo**:
    - Le applicazioni devono parallelizzare le operazioni non sequenziali e gestire l'accesso concorrente alle risorse in mutua esclusione.
- **Persistenza dei dati**:
    - Il server utilizza file (testo o binari) per memorizzare i dati.

---

## Mail Client

### Funzionalità

1. **Autenticazione**:
    - L'utente inserisce il proprio indirizzo email, che viene verificato sintatticamente tramite regex.
    - Il client verifica l'esistenza dell'email contattando il server.

2. **Gestione Inbox**:
    - Visualizzazione dei messaggi ricevuti.
    - Dettaglio di un messaggio selezionato.
    - Cancellazione di un messaggio.

3. **Gestione Messaggi**:
    - Creazione e invio di email a uno o più destinatari.
    - Risposta a un messaggio (“Reply” o “Reply-All”).
    - Inoltro di un messaggio (“Forward”).

4. **Gestione Connessione**:
    - Visualizzazione dello stato della connessione con il server.
    - Gestione della riconnessione automatica in caso di disconnessione.

### Requisiti GUI

- **Inbox aggiornata automaticamente**:
    - La lista dei messaggi si aggiorna senza intervento dell'utente.
    - Notifiche per nuovi messaggi ricevuti.
- **Segnalazione errori**:
    - Problemi (es. connessione persa) devono essere comunicati in modo chiaro.
- **Efficacia**:
    - Minimizzare il numero di click necessari per eseguire operazioni.

---

## Mail Server

### Funzionalità

1. **Gestione Caselle Postali**:
    - Ogni casella contiene:
        - Nome dell’account email.
        - Lista di messaggi (istanze della classe `Email`).
    - I dati sono salvati su file per garantire la persistenza.

2. **Log degli Eventi**:
    - Visualizzazione di eventi come:
        - Connessioni aperte/chiuse con i client.
        - Invio di messaggi e relativi errori.
    - Non devono essere loggati eventi locali al client (es. click sui bottoni).

3. **Gestione Utenti**:
    - Il server ha un numero fisso di account preconfigurati (es. 3 account).
    - Non è prevista la registrazione di nuovi utenti tramite il client.

---

## Comunicazione Client-Server

- **Verifica Indirizzi**:
    - Il server è responsabile della verifica dell’esistenza degli indirizzi email.
    - In caso di errore (es. indirizzo inesistente), il server invia un messaggio di errore al client.

- **Connessioni**:
    - Il client apre una connessione temporanea al server per ogni operazione.
    - In caso di disconnessione, il client deve gestire il problema e tentare una riconnessione automatica.

- **Aggiornamenti**:
    - Il server invia al client solo i nuovi messaggi non ancora scaricati per garantire la scalabilità.

---

## Requisiti Aggiuntivi

- **Scalabilità**:
    - Il sistema deve essere progettato per supportare un numero crescente di utenti.

- **Socket Non Permanenti**:
    - Le connessioni tra client e server non devono essere permanenti.

---

## Promemoria per l'Esame

- **Modalità di Svolgimento**:
    - Il progetto può essere svolto individualmente o in gruppo (max 3 persone).
    - La discussione deve essere effettuata da tutto il gruppo in un’unica soluzione.

- **Valutazione**:
    - Il voto finale è la media tra la prova teorica e la discussione del progetto.
    - Il voto deve essere registrato entro settembre 2025.
