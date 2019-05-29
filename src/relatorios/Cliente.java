package relatorios;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Date;

class Cliente implements Entidade
{
    private int idCliente;
    protected String nomeCliente;
    protected String email;
    protected String cpf;
    private float despesaFeita;
    protected Date ultimoLogin;

    public Cliente(){
        this.idCliente = -1;
        this.nomeCliente = "";
        this.email = "";
        this.cpf = "";
        this.despesaFeita = 0;
        this.ultimoLogin = null;
    }


    public Cliente(String nomeCliente, String email, String cpf){
        this.nomeCliente = nomeCliente;
        this.email = email;
        this.cpf = cpf;
        this.despesaFeita = 0;
        this.ultimoLogin = new Date();
    }

    public void adicionarGasto(float gasto){
        this.despesaFeita += gasto;
    }

    public float getTotalGasto(){
        return this.despesaFeita;
    }

    public void zerarGasto(){
        this.despesaFeita = 0;
    }


    public void setID(int id){
        this.idCliente = id;
    }

    public int getID(){
        return this.idCliente;
    }

    //Retorna um array de bytes com os bytes para escrever no arquivo
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream dados = new ByteArrayOutputStream();
        DataOutputStream saida = new DataOutputStream(dados);

        saida.writeInt(this.idCliente);
        saida.writeUTF(this.nomeCliente);
        saida.writeUTF(this.email);
        saida.writeUTF(this.cpf);
        saida.writeFloat(this.despesaFeita);
        saida.writeLong(this.ultimoLogin.getTime());
        return dados.toByteArray();
    }

    //Coloca nos atributos os bytes lidos do arquivo
    public void fromByteArray(byte[] b) throws Exception {
        ByteArrayInputStream dados = new ByteArrayInputStream(b);
        DataInputStream entrada = new DataInputStream(dados);

        this.idCliente = entrada.readInt();
        this.nomeCliente = entrada.readUTF();
        this.email = entrada.readUTF();
        this.cpf = entrada.readUTF();
        this.despesaFeita = entrada.readFloat();
        this.ultimoLogin = new Date(entrada.readLong());
        entrada.close();
    }

    public String toString(){
        return "Id:"    + this.idCliente +
            "\nNome: "  + this.nomeCliente +
            "\nEmail: " + this.email +
            "\nCPF: "   + this.cpf;
    }
}//End Cliente
