package com.example.parakh.user_grievance;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Parakh
 */

public class FileAComplaintFragment extends Fragment implements AdapterView.OnItemSelectedListener{

RadioButton rb;
    RadioGroup rg;
    ImageView imageView;
    TextView textLoc;
    String imagetype;
    ProgressDialog progressDialog;
    Context context;
    Bundle b;
    TextInputLayout til_subject;
    View v;
    int temp;
    String subject = "", description = "", image = "", accessToken, secretKey;
    final private static String URL_FOR_COMPLAINT = Constants.SERVER + "/complaints/create_complaint";
    public FileAComplaintFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.activity_form, container, false);
        b = getActivity().getIntent().getExtras();
        accessToken = b.getString("accessToken");
        secretKey = b.getString("secretKey");
        context = getContext();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
       // textLoc = (TextView) v.findViewById(R.id.textView7);
        imageView = (ImageView) v.findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();

            }
        });

        Button submit = (Button) v.findViewById(R.id.button5);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                description = (((TextInputLayout) v.findViewById(R.id.textView6)).getEditText().getText()).toString();
                subject = (((TextInputLayout) v.findViewById(R.id.textView13)).getEditText().getText()).toString();

                //  Toast.makeText(getApplicationContext(),"descripion:"+description+" subject:"+subject+" image:"+image+" city:"+city+" state:"+state+" pincode:"+pincode+" lat:"+latitude+" long:"+longitude, Toast.LENGTH_SHORT).show();
                registerComplaint(subject,description);
            }
        });
        return v;
    }

    private void registerComplaint(final String subject,final String description) {
        // Tag used to cancel the request
        String cancel_req_tag = "register";
        // Toast.makeText(getApplicationContext(),"descripion:"+description+" subject:"+subject+" image:"+image+" city:"+city+" state:"+state+" pincode:"+pincode+" lat:"+latitude+" long:"+longitude, Toast.LENGTH_SHORT).show();

        progressDialog.setMessage("Filing your Complaint");
        showDialog();
       /* if(test == 0) {
            URL_FOR_REGISTRATION+="?name="+name+"&contact="+contact+"&email="+email+"&password="+password;
            test = 1;
        }*/
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_COMPLAINT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Form", "Register Response: " + response.toString());
                //Toast.makeText(getApplicationContext(),"Register Response: " + response.toString(), Toast.LENGTH_SHORT).show();
                hideDialog();
                if(response!=null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        //   boolean error = jObj.getBoolean("error");
                        String status = jObj.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(context,
                                    "Complaint Successfully Filed!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, HomePage.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        /*// Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();*/
                        } else {

                            String errorMsg = jObj.getString("error_message");
                            Toast.makeText(context,
                                    errorMsg, Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Form", "Registration Error: " + error.getMessage());
                Toast.makeText(context,"Error:"+
                        "Please Check your Internet Connection!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("subject", subject);
                params.put("description",description);
                params.put("image", "image.jpg");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map <String,String> params  = new HashMap<String, String>();
                params.put("access_token",accessToken);
                params.put("secret_key",secretKey);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(context).addToRequestQueue(strReq, cancel_req_tag);

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = resize(imageBitmap);
            imageView.setImageBitmap(imageBitmap);

            final String OBJECT_KEY="https://s3-ap-southeast-1.amazonaws.com/asarcgrs/ "+imageBitmap.toString()+".jpeg";
            image = OBJECT_KEY;
            // this example to work
            AWSCredentials credentials = new BasicAWSCredentials(
                    Constants.BUCKET_ACCESS_KEY_ID,
                    Constants.BUCKET_SECRET_KEY_ID);

            // create a client connection based on credentials
            final AmazonS3 s3client = new AmazonS3Client(credentials);
            // s3client.setEndpoint("s3.ap-south-1.amazonaws.com");
            // create bucket - name must be unique for all S3 users
            final String bucketName = Constants.BUCKET_NAME;
           /* s3client.putObject(new PutObjectRequest(bucketName, fileName,
                    new File("C:\\Users\\user\\Desktop\\testvideo.mp4"))
                    .withCannedAcl(CannedAccessControlList.PublicRead));*/

            Uri tempUri = getImageUri(context, imageBitmap);
            final String filePath = getRealPathFromURI(tempUri);
          /*  TransferManager manager = new TransferManager(credentials);
            Upload upload = manager.upload("bucket_name", "Android/" + OBJECT_KEY, file);*/
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    PutObjectRequest por = new PutObjectRequest( bucketName, OBJECT_KEY, new java.io.File( filePath) ).withCannedAcl(CannedAccessControlList.PublicRead);
                    s3client.putObject( por );
                }
            });

            /*File filesDir = context.getFilesDir();
            File imageFile = new File(filesDir,OBJECT_KEY);
            OutputStream os = null;
            try {
                os = new BufferedOutputStream(new FileOutputStream(imageFile));
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
                AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(Constants.BUCKET_ACCESS_KEY_ID,
                        Constants.BUCKET_SECRET_KEY_ID));
                TransferUtility transferUtility = new TransferUtility(s3, context);
                TransferObserver observer = transferUtility.upload(
                        Constants.BUCKET_NAME,
                        OBJECT_KEY,
                        imageFile
                );
                image = OBJECT_KEY;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/



           /* s3.putObject(new PutObjectRequest("your-bucket", "some-path/some-key.jpg", new File("somePath/someKey.jpg")).withCannedAcl(CannedAccessControlList.PublicRead));
            s3.getUrl("your-bucket", "some-path/some-key.jpg");*/
        }
        else if(requestCode == 0 )
        {
            Bitmap bitmap;
            super.onActivityResult(requestCode, resultCode, data);
            //  TextView textTargetUri;

          //  if (resultCode == RESULT_){
                Uri targetUri = data.getData();
                //   textTargetUri.setText(targetUri.toString());

                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                    bitmap = resize(bitmap);
                    imageView.setImageBitmap(bitmap);
                    final String OBJECT_KEY="https://s3-ap-southeast-1.amazonaws.com/asarcgrs/ "+bitmap.toString()+".jpeg";
                    image = OBJECT_KEY;
                    final String filePath = getRealPathFromURI(targetUri);
                    AWSCredentials credentials = new BasicAWSCredentials(
                            Constants.BUCKET_ACCESS_KEY_ID,
                            Constants.BUCKET_SECRET_KEY_ID);

                    // create a client connection based on credentials
                    final AmazonS3 s3client = new AmazonS3Client(credentials);
                    //  s3client.setEndpoint("s3.ap-south-1.amazonaws.com");
                    // create bucket - name must be unique for all S3 users
                    final String bucketName = Constants.BUCKET_NAME;
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            PutObjectRequest por = new PutObjectRequest( bucketName, OBJECT_KEY, new java.io.File( filePath) ).withCannedAcl(CannedAccessControlList.PublicRead);
                            s3client.putObject( por );
                        }
                    });
                    /*File filesDir = context.getFilesDir();
                    File imageFile = new File(filesDir,OBJECT_KEY);
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(imageFile));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        os.close();
                        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(Constants.BUCKET_ACCESS_KEY_ID,
                                Constants.BUCKET_SECRET_KEY_ID));
                        TransferUtility transferUtility = new TransferUtility(s3, context);

                        TransferObserver observer = transferUtility.upload(
                                Constants.BUCKET_NAME,
                                OBJECT_KEY,
                                imageFile
                        );
                        image = OBJECT_KEY;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
*/


                } catch (java.io.FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
  //  }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }




    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Form Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String type = parent.getItemAtPosition(position).toString();


    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }






    private Bitmap resize(Bitmap bitmap)
    {
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap,
                (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), false);
        return bitmapResized;
    }
    void showDialogBox()
    {
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.image_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Select",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text

                                rg = (RadioGroup)promptsView.findViewById(R.id.radioImage);
                                int selectedId = rg.getCheckedRadioButtonId();
                                rb = (RadioButton) promptsView.findViewById(selectedId);
                                imagetype = rb.getText().toString();
                                if(imagetype.equals("Gallery"))
                                {
                                    Intent intent = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, 0);
                                }
                                else
                                {
                                    dispatchTakePictureIntent();
                                }
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
