package sharma.yateesh.cloudinary_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import sharma.yateesh.cloudinary_app.mCloud.CloudinaryClient;
import sharma.yateesh.cloudinary_app.mCloud.MyConfiguration;
import sharma.yateesh.cloudinary_app.mPicasso.PicassoClient;

public class MainActivity extends AppCompatActivity {

    public Button button;
    public ImageView img;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Boolean resize = false;
    static final int REQUEST_UPLOAD = 1;
    static final int SELECT_VIDEO = 3;
    private final static int SELECT_PICTURE = 2;
    Button btn_img_cam, btn_vi_cam, btn_img_gell, btn_video_gell, btn_1;
    String publicId, signature, timestamp, api_key;
    public static ProgressBar dialog;
    static TextView txt_1, txt_2, txt_3;
    ByteArrayInputStream bs;

    TextView ShowText;
    int progressBarValue = 0;
    Handler handler = new Handler();
    static boolean isStart;
    InputStream iStream;
    static long start, runTime;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_img_cam = (Button) findViewById(R.id.btn_img_cam);
        btn_vi_cam = (Button) findViewById(R.id.btn_vi_cam);
        btn_img_gell = (Button) findViewById(R.id.btn_img_gell);
        btn_video_gell = (Button) findViewById(R.id.btn_video_gell);
        btn_1 = (Button) findViewById(R.id.btn_1);
        dialog = (ProgressBar) findViewById(R.id.progressBar1);

        txt_1 = (TextView) findViewById(R.id.txt_1);
        txt_2 = (TextView) findViewById(R.id.txt_2);
        txt_3 = (TextView) findViewById(R.id.txt_3);
        ShowText = (TextView) findViewById(R.id.textView1);

        img = (ImageView) findViewById(R.id.movieImage);

        btn_img_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_UPLOAD);
//////                if (resize){
//                    PicassoClient.downloadImage(MainActivity.this, CloudinaryClient.resize(),img);
//                    resize=false;
//                }else {
//                    PicassoClient.downloadImage(MainActivity.this, CloudinaryClient.getRoundedCorners(),img);
//                    resize=true;                }
//
            }
        });
        btn_img_gell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 0;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

            }
        });

        btn_vi_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = 1;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_UPLOAD);
            }
        });
        btn_video_gell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = 1;
                Intent vid_intent = new Intent();
                vid_intent.setType("video/*");
                vid_intent.setAction(Intent.ACTION_GET_CONTENT);
                vid_intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(vid_intent, "Select Picture"), SELECT_VIDEO);

                //   activity.startActivityForResult(Intent.createChooser(vid_intent, "Select Video"), Config.REQUEST_VIDEO_TRIMMER);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_UPLOAD);
            }
        });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                start = System.currentTimeMillis();
                String start_time = String.valueOf(start);
                txt_1.setText(start_time);
                dialog.setVisibility(View.VISIBLE);
                //   ((ProgressBar) findViewById(R.id.progressBarHorizontal)).setProgress(0);
                if (i==0){
                uploader2(bs);
                }else{
                    uploader(iStream);
                }

            }
        });
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (isStart) {
                    progressBarValue++;
                }
                dialog.setProgress(progressBarValue);
                ShowText.setText(String.valueOf(progressBarValue / 60) + ":" + String.valueOf(progressBarValue % 60));

                handler.sendEmptyMessageDelayed(0, 100);
            }
        };

        handler.sendEmptyMessage(0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//

        // this is use for camera
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_UPLOAD) {
//


                Bitmap photo = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(photo);


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                bs = new ByteArrayInputStream(bitmapdata);
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
//                Uri tempUri = getImageUri(getApplicationContext(), photo);
//
//                // CALL THIS METHOD TO GET THE ACTUAL PATH
//                File finalFile = new File(getRealPathFromURI(tempUri));
////
////                //imagePath = String.valueOf(finalFile);
//
//                Map config = new HashMap();
//                config.put("cloud_name", "ddlo5lzpt");
//                config.put("api_key", "675358991995299");
//                config.put("api_secret", "nsjPjOjECT8bUyQYaGaFm54UhKw");
//                Cloudinary cloudinary = new Cloudinary(config);
//
//
//                int SDK_INT = android.os.Build.VERSION.SDK_INT;
//                if (SDK_INT > 8) {
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                            .permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    //your codes here
//                    try {
//                        //  cloudinary.uploader().upload(photoFile.getAbsolutePath(),          Cloudinary.emptyMap());
//                        Map<String, String> m = cloudinary.uploader().upload(bs, ObjectUtils.emptyMap());
//                        //   cloudinary.uploader().upload(bs,ObjectUtils.asMap("public_id", publicId, "signature", signature, "timestamp", timestamp, "api_key", api_key));
//                        //  cloudinary.uploader().unsignedUpload(bs,"yo",ObjectUtils.emptyMap());
//                        // UploadPhotoActivity u=new UploadPhotoActivity();
//                        //  u.startUpload();
//                        if (m.containsKey("url")) {
//                            Object value = m.get("url");
//                            System.out.println("Key : " + "url" + " value :" + value);
//                        }
//                        String string = m.toString();
//                        Log.d("helll", string);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }


            } else if (requestCode == SELECT_PICTURE) {
                Log.d("requestCode : ", String.valueOf(SELECT_PICTURE));

                Uri selectedImage = data.getData();
                img.setImageURI(selectedImage);
                File file = new File(selectedImage.toString());

                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    // uploader2(fileInputStream);
                } catch (Exception e) {
                    System.out.println(e);
                }


                String picturePath = getPath(getApplicationContext(), selectedImage);
                //   imagePath = picturePath;


