package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gsu.csc.crud.R;
import com.gsu.csc.crud.details;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import models.PetModel;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>{

    private Context context;
    private List<PetModel> petModel;

    public PetAdapter(Context context, List<PetModel> petModel) {
        this.context = context;
        this.petModel = petModel;
    }

    @NonNull
    @Override
    public PetAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);

        return new PetAdapter.PetViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.PetViewHolder petViewHolder, int i) {
        PetModel item = petModel.get(i);

        petViewHolder.txtName.setText(item.getName());
        petViewHolder.txtBreed.setText(item.getBreed());
    }

    @Override
    public int getItemCount() {
        return petModel.size();
    }

    public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName;
        private TextView txtBreed;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            txtName = itemView.findViewById(R.id.txtName);
            txtBreed = itemView.findViewById(R.id.breed);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            PetModel currentItem = petModel.get(position);

            Intent intent = new Intent(context, details.class);

            intent.putExtra("id", currentItem.getId());
            intent.putExtra("name", currentItem.getName());
            intent.putExtra("sex", currentItem.getSex());
            intent.putExtra("dob", new SimpleDateFormat("MMM dd, yyyy").format(currentItem.getDob()));
            intent.putExtra("breed", currentItem.getBreed());
            intent.putExtra("owner_id", currentItem.getOwner_id());
            intent.putExtra("type", currentItem.getType());


            intent.putExtra("action", "change");
            intent.putExtra("position", position);


            ((Activity) context).startActivityForResult(intent, 1);


        }
    }
}
