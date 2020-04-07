package com.example.myfirstapp;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfirstapp.model.AdminChatVO;

import java.util.ArrayList;

public class AdminChatAdapter extends RecyclerView.Adapter<AdminChatAdapter.ViewHolder> {

    private ArrayList<AdminChatVO> AdminChatVOList = null;
    private String TAG = "ChatAdapt";

    AdminChatAdapter(){};
    AdminChatAdapter(ArrayList<AdminChatVO> list){ AdminChatVOList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.activity_admin_hall, parent, false) ;
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminChatVO chatVO = AdminChatVOList.get(position);
        holder.userIdView.setText(chatVO.getUserId());

        Log.d(TAG, "onBindViewHolder: Success" );
    }

    @Override
    public int getItemCount() {
        return AdminChatVOList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private AdminChatAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(AdminChatAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdView;
        TextView chatRoomView;
        TextView lastchatView;
        TextView lastChatTimestamp;
        Button selectBtn;
        Button deleteBtn;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);

            Log.d("ViewHolder", "ViewHolder: Created");
            userIdView = (TextView) convertView.findViewById(R.id.userIDView);
//            chatRoomView = (TextView) convertView.findViewById(R.id.chatRoomVIew);
//            lastchatView = (TextView) convertView.findViewById(R.id.lastChatView);
//            lastChatTimestamp = (TextView) convertView.findViewById(R.id.lastChatTimestampVIew);
            selectBtn = (Button)convertView.findViewById(R.id.insertBtn);
            deleteBtn = (Button)convertView.findViewById(R.id.resetBtn);

            selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                        }
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });

        }
    }
}

