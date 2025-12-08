package com.example.workio.ui.main.more;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.workio.R;
import com.example.workio.data.model.Violation;

import java.util.List;

public class ViolationHistoryAdapter extends RecyclerView.Adapter<ViolationHistoryAdapter.ViewHolder> {

    private final List<Violation> list;

    public ViolationHistoryAdapter(List<Violation> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_violation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Violation item = list.get(position);
        String rawDate = item.getViolationDate();  // "2025-12-06T00:00:00.000Z"
        String onlyDate = rawDate.split("T")[0];        // "2025-12-06"



        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.date.setText(onlyDate);
        holder.Penalty.setText("-" + item.getPenaltyAmount() + " đ");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, Penalty;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.tvDate);
            Penalty = itemView.findViewById(R.id.tvPenalty);
        }
    }
}
