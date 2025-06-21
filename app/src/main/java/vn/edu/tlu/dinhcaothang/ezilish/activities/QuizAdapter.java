package vn.edu.tlu.dinhcaothang.ezilish.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import vn.edu.tlu.dinhcaothang.ezilish.activities.QuizQuestion;
import vn.edu.tlu.dinhcaothang.ezilish.R;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizQuestion> quizList;
    private int[] userAnswers; // 0:A, 1:B, 2:C, 3:D, -1:chưa chọn

    public QuizAdapter(List<QuizQuestion> quizList) {
        this.quizList = quizList;
        userAnswers = new int[quizList.size()];
        for (int i = 0; i < userAnswers.length; i++) userAnswers[i] = -1;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_question_item, parent, false);
        return new QuizViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizQuestion question = quizList.get(position);
        holder.tvQuestion.setText("Question " + (position + 1) + ": " + question.question);

        holder.rgOptions.removeAllViews();
        for (int i = 0; i < question.options.size(); i++) {
            RadioButton radio = new RadioButton(holder.itemView.getContext());
            radio.setText(question.options.get(i));
            radio.setTextColor(0xFF222222);
            radio.setTextSize(15);
            radio.setId(i);
            holder.rgOptions.addView(radio);
        }
        holder.rgOptions.clearCheck();
        if (userAnswers[position] != -1) {
            holder.rgOptions.check(userAnswers[position]);
        }
        holder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            userAnswers[position] = checkedId;
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public int[] getUserAnswers() {
        return userAnswers;
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        RadioGroup rgOptions;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            rgOptions = itemView.findViewById(R.id.rgOptions);
        }
    }
}