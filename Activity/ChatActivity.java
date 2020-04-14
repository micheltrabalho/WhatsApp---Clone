package br.com.michel.android.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.adapter.MensagemAdapte;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.Base64Custom;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.COnversa;
import br.com.michel.android.whatsapp.model.Mensagem;
import br.com.michel.android.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewnome;
    private CircleImageView circleImageView;
    private Usuario usuariodesd;
    private EditText editTextMsg;
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private RecyclerView recyclerViewmsg;
    private MensagemAdapte adapter;
    private List<Mensagem> mensagens = new ArrayList<>();
    private DatabaseReference databaseReference;
    private DatabaseReference msgREF;
    private ChildEventListener childEventListenerhildmensagens;
    private ImageButton btnMSG;
    private StorageReference storageReference;
    private static final int SELECAO_CAMERA = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewnome = findViewById(R.id.txtNomeChat);
        circleImageView = findViewById(R.id.circle_imageChat);
        editTextMsg = findViewById(R.id.editTextMsg);
        recyclerViewmsg = findViewById(R.id.recyclerMSG);
        btnMSG = findViewById(R.id.imageButtonMSG);

        //recuperar dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebae.getIdentificadordeUsuario();


        //recuperando os dados do usuario destinatario
        //esse trecho serve para mostrar a imagem e o nome do destinatario na toolbar

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            usuariodesd = (Usuario) bundle.getSerializable("chatContato");
            textViewnome.setText(usuariodesd.getNome());

            String foto = usuariodesd.getFoto();
            if (foto != null){
                Uri url = Uri.parse(usuariodesd.getFoto());
                Glide.with(ChatActivity.this).load(url).into(circleImageView);
            }else {
                circleImageView.setImageResource(R.drawable.padrao);
            }

            //recuperar dados usuario destinatario
            idUsuarioDestinatario = Base64Custom.configBase64(usuariodesd.getEmail());
        }

        //configuracao do adapter do recyclerview
        adapter = new MensagemAdapte(mensagens, getApplicationContext());

        //configurando o recyclerview - nesse recyclerview ira aparecer as mensagens
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewmsg.setLayoutManager(layoutManager);
        recyclerViewmsg.setHasFixedSize(true);
        recyclerViewmsg.setAdapter(adapter);

        databaseReference = ConfigFirebase.getFirebase();
        storageReference = ConfigFirebase.getFirebaseStorege();
        msgREF = databaseReference.child("usuariosWhatsApp").child("mensagens").child(idUsuarioRemetente).child(idUsuarioDestinatario);

        btnMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap img = null;

            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        img =(Bitmap) data.getExtras().get("data");
                        break;
                }
                if (img != null){

                    //salvando a imagem no firebase

                    String dd = UUID.randomUUID().toString();

                    StorageReference imgRef = storageReference.child("usuariosWhatsApp").child("conversas").child(idUsuarioRemetente + ".jpeg").child(dd);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byte[] dadosImg = byteArrayOutputStream.toByteArray();

                    UploadTask uploadTask = imgRef.putBytes(dadosImg);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                            String url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                            Mensagem m = new Mensagem();
                            m.setIdUsuario(idUsuarioRemetente);
                            m.setImg(url);

                            salvandoAMsg(idUsuarioRemetente, idUsuarioDestinatario, m);
                            salvandoAMsg(idUsuarioDestinatario, idUsuarioRemetente, m);
                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //metodo para enviar msg
    public void enviarMSG (View view){

        String msg = editTextMsg.getText().toString();
        if (!msg.isEmpty()){

            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMsg(msg);

            salvandoAMsg(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
            salvandoAMsg(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

            //salvar conversa
            salvarconversa(mensagem);


        }else {
            Toast.makeText(ChatActivity.this, "Digite uma mensagem para o destinatario", Toast.LENGTH_LONG).show();
        }

        // alem do codigo digitado anteriormente sera necessario criar uma classe um model, que nesse caso o nome foi Menssagem
    }

    private void salvarconversa(Mensagem msg){

        COnversa cOnversaRemetente = new COnversa();
        cOnversaRemetente.setIdRemetente(idUsuarioRemetente);
        cOnversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        cOnversaRemetente.setUltimamsg(msg.getMsg());
        cOnversaRemetente.setUsuario(usuariodesd);

        cOnversaRemetente.salvar();
    }

    //metodo para salvar a msg
    public void salvandoAMsg(String idRemetente, String idDestinatario, Mensagem mensagem){

        DatabaseReference databaseReference = ConfigFirebase.getFirebase();
        DatabaseReference mensagemRef = databaseReference.child("usuariosWhatsApp").child("mensagens");

        mensagemRef.child(idRemetente).child(idDestinatario).push().setValue(mensagem);

        //limpar o local do texto
        editTextMsg.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperandoMSGS();
    }

    @Override
    protected void onStop() {
        super.onStop();
        msgREF.removeEventListener(childEventListenerhildmensagens);
    }

    private void recuperandoMSGS(){

        childEventListenerhildmensagens = msgREF.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
