package com.assad.complaintapp;

import static java.lang.String.valueOf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintViewHolder> {

    @NonNull
    private Context context;
    private List<Complaints> complaintsList;

    public ComplaintAdapter(List<Complaints> complaintsList, @NonNull Context context) {
        this.complaintsList = complaintsList;
        this.context = context;
    }

    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ComplaintViewHolder(LayoutInflater.from(context).inflate(R.layout.complaint_detaili_view, parent, false));
    }

    public void updateData(List<Complaints> newItems) {
        this.complaintsList.clear();
        this.complaintsList.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {

        if (holder.idView != null) {
            holder.idView.setText(valueOf(complaintsList.get(position).getId()));
        }

        if (holder.dateView != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = dateFormat.parse(valueOf(complaintsList.get(position).getCreationdate()));

                SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
                String formattedDate = outputFormat.format(date);

                holder.dateView.setText(formattedDate);
            } catch (ParseException e) {
                // Handle parsing error (e.g., display an error message or default value)
                holder.dateView.setText("Invalid date");
            }
            //.setDaText(expensesList.get(position).getDate());
        }

        if(holder.factoryView!=null){
            holder.factoryView.setText(complaintsList.get(position).getFactoryname());
        }

        if (holder.groupView != null) {
            holder.groupView.setText(complaintsList.get(position).getGroupname() + " \n"+ complaintsList.get(position).getTypename());
        }

        if (holder.typeView != null) {
            holder.typeView.setText(complaintsList.get(position).getTypename());
        }

        if (holder.subtypeView!=null){
            holder.subtypeView.setText(complaintsList.get(position).getSubtypename());
        }

        if(holder.detailView!=null){
            holder.detailView.setText(complaintsList.get(position).getDetail());
        }

        if (holder.remarksView != null) {
            holder.remarksView.setText(complaintsList.get(position).getRemarks());
        }
        if (holder.complaintStatus != null) {
            holder.complaintStatus.setText(complaintsList.get(position).getStatus());
        }
        /*
        if (holder.spinnerStatus != null) {
            String status = String.valueOf(complaintsList.get(position).getStatus());
            if (status.equals("Pending")) {
                holder.spinnerStatus.setSelection(0);
            } else if (status.equals("Done")) {
                holder.spinnerStatus.setSelection(1);
            }else if (status.equals("In Progress")) {
                holder.spinnerStatus.setSelection(2);
            }
        }
        */

        /*
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedParentItem = String.valueOf(holder.spinnerStatus.getSelectedItem());
                Toast.makeText(context, selectedParentItem + " selected" + valueOf(complaintsList.get(position).getId()), Toast.LENGTH_SHORT).show();
                //holder.detailView.setText(complaintsList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        */



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }



    // Inside your ExpenseAdapter class
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return complaintsList.size();
    }



}
