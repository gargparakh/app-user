package com.example.parakh.user_grievance;

/**
 * Created by pari on 26-03-2017.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ComplaintObjectHolder extends RecyclerView.ViewHolder {
    TextView subject,description,status,created_at,resolved;
    public ComplaintObjectHolder(View itemView) {
        super(itemView);
        description = (TextView)itemView.findViewById(R.id.textView11);
        subject = (TextView)itemView.findViewById(R.id.textView10);
        status = (TextView)itemView.findViewById(R.id.textView);
        created_at = (TextView)itemView.findViewById(R.id.textView13);
        resolved = (TextView)itemView.findViewById(R.id.textView14);
    }

}
