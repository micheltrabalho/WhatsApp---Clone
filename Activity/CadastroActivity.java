package br.com.michel.android.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.Base64Custom;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private Button bt;
    private EditText nome, senha, email;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        bt = findViewById(R.id.buttonCadastrar);
        nome = findViewById(R.id.editTextNome);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextSenha);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtNome = nome.getText().toString();
                String txtEmail = email.getText().toString();
                String txtSENHA = senha.getText().toString();

                if (!txtNome.isEmpty()){
                    if (!txtEmail.isEmpty()){
                        if (!txtSENHA.isEmpty()){

                            usuario = new Usuario();
                            usuario.setNome(txtNome);
                            usuario.setEmail(txtEmail);
                            usuario.setSenha(txtSENHA);

                            cadastrarUsuario();

                        }
                        else {
                            Toast.makeText(CadastroActivity.this, "Preencha a Senha", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cadastrarUsuario() {

        auth = ConfigFirebase.getFirebaseAutentication();
        auth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String idUser = Base64Custom.configBase64(usuario.getEmail());

                    usuario.setIdUsuario(idUser);
                    usuario.save();

                    UsuarioFirebae.atualizarNomeUsuario(usuario.getNome());

                    finish();
                }
                else {

                    String excecao = "";// foi criada uma strinf vazia
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e ){
                        excecao = "Digite um email valido";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Essa conta ja foi cadastrada";
                    } catch (Exception e){
                        excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
