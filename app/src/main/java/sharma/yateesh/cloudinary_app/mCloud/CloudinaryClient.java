package sharma.yateesh.cloudinary_app.mCloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by welcome on 24-07-2017.
 */

public class CloudinaryClient {

    public static void  fg() {
        try{
        Cloudinary cloud=new Cloudinary(MyConfiguration.getMyConfig());

        File file = new File("my_image.jpg");
        Map uploadResult = cloud.uploader().upload(file, ObjectUtils.emptyMap());
        }catch (Exception e){}
    }

    public static String getRoundedCorners(){

        Cloudinary cloud=new Cloudinary(MyConfiguration.getMyConfig());


        Transformation t=new Transformation();
        t.radius(60);
        return cloud.url().transformation(t).generate("1_1_y9gvj3");
    }

    public static String resize(){

        Cloudinary cloud=new Cloudinary(MyConfiguration.getMyConfig());


        Transformation t=new Transformation();
        t.width(400);
        t.height(200);
        return cloud.url().transformation(t).generate("1_1_y9gvj3");
    }

}
