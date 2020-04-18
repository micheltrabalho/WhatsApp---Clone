package br.com.michel.android.whatsapp.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import br.com.michel.android.whatsapp.congfirebase.ConfigFirebase;
import br.com.michel.android.whatsapp.helper.Base64Custom;

public class Grupo implements Serializable {

    private String id, nome, foto;
    private List<Usuario> membros;

    public Grupo() {
        DatabaseReference databaseReference = ConfigFirebase.getFirebase();
        DatabaseReference reference = databaseReference.child("usuariosWhatsApp").child("grupos");

        String idFirebase = reference.push().getKey();
        setId(idFirebase);

    }

    public void salvar(){
        DatabaseReference databaseReference = ConfigFirebase.getFirebase();
        DatabaseReference grupoRef = databaseReference.child("usuariosWhatsApp").child("grupo");
        grupoRef.child(getId()).setValue(this);

        for (Usuario membro: getMembros()){

            String idDestinatario = getId();
            String idremetente = Base64Custom.configBase64(membro.getEmail());

            COnversa cOnversa = new COnversa();
            cOnversa.setIdRemetente(idremetente);
            cOnversa.setIdDestinatario(idDestinatario);
            cOnversa.setUltimamsg("");
            cOnversa.setIsgrupo(true);
            cOnversa.setGrupo(this);

            cOnversa.salvar();

        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
