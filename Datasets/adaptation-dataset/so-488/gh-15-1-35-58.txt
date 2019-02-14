package gov.nih.ncgc.bard.capextract;

import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * Get a {@link org.apache.http.client.HttpClient} that uses SSL and accepts self signed certificates.
 * <p/>
 * We make this a singleton/factory so that we can reuse the client in
 * multiple threads. This will avoid us opening multiple connections to the same
 * server (and possible avoids time out/wait problems). Taken from
 * <a href="http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html">
 * http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html</a>
 * but also see <a href="http://foo.jasonhudgins.com/2010/03/http-connections-revisited.html">
 * http://foo.jasonhudgins.com/2010/03/http-connections-revisited.html</a>
 *
 * @author Rajarshi Guha
 */
public class SslHttpClient {
    private static DefaultHttpClient client = null;

    // taken from http://stackoverflow.com/a/4837230/58681
    public static HttpClient getHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new LooseSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}
