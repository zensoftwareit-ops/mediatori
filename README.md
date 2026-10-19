# Mediatori

Gestionale Java web basato su Struts 2, JSP e Hibernate, distribuito su Tomcat 9 con Java 11.

## Configurazione

1. Copiare `src/main/java/app.properties.example` in `src/main/java/app.properties`.
2. Inserire le credenziali del database e del server SMTP nel file locale.
3. Non aggiungere `app.properties` al repository: è escluso tramite `.gitignore`.

## Database

Le migrazioni SQL sono disponibili in `database/migrations` e devono essere eseguite prima del deploy della versione corrispondente.

## Deploy

Il progetto è configurato come Dynamic Web Project Eclipse per Tomcat 9. Il sorgente Java usa compatibilità Java 11; `src/main/webapp` costituisce la web root e le classi compilate vengono distribuite in `WEB-INF/classes`.
