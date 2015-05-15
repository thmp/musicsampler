package cdtm;

import cdtm.sampler.SamplePlayers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpUpdateThread extends Thread {

    @Override
    public void run() {
        System.out.println("HTTP update");

        String url = "http://192.168.0.104:4567/sensors";
        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            //con.setRequestMethod("GET");

            //add request header
            //con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            //System.out.println("\nSending 'GET' request to URL : " + url);
            //System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            //System.out.println(response.toString());

            int volume = Integer.parseInt(response.toString());
            SamplePlayers.getInstance().active[4] = volume > 50;


            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(new HttpUpdateThread(), 1, TimeUnit.SECONDS);

        } catch(Exception e) {

        }

    }

}
