package br.com.michel.android.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.model.Usuario;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText email, senha;
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAutentication();
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.button);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextSenha);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtemail = email.getText().toString();
                String txtsenha = senha.getText().toString();

                if (!txtemail.isEmpty()){
                    if (!txtsenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail(txtemail);
                        usuario.setSenha(txtsenha);

                        validarLogin();

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Preencha o campo senha", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Preencha o campo E-mail", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public  void validarLogin() {

        auth = ConfigFirebase.getFirebaseAutentication();
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    abrirNovaActivity();
                }
                else {

                    String exception = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Email e senha nao correspondem ao usuario cadastrado";
                    } catch (FirebaseAuthInvalidUserException e){
                        exception = "Usuario nao esta cadastrado";
                    } catch (Exception e){
                        exception = "Erro ao "+ e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, exception, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // metodo para manter sempre manter o usuario logado, so ira deslogar caso ele saia no app
    @Override
    protected void onStart() {

        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            abrirNovaActivity();
        }
    }

    public void abrirNovaActivity(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }

    public void cadastroActivity(View view) {

        startActivity(new Intent(this, CadastroActivity.class));
        finish();
    }
}
