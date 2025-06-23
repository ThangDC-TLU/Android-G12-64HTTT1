package vn.edu.tlu.dinhcaothang.ezilish.adapters;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import vn.edu.tlu.dinhcaothang.ezilish.utils.QuizQuestion;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<QuizQuestion> quizList;

    // Lưu lựa chọn của người dùng cho từng câu hỏi
    private String[] userChoices;

    public QuizAdapter(List<QuizQuestion> quizList) {
        this.quizList = quizList;
        this.userChoices = new String[quizList.size()];
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_question_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizQuestion question = quizList.get(position);
        holder.tvQuestion.setText("Question " + (position + 1) + ": " + question.question);

        // Set text cho từng đáp án
        holder.rbA.setText(question.options.get(0));
        holder.rbB.setText(question.options.get(1));
        holder.rbC.setText(question.options.get(2));
        holder.rbD.setText(question.options.get(3));

        // Reset màu về mặc định
        holder.rbA.setTextColor(Color.BLACK);
        holder.rbB.setTextColor(Color.BLACK);
        holder.rbC.setTextColor(Color.BLACK);
        holder.rbD.setTextColor(Color.BLACK);

        // Set lại trạng thái checked
        holder.radioGroup.setOnCheckedChangeListener(null);
        if ("A".equals(userChoices[position])) holder.rbA.setChecked(true);
        else if ("B".equals(userChoices[position])) holder.rbB.setChecked(true);
        else if ("C".equals(userChoices[position])) holder.rbC.setChecked(true);
        else if ("D".equals(userChoices[position])) holder.rbD.setChecked(true);
        else holder.radioGroup.clearCheck();

        // Hiển thị màu đúng/sai sau khi đã chọn
        if (userChoices[position] != null) {
            // Hiển thị xanh cho đáp án đúng
            RadioButton rbCorrect = getRadioButtonByAnswer(holder, question.answer);
            rbCorrect.setTextColor(Color.parseColor("#388E3C")); // Xanh lá

            // Nếu chọn sai thì đáp án chọn chuyển đỏ
            if (!userChoices[position].equals(question.answer)) {
                RadioButton rbUser = getRadioButtonByAnswer(holder, userChoices[position]);
                rbUser.setTextColor(Color.RED);
            }

            // Không cho chọn lại
            holder.rbA.setEnabled(false);
            holder.rbB.setEnabled(false);
            holder.rbC.setEnabled(false);
            holder.rbD.setEnabled(false);
        } else {
            // Cho chọn nếu chưa chọn
            holder.rbA.setEnabled(true);
            holder.rbB.setEnabled(true);
            holder.rbC.setEnabled(true);
            holder.rbD.setEnabled(true);
        }

        // Xử lý chọn đáp án
        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (userChoices[position] != null) return; // đã chọn rồi, không cho chọn lại

            String selected = null;
            if (checkedId == holder.rbA.getId()) selected = "A";
            else if (checkedId == holder.rbB.getId()) selected = "B";
            else if (checkedId == holder.rbC.getId()) selected = "C";
            else if (checkedId == holder.rbD.getId()) selected = "D";

            userChoices[position] = selected;
            notifyItemChanged(position);
        });
    }

    private RadioButton getRadioButtonByAnswer(QuizViewHolder holder, String answer) {
        switch (answer) {
            case "A": return holder.rbA;
            case "B": return holder.rbB;
            case "C": return holder.rbC;
            case "D": return holder.rbD;
            default: return holder.rbA;
        }
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion;
        RadioGroup radioGroup;
        RadioButton rbA, rbB, rbC, rbD;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            rbA = itemView.findViewById(R.id.rbA);
            rbB = itemView.findViewById(R.id.rbB);
            rbC = itemView.findViewById(R.id.rbC);
            rbD = itemView.findViewById(R.id.rbD);
        }
    }
}