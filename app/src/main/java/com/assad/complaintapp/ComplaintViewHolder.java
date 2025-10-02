package com.assad.complaintapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ComplaintViewHolder extends RecyclerView.ViewHolder{

    TextView idView, dateView, factoryView, groupView, typeView, subtypeView, detailView, remarksView;
    TextView complaintStatus;


    public ComplaintViewHolder(@NonNull View itemView) {
        super(itemView);
        idView = itemView.findViewById(R.id.textViewId);
        dateView = itemView.findViewById(R.id.textViewDate);
        factoryView = itemView.findViewById(R.id.textViewFactory);
        groupView = itemView.findViewById(R.id.textViewGroup);
        typeView = itemView.findViewById(R.id.textViewType);
        subtypeView = itemView.findViewById(R.id.textViewSubtype);
        detailView = itemView.findViewById(R.id.textViewDetail);
        remarksView = itemView.findViewById(R.id.textViewRemarks);
        complaintStatus = itemView.findViewById(R.id.complaintStatus);



    }
}
