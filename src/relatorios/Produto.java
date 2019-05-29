package relatorios;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

class Produto implements Entidade{

    private   int    idProduto;
    protected String nomeProduto;
    protected String descricao;
    protected float  preco;
    protected String marca;
    protected String origem; 
    protected int    idCategoria;
    private   int    quantVendidos;

    //Construtor vazio
    public Produto(){
        this.idProduto    = -1;
        this.nomeProduto  = "";
        this.descricao    = "";
        this.preco        = 0;
        this.marca        = "";
        this.origem       = "";
        this.idCategoria  = -1;
        this.quantVendidos = 0;
    }//end Produto 

    //construtor com parametros
    public Produto(String nomeProduto, String descricao, float preco, String marca, String origem, int idCategoria){
        this.nomeProduto = nomeProduto;
        this.descricao    = descricao;
        this.preco        = preco;
        this.marca        = marca;
        this.origem       = origem;
        this.idCategoria = idCategoria;
        this.quantVendidos = 0;
    }//end Produto

    public void setID(int id){
        this.idProduto = id;
    }//end setId

    public int getID(){
        return this.idProduto;
    }//end getId

    public void addQuantVendidos(int quantVendidos){
        this.quantVendidos += quantVendidos;
    }

    public void zeraQuantVendidos(){
        this.quantVendidos = 0;
    }

    public int getQuantVendidos(){
        return this.quantVendidos;
    }

    //Retorna um array de bytes com os bytes para escrever no arquivo
    public byte[] toByteArray() throws Exception{
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idProduto);
        saida.writeUTF(this.nomeProduto);
        saida.writeUTF(this.descricao);
        saida.writeFloat(this.preco);
        saida.writeUTF(this.marca);
        saida.writeUTF(this.origem);
        saida.writeInt(this.idCategoria);
        saida.writeInt(this.quantVendidos);

        return dados.toByteArray();
    }//end toByteArray

    //Coloca nos atributos os bytes lidos doa arquivo
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream dados   = new ByteArrayInputStream(b);
        DataInputStream      entrada = new DataInputStream(dados);

        this.idProduto    = entrada.readInt();
        this.nomeProduto = entrada.readUTF();
        this.descricao    = entrada.readUTF();
        this.preco        = entrada.readFloat();
        this.marca        = entrada.readUTF();
        this.origem       = entrada.readUTF();
        this.idCategoria  = entrada.readInt();
        this.quantVendidos = entrada.readInt();

        entrada.close();
    }//end fromByteArray

    public String toString(){
        return "Id: "         + this.idProduto    + 
            "\nNome: "        + this.nomeProduto  + 
            "\nDescricao: "   + this.descricao    + 
            "\nPreco: "       + this.preco        + 
            "\nMarca: "       + this.marca        + 
            "\nOrigem: "      + this.origem       +
            "\nIdCategoria: " + this.idCategoria;
    }//end toString
}//end Produto