//
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    bs = new ByteArrayInputStream(bitmapdata);
                    //  uploader2(bs2);
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver() , Uri.parse(selectedImage));

                } catch (Exception e) {
                    //handle exception
                }

            } else if (requestCode == SELECT_VIDEO) {
                Uri selectedVideoUri = data.getData();
                String videoPath = "";

                if (selectedVideoUri != null) {
                    try {
                        iStream = getContentResolver().openInputStream(selectedVideoUri);
                        videoPath = getFilePath(getApplicationContext(), selectedVideoUri);
                      //  uploader(iStream);
                    } catch (Exception e) {
                    }


                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                    img.setImageBitmap(bitmap);
//                        ((ImageView) findViewById(R.id.vid_add_video)).setImageBitmap(bitmap);
                }


            }
        }
    }


    public static void uploader(InputStream fileInputStream) {
        Log.d("requestCode : ", "get in");
        Map config = new HashMap();
        config.put("cloud_name", "ddlo5lzpt");
        config.put("api_key", "675358991995299");
        config.put("api_secret", "nsjPjOjECT8bUyQYaGaFm54UhKw");
        Cloudinary cloudinary = new Cloudinary(config);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                //  cloudinary.uploader().upload(photoFile.getAbsolutePath(),          Cloudinary.emptyMap());
                Map<String, String> m = cloudinary.uploader().upload(fileInputStream, ObjectUtils.asMap("resource_type", "video"));
                //   cloudinary.uploader().upload(bs,ObjectUtils.asMap("public_id", publicId, "signature", signature, "timestamp", timestamp, "api_key", api_key));
                //  cloudinary.uploader().unsignedUpload(bs,"yo",ObjectUtils.emptyMap());
                // UploadPhotoActivity u=new UploadPhotoActivity();
                //  u.startUpload();
                if (m.containsKey("url")) {
                    Object value = m.get("url");
                    System.out.println("Key : " + "url" + " value :" + value);
                }
                String string = m.toString();
                Log.d("helll", string);

                long timer = System.currentTimeMillis();
                String ti3 = String.valueOf(timer);
                txt_2.setText(ti3);

                runTime = System.currentTimeMillis() - start;
                String time = String.valueOf(runTime);
                //  Integer time6=Integer.valueOf(runTime);
                Integer i = (int) (long) runTime;

                txt_3.setText(time);


                dialog.setProgress(0);
                // dialog.setProgress(i[0]);
                dialog.setProgress(i);
                isStart = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void uploader2(ByteArrayInputStream fileInputStream) {
        Log.d("requestCode : ", "get in");
        Map config = new HashMap();
        config.put("cloud_name", "ddlo5lzpt");
        config.put("api_key", "675358991995299");
        config.put("api_secret", "nsjPjOjECT8bUyQYaGaFm54UhKw");
        Cloudinary cloudinary = new Cloudinary(config);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                //  cloudinary.uploader().upload(photoFile.getAbsolutePath(),          Cloudinary.emptyMap());
                Map<String, String> m = cloudinary.uploader().upload(fileInputStream, ObjectUtils.emptyMap());
                //   cloudinary.uploader().upload(bs,ObjectUtils.asMap("public_id", publicId, "signature", signature, "timestamp", timestamp, "api_key", api_key));
                //  cloudinary.uploader().unsignedUpload(bs,"yo",ObjectUtils.emptyMap());
                // UploadPhotoActivity u=new UploadPhotoActivity();
                //  u.startUpload();

                if (m.containsKey("url")) {
                    Object value = m.get("url");
                    System.out.println("Key : " + "url" + " value :" + value);
                }
                String string = m.toString();
                Log.d("helll", string);

                long timer = System.currentTimeMillis();
                String ti3 = String.valueOf(timer);
                txt_2.setText(ti3);

                runTime = System.currentTimeMillis() - start;
                String time = String.valueOf(runTime);
                //  Integer time6=Integer.valueOf(runTime);
                Integer i = (int) (long) runTime;

                txt_3.setText(time);


                dialog.setProgress(0);
                // dialog.setProgress(i[0]);
                dialog.setProgress(i);
                isStart = false;
                // dialog.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    //    they both can gaet path from the camera gallery
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        //  imagePath = cursor.getString(idx);
        return cursor.getString(idx);
    }


}
