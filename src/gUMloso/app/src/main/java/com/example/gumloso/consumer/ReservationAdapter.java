package com.example.gumloso.consumer;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumloso.Booking;
import com.example.gumloso.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private final List<Booking> mData;
    private ItemClickListener mClickListener;
    private final boolean isForConsumer;

    public ReservationAdapter(List<Booking> mData, boolean isForConsumer) {
        this.mData = mData;
        this.isForConsumer = isForConsumer;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View reservationView = inflater.inflate(R.layout.reservation_item, parent, false);

        return new ReservationViewHolder(reservationView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Booking res = mData.get(position);
        String pessoas_reserva = "Nr. pessoas " + res.getNumberOfPeople();
        holder.txtReservation.setText(pessoas_reserva);
        String hora = "Hora "+ res.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        holder.horaReservation.setText(hora);
        String data = "Data " + res.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        holder.dateReservation.setText(data);
        holder.nameReservation.setText(isForConsumer ? res.getRestaurant().getName() : res.getOwner());
        if(isForConsumer)
            holder.favRestImg.setVisibility(res.getRestaurant().isFavorite() ? View.VISIBLE : View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtReservation;
        TextView horaReservation;
        TextView dateReservation;
        TextView nameReservation;
        ImageView favRestImg;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReservation = itemView.findViewById(R.id.nrPessoas_reserva);
            dateReservation = itemView.findViewById(R.id.data_reserve);
            horaReservation = itemView.findViewById(R.id.hora_reserva);
            nameReservation = itemView.findViewById(R.id.reservation_name);
            favRestImg = itemView.findViewById(R.id.favRestImg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Booking getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
