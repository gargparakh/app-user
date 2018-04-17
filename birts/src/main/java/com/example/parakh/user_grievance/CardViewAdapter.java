package com.example.parakh.user_grievance;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class CardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ComplaintObject> items = new ArrayList<ComplaintObject>();
    View view;
    Context context;
    String name,email;
    Boolean verified;
    public CardViewAdapter(ArrayList<ComplaintObject> items,Context context,String name,String email,Boolean verified)
    {
        this.items=items;
        this.context = context;
        this.name = name;
        this.email = email;
        this.verified= verified;
       // this.phone_no_verified=phone_no_verified;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_complaints, parent, false);
        final ComplaintObjectHolder complaintObjectHolder = new ComplaintObjectHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditComplaint.class);
                int position = complaintObjectHolder.getPosition();
                ComplaintObject co = items.get(position);
                Bundle b = new Bundle();
                b.putString("id",co.getId());
                b.putString("subject",co.getSubject());
                b.putString("description",co.getDescription());
                b.putString("image",co.getImage());
                b.putString("created_at",co.getCreated_at());
                b.putString("updated_at",co.getUpdated_at());
                b.putString("user_id",co.getUserid());
                b.putString("status",co.getStatus());
               // b.putString("priority",co.getPriority());
                b.putString("accessToken",co.getAccessToken());
                b.putString("secretKey",co.getSecretKey());
                b.putString("name",name);
                b.putString("email",email);
                b.putBoolean("verified",verified);
               // b.putBoolean("phone_no_verified",phone_no_verified);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        return complaintObjectHolder;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ComplaintObjectHolder vh1 = (ComplaintObjectHolder) holder;
       // vh1.id.setText("Complaint ID : "+items.get(position).getId());
        vh1.subject.setText(items.get(position).getId()+". Subject : "+items.get(position).getSubject());
        vh1.description.setText("Description : "+items.get(position).getDescription());
        vh1.status.setText("Status : "+items.get(position).getStatus());
        vh1.created_at.setText("Created At : "+items.get(position).getCreated_at());
     //   vh1.resolved.setText("Resolved : "+items.get(position).getCreated_at());
      /*  vh1.latitude.setText("Latitude : "+items.get(position).getLatitude());
        vh1.longitude.setText("Longitude : "+items.get(position).getLongitude());
        vh1.city.setText("City : "+items.get(position).getCity());
        vh1.state.setText("State : "+items.get(position).getState());
        vh1.pincode.setText("Pincode : "+items.get(position).getPincode());
        vh1.updated_at.setText("Updated At : "+items.get(position).getUpdated_at());
        vh1.userid.setText("User ID : "+items.get(position).getUserid());
        vh1.priority.setText("Priority : "+items.get(position).getPriority());
        vh1.status.setText("Status : "+items.get(position).getState());*/

    /*    AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(Constants.BUCKET_ACCESS_KEY_ID,
                Constants.BUCKET_SECRET_KEY_ID));
        TransferUtility transferUtility = new TransferUtility(s3, context);
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir,key);
        TransferObserver observer = transferUtility.download(
                Constants.BUCKET_NAME,
                key,
                imageFile
        );
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
