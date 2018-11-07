package com.petergeras.cameraandcloud.inApp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.petergeras.cameraandcloud.R;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> commentList;

    public class PhotoViewHolder extends RecyclerView.ViewHolder{


        public ImageView selectedImageView;
        public ImageView likeBtn;
        public ImageView commentBtn;

        public TextView likeCount;
        public TextView publishedBy;
        public TextView commentCount;
        public TextView dateTV;
        public TextView name;
        public TextView userComment;


        public long commentCountValue;

        public FirebaseDatabase database;
        public DatabaseReference databaseRef;
        public FirebaseAuth firebaseAuth;



        public String uid;


        public PhotoViewHolder(View view) {
            super(view);

            likeCount = view.findViewById(R.id.likes_TV);
            selectedImageView = view.findViewById(R.id.photo);
            likeBtn = view.findViewById(R.id.like_inactive);
            likeCount = view.findViewById(R.id.likes_TV);
            publishedBy = view.findViewById(R.id.publishedBy);
            commentBtn = view.findViewById(R.id.commentIcon);
            commentCount = view.findViewById(R.id.comments_TV);
            dateTV = view.findViewById(R.id.date);
            name = view.findViewById(R.id.name);
            userComment = view.findViewById(R.id.comment);

            likeBtn = view.findViewById(R.id.like_inactive);


            database = FirebaseDatabase.getInstance();
            databaseRef = database.getReference();
            databaseRef = databaseRef.child("images");

            firebaseAuth = FirebaseAuth.getInstance();

            uid = firebaseAuth.getCurrentUser().getUid();




            // When the user likes the post
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final DatabaseReference ref = databaseRef.child(MainActivity.selectedUploadInfoWithKey.getKey()).child("likes");

                    ref.addListenerForSingleValueEvent (new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // If the current user likes the post then the like button will change
                            // colors to red
                            if (!dataSnapshot.hasChild(uid)) {
                                ref.child(uid).setValue(true);
                                likeBtn.setImageResource(R.mipmap.ic_favorite_active);

                            }
                            // If the current user unlikes the post then like button will will
                            // change colors to gray
                            else {
                                ref.child(uid).removeValue();
                                likeBtn.setImageResource(R.mipmap.ic_favorite_inactive);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });





         }
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder{

        public ImageView sendBtn;

        public TextView user;
        public TextView comment;

        public FirebaseDatabase database;



        public CommentViewHolder(View view) {
            super(view);

            user = view.findViewById(R.id.name);
            comment = view.findViewById(R.id.comment);

        }
    }


    public RecyclerViewAdapter(List<Object> commentList) {
        this.commentList = commentList;
    }


    @Override
    public int getItemViewType(int position) {
         return position;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        // ViewType is the item position got from the method getItemViewType. If viewType or item
        // position is 0 - it means the first item. The photo_layout is assigned to position 0 in
        // the RecyclerView in fragment_photo.
        if(viewType == 0){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_layout, parent, false);

            return new PhotoViewHolder(view);

        }
        // The comment_layout is assigned to the remaining positions in the RecyclerView in
        // fragment_photo.
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_layout, parent, false);

            return new CommentViewHolder(view);

        }
     }


    // Sets all the data from the Firebase to the layout
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        if(position == 0){

            PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
            PhotoInfo photoInfo = (PhotoInfo) commentList.get(0);

            holder.selectedImageView.setImageDrawable(MainActivity.selectedImageView);

            holder.likeCount.setText((photoInfo.getLikeCountValue() + " Likes"));

            holder.publishedBy.setText("Published By: " + photoInfo.getEmail());

            holder.commentCount.setText(photoInfo.getCommentCountValue() + " Comments");
            holder.dateTV.setText("Date: " + photoInfo.getDate());


            if (photoInfo.getLiked()) {
                holder.likeBtn.setImageResource(R.mipmap.ic_favorite_active);
            }
            else {
                holder.likeBtn.setImageResource(R.mipmap.ic_favorite_inactive);
            }

        }
        else {

            CommentViewHolder holder = (CommentViewHolder) viewHolder;
            Comment comment = (Comment) commentList.get(position);
            holder.comment.setText(comment.getComment());
            holder.user.setText(comment.getEmail());

        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
