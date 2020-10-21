package com.example.organizeclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizeclone.R;
import com.example.organizeclone.config.ConfiguracaoFirebase;
import com.example.organizeclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends Activity implements View.OnClickListener {
    private EditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        campoNome = findViewById(R.id.edt_nome);
        campoEmail = findViewById(R.id.edt_email);
        campoSenha = findViewById(R.id.edt_senha);
        Button botaoCadastrar = findViewById(R.id.btn_cadastrar);
        botaoCadastrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        valideInputs(campoNome.getText().toString(), campoEmail.getText().toString(), campoSenha.getText().toString());
    }

    private void valideInputs(String textNome, String textEmail, String textSenha) {
        if (!textNome.isEmpty()) {
            if (!textEmail.isEmpty()) {
                if (!textSenha.isEmpty()) {
                    usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);
                    cadastrarUsuario();
                } else {
                    Toast.makeText(this, "Preencha a senha ", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Preencha o email", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_LONG).show();
        }
    }

    public void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Usuário cadastrado com sucesso !", Toast.LENGTH_LONG).show();
                } else {
                    String excecao = "";
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Por favor, digite um e-mail válido!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada!";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
