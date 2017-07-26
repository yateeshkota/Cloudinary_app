package sharma.yateesh.cloudinary_app.mCloud;

import java.util.HashMap;

/**
 * Created by welcome on 24-07-2017.
 */

public class MyConfiguration {

    public static HashMap getMyConfig(){

        HashMap config=new HashMap();
        config.put("cloud_name", "ddlo5lzpt");
        config.put("api_key", "675358991995299");
        config.put("api_secret", "nsjPjOjECT8bUyQYaGaFm54UhKw");


//        <meta-data android:name="CLOUDINARY_URL" android:value="cloudinary://675358991995299:nsjPjOjECT8bUyQYaGaFm54UhKw@ddlo5lzpt"/>
        return  config;
    }
}
