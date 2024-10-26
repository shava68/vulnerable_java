import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VulnerableJavaApplication {

    // Уязвимость 1: Использование устаревшего алгоритма DES
    public static String encryptSensitiveData(String data) throws Exception {
        String key = "12345678"; // DES использует 8-байтный ключ
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // Использование небезопасного режима ECB
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "DES");

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return new String(encryptedData);
    }

    // Уязвимость 2: Отключение проверки сертификатов SSL/TLS
    public static void connectToServer(String urlString) throws Exception {
        // Отключаем проверку SSL сертификатов (очень небезопасно)
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Уязвимая логика подключения
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
    }

    // Уязвимость 3: SQL-инъекция
    public static void getUserData(String userId) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stmt = conn.createStatement();

            // Уязвимость: SQL-инъекция, строка не параметризована
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("User ID: " + rs.getString("id") + ", Username: " + rs.getString("username"));
            }
        } catch (Exception e) {
            // Уязвимость 4: Подробные сообщения об ошибках
            e.printStackTrace(); // Возврат подробной информации об ошибке пользователю
        }
    }

    // Уязвимость 5: Инъекция команд через ping
    public static void pingHost(String ip) {
        try {
            // Уязвимость: выполнение пользовательской команды напрямую
            String command = "ping -c 4 " + ip;
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

        } catch (Exception e) {
            
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Пример уязвимого шифрования данных с использованием DES-ECB
            String sensitiveData = scanner.nextLine();
            String encryptedData = encryptSensitiveData(sensitiveData);

            // Пример уязвимости при подключении к серверу с отключенной проверкой сертификатов
            String url = scanner.nextLine();
            connectToServer(url);

            // Пример SQL-инъекции
            String userId = scanner.nextLine();
            getUserData(userId);

            // Пример инъекции команд ОС через ping
            String ip = scanner.nextLine();
            pingHost(ip);
        } catch (Exception e) {

        }
    }
}