package com.example.server;

import com.example.server.controller.ServerController;
import com.example.server.model.ClientHandler;
import com.example.server.model.MailStorage;
import com.example.server.model.MailBox;
import com.example.server.model.Email;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailIntegrationTest {

    private File tempDir;
    private MailStorage mailStorage;
    private TestServerController testController;
    private ClientHandler clientHandler;

    /**
     * Sottoclasse di ServerController che registra i log in una StringBuilder,
     * così da poter verificare facilmente i messaggi senza usare componenti JavaFX reali.
     */
    static class TestServerController extends ServerController {
        StringBuilder logBuilder = new StringBuilder();

        @Override
        public void appendLog(String message) {
            logBuilder.append(message).append("\n");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Crea una directory temporanea per il MailStorage (così i test sono isolati)
        tempDir = Files.createTempDirectory("mailTest").toFile();
        mailStorage = new MailStorage(tempDir);
        testController = new TestServerController();

        // Crea e registra le mailbox per i 3 client
        // Si assume che MailBox abbia un costruttore MailBox(String account)
        MailBox mailboxAlessio = new MailBox("alessio@notamail.com");
        MailBox mailboxLuis = new MailBox("luis@notamail.com");
        MailBox mailboxGigi = new MailBox("gigi@notamail.com");

        mailStorage.saveMailBox(mailboxAlessio);
        mailStorage.saveMailBox(mailboxLuis);
        mailStorage.saveMailBox(mailboxGigi);

        // Crea il ClientHandler (socket non usato per test diretti)
        clientHandler = new ClientHandler(null, mailStorage, testController);
    }

    @AfterEach
    void tearDown() {
        // Rimuove ricorsivamente la directory temporanea
        deleteDir(tempDir);
    }

    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

    // ======== TEST LOGIN ========

    @Test
    void testLoginSuccess() {
        JSONObject request = new JSONObject();
        request.put("operazione", "LOGIN");
        request.put("email", "alessio@notamail.com");
        JSONObject response = clientHandler.handleRequest(request);
        assertEquals("OK", response.getString("risultato"), "Login di un utente registrato deve dare OK");
    }

    @Test
    void testLoginFailure() {
        JSONObject request = new JSONObject();
        request.put("operazione", "LOGIN");
        request.put("email", "nonexistent@notamail.com");
        JSONObject response = clientHandler.handleRequest(request);
        assertEquals("ERRORE", response.getString("risultato"), "Login di un utente non registrato deve dare ERRORE");
        assertEquals("Email non registrata", response.getString("messaggio"));
    }

    // ======== TEST INVIO EMAIL ========

    @Test
    void testSendEmail() throws IOException, ClassNotFoundException {
        // Alessio invia un'email a Luis e Gigi
        JSONObject request = new JSONObject();
        request.put("operazione", "SEND_EMAIL");
        request.put("mittente", "alessio@notamail.com");
        JSONArray destinatari = new JSONArray();
        destinatari.put("luis@notamail.com");
        destinatari.put("gigi@notamail.com");
        request.put("destinatari", destinatari);
        request.put("oggetto", "Subject Test");
        request.put("corpo", "Body of the email");
        request.put("data", "2025-02-13");

        JSONObject response = clientHandler.handleRequest(request);
        assertEquals("OK", response.getString("risultato"), "L'invio email deve dare OK");

        // Verifica che Luis abbia ricevuto l'email
        MailBox mailboxLuis = mailStorage.loadMailBox("luis@notamail.com");
        List<Email> emailsLuis = mailboxLuis.getEmails();
        assertFalse(emailsLuis.isEmpty(), "Luis deve avere almeno un'email");
        boolean found = emailsLuis.stream().anyMatch(e -> e.getSubject().equals("Subject Test"));
        assertTrue(found, "L'email inviata da Alessio deve essere presente nella mailbox di Luis");

        // Verifica anche per Gigi
        MailBox mailboxGigi = mailStorage.loadMailBox("gigi@notamail.com");
        List<Email> emailsGigi = mailboxGigi.getEmails();
        assertFalse(emailsGigi.isEmpty(), "Gigi deve avere almeno un'email");
        found = emailsGigi.stream().anyMatch(e -> e.getSubject().equals("Subject Test"));
        assertTrue(found, "L'email inviata da Alessio deve essere presente nella mailbox di Gigi");
    }

    // ======== TEST CANCELLAZIONE EMAIL ========

    @Test
    void testDeleteEmail() throws IOException, ClassNotFoundException {
        // Invia un'email da Alessio a Luis che verrà poi cancellata
        JSONObject sendRequest = new JSONObject();
        sendRequest.put("operazione", "SEND_EMAIL");
        sendRequest.put("mittente", "alessio@notamail.com");
        JSONArray destinatari = new JSONArray();
        destinatari.put("luis@notamail.com");
        sendRequest.put("destinatari", destinatari);
        sendRequest.put("oggetto", "Email to Delete");
        sendRequest.put("corpo", "This email will be deleted");
        sendRequest.put("data", "2025-02-13");

        JSONObject sendResponse = clientHandler.handleRequest(sendRequest);
        assertEquals("OK", sendResponse.getString("risultato"));

        // Recupera l'email dalla mailbox di Luis
        MailBox mailboxLuis = mailStorage.loadMailBox("luis@notamail.com");
        List<Email> emailsLuis = mailboxLuis.getEmails();
        assertFalse(emailsLuis.isEmpty(), "Luis deve aver ricevuto l'email da cancellare");
        Email emailToDelete = emailsLuis.stream()
                .filter(e -> e.getSubject().equals("Email to Delete"))
                .findFirst()
                .orElse(null);
        assertNotNull(emailToDelete, "L'email da cancellare deve essere presente");

        // Invia la richiesta di cancellazione
        JSONObject deleteRequest = new JSONObject();
        deleteRequest.put("operazione", "DELETE_EMAIL");
        deleteRequest.put("mittente", "luis@notamail.com");
        deleteRequest.put("id", emailToDelete.getId());
        JSONObject deleteResponse = clientHandler.handleRequest(deleteRequest);
        assertEquals("OK", deleteResponse.getString("risultato"), "La cancellazione deve dare OK");

        // Verifica che l'email non sia più presente nella mailbox di Luis
        mailboxLuis = mailStorage.loadMailBox("luis@notamail.com");
        emailsLuis = mailboxLuis.getEmails();
        boolean stillPresent = emailsLuis.stream()
                .anyMatch(e -> e.getId().equals(emailToDelete.getId()));
        assertFalse(stillPresent, "L'email deve essere rimossa dalla mailbox di Luis");
    }

    // ======== TEST INOLTRAMENTO (FORWARD) ========

    @Test
    void testForwardEmail() throws IOException, ClassNotFoundException {
        // 1. Alessio invia un'email a Luis
        JSONObject sendRequest = new JSONObject();
        sendRequest.put("operazione", "SEND_EMAIL");
        sendRequest.put("mittente", "alessio@notamail.com");
        JSONArray destinatari = new JSONArray();
        destinatari.put("luis@notamail.com");
        sendRequest.put("destinatari", destinatari);
        sendRequest.put("oggetto", "Original Email");
        sendRequest.put("corpo", "Original content");
        sendRequest.put("data", "2025-02-13");

        JSONObject sendResponse = clientHandler.handleRequest(sendRequest);
        assertEquals("OK", sendResponse.getString("risultato"));

        // 2. Verifica che Luis abbia ricevuto l'email originale
        MailBox mailboxLuis = mailStorage.loadMailBox("luis@notamail.com");
        List<Email> emailsLuis = mailboxLuis.getEmails();
        Email originalEmail = emailsLuis.stream()
                .filter(e -> e.getSubject().equals("Original Email"))
                .findFirst()
                .orElse(null);
        assertNotNull(originalEmail, "Luis deve aver ricevuto l'email originale");

        // 3. Luis inoltra (forward) l'email a Gigi
        JSONObject forwardRequest = new JSONObject();
        forwardRequest.put("operazione", "SEND_EMAIL");
        forwardRequest.put("mittente", "luis@notamail.com");
        JSONArray forwardDestinatari = new JSONArray();
        forwardDestinatari.put("gigi@notamail.com");
        forwardRequest.put("destinatari", forwardDestinatari);
        forwardRequest.put("oggetto", "Fwd: Original Email");
        forwardRequest.put("corpo", "Forwarded content: " + originalEmail.getBody());
        forwardRequest.put("data", "2025-02-13");

        JSONObject forwardResponse = clientHandler.handleRequest(forwardRequest);
        assertEquals("OK", forwardResponse.getString("risultato"));

        // Verifica che Gigi abbia ricevuto l'email inoltrata
        MailBox mailboxGigi = mailStorage.loadMailBox("gigi@notamail.com");
        List<Email> emailsGigi = mailboxGigi.getEmails();
        boolean forwardFound = emailsGigi.stream()
                .anyMatch(e -> e.getSubject().equals("Fwd: Original Email"));
        assertTrue(forwardFound, "Gigi deve aver ricevuto l'email inoltrata da Luis");
    }
}
