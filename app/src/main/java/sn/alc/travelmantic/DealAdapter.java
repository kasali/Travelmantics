package sn.alc.travelmantic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewDealHolder>  {
    ArrayList<TravelDeal> deals=null;
    Context context;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;
    private TravelDeal deal;

    public DealAdapter(Context context) {
        databaseReference=FirebaseUtil.databaseReference;
        deals = FirebaseUtil.mdeals;
        this.context = context;
        listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               TravelDeal deal=dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal", "onChildAdded: "+deal.getTitle());
                deal.setId(dataSnapshot.getKey());
                deals.add(deal);
                notifyItemInserted(deals.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(listener);
    }

    @NonNull
    @Override
    public ViewDealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.row_deal,parent,false);
        return new ViewDealHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewDealHolder holder, int position) {
         TravelDeal deal=deals.get(position);
         holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return (deals == null) ? 0 : deals.size();
    }

    public class ViewDealHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text_title,text_description,text_price;
        public ViewDealHolder(@NonNull View itemView) {
            super(itemView);
            text_title= itemView.findViewById(R.id.titleDeal);
            text_price= itemView.findViewById(R.id.priceDeal);
            text_description= itemView.findViewById(R.id.descriptionDeal);
            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal) {
            text_title.setText(deal.getTitle());
            text_price.setText(deal.getPrice()+"FCFA");
            text_description.setText(deal.getDescription());
        }


        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            Log.d("deal position", "onLongClick: "+String.valueOf(position));
            TravelDeal selecteddeal=deals.get(position);
            Intent intent=new Intent(view.getContext(),InsertDealActivity.class);
            intent.putExtra("Deal",selecteddeal);
            view.getContext().startActivity(intent);
        }
    }

}

