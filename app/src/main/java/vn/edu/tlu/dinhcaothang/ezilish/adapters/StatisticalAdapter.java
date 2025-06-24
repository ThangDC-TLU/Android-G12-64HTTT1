package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.models.Statistical;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.StatisticalViewHolder> {
    private List<Statistical> statsList;

    public StatisticalAdapter(List<Statistical> statsList) {
        this.statsList = statsList;
    }

    @NonNull
    @Override
    public StatisticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_statistical, parent, false);
        return new StatisticalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticalViewHolder holder, int position) {
        Statistical stats = statsList.get(position);
        holder.tvTopicName.setText(stats.getTopicName());
        holder.tvWords.setText(stats.getWordsLearned() + " / " + stats.getTotalWords() + " từ");
        holder.progressWords.setMax(stats.getTotalWords());
        holder.progressWords.setProgress(stats.getWordsLearned());
        holder.tvScore.setText("Score: " + stats.getScore());
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public static class StatisticalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvWords, tvScore;
        ProgressBar progressWords;

        public StatisticalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            tvWords = itemView.findViewById(R.id.tvWords);
            progressWords = itemView.findViewById(R.id.progressWords);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}
