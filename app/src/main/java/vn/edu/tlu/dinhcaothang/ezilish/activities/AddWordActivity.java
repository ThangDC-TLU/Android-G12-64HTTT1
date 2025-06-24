package vn.edu.tlu.dinhcaothang.ezilish.activities;

import vn.edu.tlu.dinhcaothang.ezilish.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

import vn.edu.tlu.dinhcaothang.ezilish.models.Word;
import vn.edu.tlu.dinhcaothang.ezilish.utils.FreeDictionaryApi;
import vn.edu.tlu.dinhcaothang.ezilish.utils.FreeDictionaryResponse;

public class AddWordActivity extends AppCompatActivity {
    private EditText etWord, etPhonetic, etExplanation, etExample, etMeaning;
    private Button btnAddWord;
    private ImageView btnBack;

    private String topicId;

    private FreeDictionaryApi freeDictionaryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        etWord = findViewById(R.id.etWord);
        etPhonetic = findViewById(R.id.etPhonetic);
        etExplanation = findViewById(R.id.etExplanation);
        etExample = findViewById(R.id.etExample);
        etMeaning = findViewById(R.id.etMeaning);
        btnAddWord = findViewById(R.id.btnAddWord);
        btnBack = findViewById(R.id.btnBack);

        topicId = getIntent().getStringExtra("topicId");

        // Khởi tạo Retrofit & API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        freeDictionaryApi = retrofit.create(FreeDictionaryApi.class);

        btnBack.setOnClickListener(v -> onBackPressed());

        etWord.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString().trim();
                if (word.isEmpty()) {
                    etPhonetic.setText("");
                    etPhonetic.setError(null);
                    return;
                }
                getPhoneticWithFreeDictionary(word);
            }
        });

        btnAddWord.setOnClickListener(v -> addWord());
    }

    private void getPhoneticWithFreeDictionary(String word) {
        if (!word.matches("^[a-zA-Z]+$")) {
            etPhonetic.setText("");
            etPhonetic.setError("Invalid word!");
            Toast.makeText(this, "Từ không hợp lệ, vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }
        freeDictionaryApi.getWord(word.toLowerCase()).enqueue(new Callback<List<FreeDictionaryResponse>>() {
            @Override
            public void onResponse(Call<List<FreeDictionaryResponse>> call, Response<List<FreeDictionaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<FreeDictionaryResponse.Phonetic> phonetics = response.body().get(0).phonetics;
                    String ipa = "";
                    if (phonetics != null) {
                        for (FreeDictionaryResponse.Phonetic p : phonetics) {
                            if (p.text != null && !p.text.isEmpty()) {
                                ipa = p.text;
                                break;
                            }
                        }
                    }
                    if (!ipa.isEmpty()) {
                        etPhonetic.setText(ipa);
                        etPhonetic.setError(null);
                    } else {
                        etPhonetic.setText("");
                        etPhonetic.setError("No phonetic found!");
                        Toast.makeText(AddWordActivity.this, "Không tìm thấy phiên âm!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    etPhonetic.setText("");
                    etPhonetic.setError("Not a valid English word!");
                    Toast.makeText(AddWordActivity.this, "Không phải từ tiếng Anh hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FreeDictionaryResponse>> call, Throwable t) {
                etPhonetic.setText("");
                etPhonetic.setError("Error connecting API!");
                Toast.makeText(AddWordActivity.this, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addWord() {
        String word = etWord.getText().toString().trim();
        String phonetic = etPhonetic.getText().toString().trim();
        String explanation = etExplanation.getText().toString().trim();
        String example = etExample.getText().toString().trim();
        String meaning = etMeaning.getText().toString().trim();

        if (word.isEmpty() || phonetic.isEmpty() || explanation.isEmpty() || example.isEmpty() || meaning.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference wordsRef = FirebaseDatabase.getInstance().getReference("words");
        String id = wordsRef.push().getKey();

        boolean learned = false;

        Word w = new Word(id, word, phonetic, meaning, example, explanation, topicId, learned);

        wordsRef.child(id).setValue(w, (error, ref) -> {
            if (error == null) {
                Toast.makeText(this, "Word added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add word!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}