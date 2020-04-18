package br.com.michel.android.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import br.com.michel.android.whatsapp.R;
import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.Permissao;
import br.com.michel.android.whatsapp.helper.UsuarioFirebae;
import br.com.michel.android.whatsapp.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ConfigActivity extends AppCompatActivity {

    //permissao para o usuario, antes e necessario ir no manifest e colocar  <uses-permission
    //        android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static  final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageView;
    private StorageReference storageReference;
    private String idUser;
    private ImageView imageButton;
    private EditText editText;
    private Usuario usuarioLogad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        idUser = UsuarioFirebae.getIdentificadordeUsuario();//essa classe foi criada, e o metodo tambem
        storageReference = ConfigFirebase.getFirebaseStorege();
        imageButton = findViewById(R.id.imageViewConfgNome);

        usuarioLogad = UsuarioFirebae.getDadosUser();




        // alterar nome
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                boolean retorno = UsuarioFirebae.atualizarNomeUsuario(name);

                if ((retorno)){

                    usuarioLogad.setNome(name);
                    usuarioLogad.atualizar();

                    Toast.makeText(ConfigActivity.this, "Nome alterado com sucesso.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //criando toobar

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configuracoes");
        setSupportActionBar(toolbar);

        //metodo para aparecer o botao voltar na actionbar, porem para que ele tenha funcao é necessario
        // ir no manifest-localizar essa activity-android:parentActivityName="aqui sera colocado para qual activity deseja voltar"

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //solicitacao de permissao para o usuario, antes e necessario ir no manifest e colocar  <uses-permission
        //        android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        // tambem precisa criar uma classe no helper, nesse caso o nome dela foi Permissao.java, la estao as configuracoes na permissao

        Permissao.validarPermissao(permissoes, this, 1);

         //---------------------------------------------------------------------

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // dando funcao aos botoes na imagem
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        circleImageView = findViewById(R.id.circle_imageChat);
        editText = findViewById(R.id.editTextNomeUsuario);

        // dando funcao ao botao camera na imagem
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });

        // dando funcao ao botao camera na imagem
        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        // recuperando dados do usurario no firebase

        FirebaseUser usuario = UsuarioFirebae.getUserAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null){
            Glide.with(ConfigActivity.this).load(url).into(circleImageView);
        }
        else {
            circleImageView.setImageResource(R.drawable.padrao);
        }

        editText.setText(usuario.getDisplayName());

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
                    case SELECAO_GALERIA:
                        Uri uri = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        break;
                }
                if (img != null){

                    circleImageView.setImageBitmap(img);

                    //salvando a imagem no firebase

                    final StorageReference imgRef = storageReference.child("imagens").child("perfil").child(idUser + ".jpeg");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    byte[] dadosImg = byteArrayOutputStream.toByteArray();

                    UploadTask uploadTask = imgRef.putBytes(dadosImg);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigActivity.this, "Sucesso ao fazer o upload da imagem", Toast.LENGTH_LONG).show();
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

           Toast.makeText(ConfigActivity.this, "Sua foto foi alterada", Toast.LENGTH_LONG).show();
       }
    }

    //---------------------------------------------------------------------


    // faz parte da solicitacao para o usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int prmissaresultado : grantResults){
            if (prmissaresultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }
    // faz parte da solicitacao para o usuario
    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissao Negada");
        builder.setMessage("Para utilizar o app e necessario aceitar as permissoes");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
}
