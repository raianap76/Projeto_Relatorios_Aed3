package relatorios;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

class Compra implements Entidade{
    private int idCompra;
    protected int idCliente;
    protected Date dataCompra;
    private float valorCompra;

    public Compra(){
        this.idCompra    = -1;
        this.idCliente   = -1;
        this.dataCompra  = null;
        this.valorCompra = 0;
      
    }

    public Compra(int idCliente, Date data){
        this.idCompra    = -1;
        this.idCliente   = idCliente;
        this.dataCompra  = data;
        this.valorCompra = 0;
    }

    public int getID(){
        return this.idCompra;
    }

    public void setID(int id){
        this.idCompra = id;
    }

    //Retorna um array de bytes com os bytes para escrever no arquivo
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idCompra);
        saida.writeInt(this.idCliente);
        saida.writeLong(this.dataCompra.getTime());//converter data em milisegundos para formato de data padrão
        saida.writeFloat(this.valorCompra);
        return dados.toByteArray();
    }

    //Coloca nos atributos os bytes lidos do arquivo
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream dados = new ByteArrayInputStream(b);
        DataInputStream entrada = new DataInputStream(dados);

        this.idCompra = entrada.readInt();
        this.idCliente = entrada.readInt();
        this.dataCompra = new Date(entrada.readLong());
        this.valorCompra = entrada.readFloat();
        entrada.close();
    }

    public String toString(){
        return "Id Compra: "          + this.idCompra + 
            "\nId Cliente: "      + this.idCliente + 
            "\nData da compra: "  + this.dataCompra.toString() + 
            "\nValor da compra: " + this.valorCompra;
    }
}
