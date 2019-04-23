package com.quangedm2202.fithou_chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 24/03/2019.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private Context context;

    private FirebaseAuth mAuth;

    private StorageReference mImageStorage;


    public MessageAdapter(List<Messages> mMessageList, Context context) {

        this.mMessageList = mMessageList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mImageStorage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public int getItemViewType(int position) {
        // lay id nguoi dung
        Log.d("Current User: ", mAuth.getCurrentUser().getUid());
        //
        Log.d("Send by: ", mMessageList.get(position).getFrom());
        return (mMessageList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid()) ? 1 : 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // neu = 0 thi la cua nguoi kia thi can phai
        if (viewType == 0) {
            View vOther = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout_other, parent, false);

            return new OtherMessageViewHolder(vOther);

        }
        // Nguoc lai neu la 1  thi la cua nguoi dang dang nhap thi can trai
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_single_layout, parent, false);

            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        // Neu = 0 thi day la tin nhan cua nguoi dung khac , can phai
        if (holder.getItemViewType() == 0) {

            final OtherMessageViewHolder viewHolder = (OtherMessageViewHolder) holder;
            Messages c = mMessageList.get(i);

            String from_user = c.getFrom();
            String message_type = c.getType();

            //07/04
            String message_user_id = mAuth.getCurrentUser().getUid();
            if (from_user.equals(message_user_id)) {
                viewHolder.messageText.setBackgroundResource(R.drawable.background_right);
                viewHolder.messageText.setTextColor(Color.WHITE);
//            viewHolder.messageText.setGravity(Gravity.RIGHT);
            } else {
                viewHolder.messageText.setBackgroundResource(R.drawable.background_left);
                viewHolder.messageText.setTextColor(Color.WHITE);
//            viewHolder.messageText.setGravity(Gravity.LEFT);
            }

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    viewHolder.displayName.setText(name);

                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avata).into(viewHolder.profileImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //convert thoi gian sang string
//        String time = new Long(c.getTime()).toString();
//        viewHolder.timeText.setText(time);

            String Date = DateFormat.getDateTimeInstance().format(c.getTime());
            viewHolder.timeText.setText(Date.toString());


            //ban dau thi hien tiem nhu the nay
//        Timestamp ts=new Timestamp(c.getTime());
//        Date date=ts;
//        viewHolder.timeText.setText(date.toString());

            //neu tin nhan la chu~ (text)
            if (message_type.equals("text")) {

                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);
                // neu tin nhan la image (anh)
            } else if (message_type.equals("image")) {

                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.VISIBLE);
//            Picasso.get().load(c.getMessage())
//                    .placeholder(R.drawable.default_avata).into(viewHolder.messageImage);
                Picasso.get()
                        .load(c.getMessage())
                        .placeholder(R.drawable.default_avata)
                        .fit()
                        .centerInside()
                        .into(viewHolder.messageImage);

            } else if (message_type.equals("file")) {
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String firebaseFilePath = viewHolder.messageText.getText().toString();
//                downloadFile("files/ThucHanhNgay20190318.txt");
                        downloadFile(firebaseFilePath);
                        Log.d("MessageAdapter", "Clicked");
                    }
                });
            }
        }


        // Neu = 1 thi day la tin nhan Cua nguoi dang dang nhap
        else {
            final MessageViewHolder viewHolder = (MessageViewHolder) holder;
            //Tin nhan thu i

            Messages c = mMessageList.get(i);

            String from_user = c.getFrom();
            String message_type = c.getType();

            //07/04
            String message_user_id = mAuth.getCurrentUser().getUid();
            if (from_user.equals(message_user_id)) {
                viewHolder.messageText.setBackgroundResource(R.drawable.background_right);
                viewHolder.messageText.setTextColor(Color.WHITE);
//            viewHolder.messageText.setGravity(Gravity.RIGHT);
            } else {
                viewHolder.messageText.setBackgroundResource(R.drawable.background_left);
                viewHolder.messageText.setTextColor(Color.WHITE);
//            viewHolder.messageText.setGravity(Gravity.LEFT);
            }

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    viewHolder.displayName.setText(name);

                    Picasso.get().load(image)
                            .placeholder(R.drawable.default_avata).into(viewHolder.profileImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //convert thoi gian sang string
//        String time = new Long(c.getTime()).toString();
//        viewHolder.timeText.setText(time);

            String Date = DateFormat.getDateTimeInstance().format(c.getTime());
            viewHolder.timeText.setText(Date.toString());


            //ban dau thi hien tiem nhu the nay
//        Timestamp ts=new Timestamp(c.getTime());
//        Date date=ts;
//        viewHolder.timeText.setText(date.toString());

            //neu tin nhan la chu~ (text)
            if (message_type.equals("text")) {

                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setVisibility(View.VISIBLE);
                // neu tin nhan la image (anh)
            } else if (message_type.equals("image")) {

                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.VISIBLE);
//            Picasso.get().load(c.getMessage())
//                    .placeholder(R.drawable.default_avata).into(viewHolder.messageImage);
                Picasso.get()
                        .load(c.getMessage())
                        .placeholder(R.drawable.default_avata)
                        .fit()
                        .centerInside()
                        .into(viewHolder.messageImage);

            } else if (message_type.equals("file")) {
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.messageImage.setVisibility(View.GONE);
                viewHolder.messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String firebaseFilePath = viewHolder.messageText.getText().toString();
//                downloadFile("files/ThucHanhNgay20190318.txt");
                        downloadFile(firebaseFilePath);
                        Log.d("MessageAdapter", "Clicked");
                    }
                });
            }
        }

    }

    public class OtherMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView timeText;

        public OtherMessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout_other);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout_other);
            displayName = (TextView) view.findViewById(R.id.name_text_layout_other);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout_other);
            timeText = (TextView) view.findViewById(R.id.time_text_layout_other);


        }
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public TextView timeText;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            timeText = (TextView) view.findViewById(R.id.time_text_layout);


        }
    }


    private void downloadFile(String firebaseFilePath) {
        Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show();
        StorageReference islandRef = mImageStorage.child("files/" + firebaseFilePath);
//        Log.d("MessageAdapetr", firebaseFilePath);

        String fileName = firebaseFilePath.substring(firebaseFilePath.lastIndexOf('/') + 1);


        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath, fileName);

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "File Download Succeed", Toast.LENGTH_LONG).show();
//                Log.d("MessageAdapter", "Success");
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "File Download Failed", Toast.LENGTH_LONG).show();
//                Log.d("MessageAdapter", "Error");
                //Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}