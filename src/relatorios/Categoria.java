package relatorios;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream; 

class Categoria implements Entidade{

    private int    idCategoria;
    protected String nome;

    //construtor vazio
    public Categoria(){
        this.idCategoria = -1;
        this.nome        = "";
    }//end Categoria

    //construtor com parametros
    public Categoria(String nome){
        this.idCategoria = -1;
        this.nome        = nome;
    }//end Categoria

    public void setID(int id){
        this.idCategoria = id;
    }//end setID

    public int getID(){
        return this.idCategoria;
    }//end getID

    //Retorna um array de bytes com os bytes para escrever no arquivo
    public byte[] toByteArray() throws Exception{
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idCategoria);
        saida.writeUTF(this.nome);

        return dados.toByteArray();
    }//end toByteArray

    //Coloca nos atributos os bytes lidos doa arquivo
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream dados   = new ByteArrayInputStream(b);
        DataInputStream      entrada = new DataInputStream(dados);

        this.idCategoria  = entrada.readInt();
        this.nome         = entrada.readUTF();

        entrada.close();
    }//end fromByteArray

    public String toString(){
        return "ID: " + this.idCategoria + " Nome: " + this.nome;
    }//end toString

}//end Categoria
