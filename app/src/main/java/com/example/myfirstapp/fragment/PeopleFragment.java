package com.example.myfirstapp.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapp.MessageActivity;
import com.example.myfirstapp.R;
import com.example.myfirstapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager((new LinearLayoutManager((inflater.getContext()))));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        //return super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModelList;
        public PeopleFragmentRecyclerViewAdapter() {
            userModelList = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModelList.clear();

                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if(userModel.uid.equals(myUid)){
                            continue;
                        }
                        userModelList.add(userModel);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.baseline_account_circle_black_36).apply(new RequestOptions().circleCrop()).into( ((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(userModelList.get(position).userName+userModelList.get(position).uid);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid", userModelList.get(position).uid);
//                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromleft, R.anim.toleft);
//                    startActivity(intent, activityOptions.toBundle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModelList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView)view.findViewById(R.id.frienditem_imageview);
                textView = (TextView)view.findViewById(R.id.frienditem_textview);
            }
        }
    }
}
