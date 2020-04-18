package br.com.michel.android.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.adapter.GrupoSelecionadoAdapter;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.RecyclerItemClickListener;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Grupo;
import br.com.michel.android.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private List<Usuario> listMembrosSlecionados = new ArrayList<>();
    private TextView textViewtotal;
    private CircleImageView circleImageView;
    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private RecyclerView recyclerMembrosSelecionados;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private Grupo grupo;
    private Usuario usuarioLogad;
    private FloatingActionButton floatingActionButton;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_grupo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("Defina um nome");
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);

        editText = findViewById(R.id.editTextNomeGrup);
        floatingActionButton = findViewById(R.id.fabsalvargrupo);
        usuarioLogad = UsuarioFirebae.getDadosUser();
        storageReference = ConfigFirebase.getFirebaseStorege();
        grupo = new Grupo();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewtotal = findViewById(R.id.textViewParticipantes);

        if (getIntent().getExtras() != null){
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listMembrosSlecionados.addAll(membros);
            textViewtotal.setText("Participantes: " + listMembrosSlecionados.size());
        }

        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosGrupo);

        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listMembrosSlecionados, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerMembrosSelecionados.setLayoutManager(layoutManager);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        circleImageView = findViewById(R.id.imageGrupo);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeGrupo = editText.getText().toString();
                listMembrosSlecionados.add(UsuarioFirebae.getDadosUser());

                grupo.setMembros(listMembrosSlecionados);

                grupo.setNome(nomeGrupo);
                grupo.salvar();

                Intent i = new Intent(CadastroGrupoActivity.this, ChatActivity.class);
                i.putExtra("chatGrupo", grupo);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap img = null;

            try {

                Uri uri = data.getData();
                img = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                if (img != null){

                    circleImageView.setImageBitmap(img);

                    //salvando a imagem no firebase

                    final StorageReference imgRef = storageReference.child("usuariosWhatsApp").child("imagens").child("grupos").child("perfil").child(grupo.getId() + ".jpg");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byte[] dadosImg = byteArrayOutputStream.toByteArray();

                    UploadTask uploadTask = imgRef.putBytes(dadosImg);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroGrupoActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroGrupoActivity.this, "Sucesso ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri url = uri;
                                    atualizarFoto(url);
                                    
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFoto(Uri url){

        boolean retorno =  UsuarioFirebae.atualizarFotoUsuario(url);

        if (retorno){
            usuarioLogad.setFoto(url.toString());
            usuarioLogad.atualizar();

            Toast.makeText(CadastroGrupoActivity.this, "Sua foto foi alterada", Toast.LENGTH_LONG).show();
        }
    }
}
