package com.example.pam_googlemapsfirebase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pam_googlemapsfirebase.R;
import com.example.pam_googlemapsfirebase.model.Pesanan;

import java.util.List;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.MyViewHolder> {

    private Context context;
    private List<Pesanan> list;
    private Dialog dialog;

    public interface Dialog {
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public PesananAdapter(Context context, List<Pesanan> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pesanan, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.namapesanan.setText(list.get(position).getName());
        holder.alamatawal.setText(list.get(position).getTitikawal());
        holder.alamattujuan.setText(list.get(position).getTujuan());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namapesanan, alamatawal, alamattujuan;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namapesanan = itemView.findViewById(R.id.namapesanan);
            alamatawal = itemView.findViewById(R.id.alamatawal);
            alamattujuan = itemView.findViewById(R.id.alamattujuan);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dialog != null) {
                        dialog.onClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
