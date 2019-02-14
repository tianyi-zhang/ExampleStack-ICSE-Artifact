package ua.p2psafety.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ua.p2psafety.data.Prefs;
import ua.p2psafety.util.Logs;
import ua.p2psafety.util.Utils;

/**
 * From http://stackoverflow.com/questions/12503303/javamail-api-in-android-using-xoauth
 */
public class GmailOAuth2Sender {

    private Session session;
    private String token;
    private AccountManager mAccountManager;
    private Context context;
    private static Logs LOGS;

    public GmailOAuth2Sender(Context ctx) {
        super();
        context = ctx;
        token = Prefs.getGmailToken(context);
        mAccountManager = AccountManager.get(context);

        LOGS = new Logs(context);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (LOGS != null)
            LOGS.close();
    }

    private SMTPTransport connectToSmtp(String host, int port, String userEmail,
                                        String oauthToken, boolean debug) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");
        props.put("mail.smtp.ssl.enable", true);
        session = Session.getInstance(props);
        session.setDebug(debug);
        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = null;
        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
                oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        transport.issueCommand("AUTH XOAUTH2 " + new String(response),
                235);

        return transport;
    }

    @SuppressLint("NewApi")
    public void initToken() {
        Account[] accounts = mAccountManager.getAccountsByType("com.google");
        for (Account account : accounts) {
            Log.d("getToken", "account=" + account);
        }

        Account me = accounts[0]; //You need to get a google account on the device, it changes if you have more than one
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            mAccountManager.getAuthToken(me, "oauth2:https://mail.google.com/", null,
                    false, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> result) {
                    saveToken(result);
                }
            }, null);
        } else {
            mAccountManager.getAuthToken(me, "oauth2:https://mail.google.com/", false, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    saveToken(future);
                }
           }, null);
        }

        Log.d("getToken", "token=" + token);
    }

    private void saveToken(AccountManagerFuture<Bundle> result) {
        try {
            Bundle bundle = result.getResult();
            Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
            if (intent != null)
                context.startActivity(intent);
            else
            {
                token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                Prefs.setGmailToken(context, token);
            }
        } catch (Exception e) {
            LOGS.error("Can't save gmail token", e);
        }
    }

    public synchronized void sendMail(String subject, String body, String user, String recipients) {
        if (!Utils.isNetworkConnected(context, LOGS)) {
            LOGS.info("GMailOAuth2Sender. No network.");
            return;
        }
        SMTPTransport smtpTransport = null;
        try {
            LOGS.info("GMailOAuth2Sender. Connecting to SMTP");
            smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    token,
                    true);

            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            try {
                LOGS.info("GMailOAuth2Sender. Setting email recipients");
                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(recipients));
                else
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipients));
                LOGS.info("GMailOAuth2Sender. Sending email. Recipients: " + recipients);
                smtpTransport.sendMessage(message, message.getAllRecipients());
            } finally {
                smtpTransport.close();
            }
        } catch (MessagingException e) {
            LOGS.error("Can't send mail", e);
            mAccountManager.invalidateAuthToken("com.google", token);
            initToken();
            sendMail(subject, body, user, recipients);
        }
    }

    public synchronized void sendMail(String subject, String body, String user, String recipients, File file) {
        if (!Utils.isNetworkConnected(context, LOGS)) {
            return;
        }
        SMTPTransport smtpTransport = null;
        try {
            smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    token,
                    true);

            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            //message.setContent(body, "text/html; charset=utf-8");
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(file.getName());
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            try {
                if (recipients.indexOf(',') > 0)
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(recipients));
                else
                    message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(recipients));
                smtpTransport.sendMessage(message, message.getAllRecipients());
            } finally {
                smtpTransport.close();
            }
        } catch (MessagingException e) {
            LOGS.error("Can't send mail with attachment", e);
            mAccountManager.invalidateAuthToken("com.google", token);
            initToken();
            sendMail(subject, body, user, recipients, file);
        }
    }

    public synchronized void sendMail(String subject, String body, String user, String email, List<File> files) {

        if (!Utils.isNetworkConnected(context, LOGS)) {
            return;
        }
        SMTPTransport smtpTransport = null;
        try {
            smtpTransport = connectToSmtp("smtp.gmail.com",
                    587,
                    user,
                    token,
                    true);

            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            //message.setContent(body, "text/html; charset=utf-8");
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachments
            for (File file: files)
            {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(file.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Send the complete message parts
            message.setContent(multipart);

            try {
                message.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(email));
                smtpTransport.sendMessage(message, message.getAllRecipients());
            } finally {
                smtpTransport.close();
            }
        } catch (MessagingException e) {
            LOGS.error("Can't send mail with attachments", e);
            mAccountManager.invalidateAuthToken("com.google", token);
            initToken();
            sendMail(subject, body, user, email, files);
        }

    }
}
