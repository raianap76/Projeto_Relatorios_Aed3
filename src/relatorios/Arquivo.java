
package relatorios;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.io.RandomAccessFile;

public class Arquivo<G extends Entidade>{

    protected RandomAccessFile raf;
    protected Indice indice;
    protected String nomeArquivo;
    protected Constructor<G> construtor;

    public Arquivo(Constructor<G> c, String nomeArquivo, String relacionamentoN_N)throws Exception{
        RelacionamentoN_N pf = new RelacionamentoN_N(relacionamentoN_N); 
        this.nomeArquivo = nomeArquivo;
        this.construtor   = c;
        this.raf          = new RandomAccessFile(pf.addFile(nomeArquivo + ".db"), "rw");
        this.indice       = new Indice(20, pf.addFile(nomeArquivo + ".idx"));
        if(raf.length() < 4){
            raf.writeInt(0);
        }
    }

    //metodo para fechar o arquivo
    public void close() throws Exception {
        raf.close();
    }//end close

    //metodo para pgear o ultimoID do aquivo(primeiros 4 bytes lidos)
    public int ultimoID() throws Exception {
        raf.seek(0);
        return raf.readInt();
    }//end utimoID

    //metodo para inserir o objeto no arquivo e adiciona-lo no indice
    public int inserir(G objeto) throws Exception{
        int ultimoID;
        ultimoID = this.ultimoID();
        raf.seek(0);
        objeto.setID(ultimoID+1); 

        indice.inserir(ultimoID, raf.length());
        //escrever o array de bytes do objeto no arquivo
        raf.seek(raf.length());
        byte[] b = objeto.toByteArray();
        raf.writeByte(' ');
        raf.writeShort(b.length);
        raf.write(b);
        //atualizar o ultimoid
        raf.seek(0);
        raf.writeInt(objeto.getID());
        raf.seek(0);
        ultimoID = raf.readInt();
        return ultimoID;
    }//end inserir 

    //metodo para pesquisar o objeto, referente ao id lido, no arquivo, usando a pesquisa feita no indice primeiro
    public G pesquisar(int idqr) throws Exception{
        raf.seek(0);
        G objeto = null;
        int i = raf.readInt();
        if(i >= idqr){
            long pos = indice.buscar(idqr);
            if (pos != -1){
                raf.seek(pos);
                byte lapide = raf.readByte();
                int tamanho = raf.readShort();
                byte[] b = new byte[tamanho];
                if(lapide == ' '){
                    objeto = construtor.newInstance();
                    raf.read(b);
                    objeto.fromByteArray(b);
                }//end if  
            }//end if
        }//end if
        return objeto;
    }//end pesquisar

    //metodo para retornar uma lista com todos os objetos no arquivo
    public ArrayList<G> toList() throws Exception{
        G objeto;
        ArrayList<G> lista = new ArrayList<G>();
        short tamanho;
        byte[] b;
        byte lapide;

        raf.seek(4);
        while(raf.getFilePointer() < raf.length()) {
            lapide = raf.readByte();
            tamanho = raf.readShort();
            b = new byte[tamanho];
            raf.read(b);
            objeto = construtor.newInstance();
            objeto.fromByteArray(b);
            if (lapide == ' ') lista.add(objeto);
        }//end while
        return lista;
    }//end toList

    public boolean remover(int idqr) throws Exception{
        return this.remover(idqr, true);
    }

    //metodo para remover o objeto referente ao id lido
    private boolean remover(int idqr, boolean removerIndice) throws Exception{
        raf.seek(0);
        int i = raf.readInt();
        boolean result = false;
        if(i >= idqr){
            long pos = indice.buscar(idqr);
            if (pos != -1){
                raf.seek(pos);
                byte lapide = raf.readByte();
                if(lapide == ' '){
                    raf.seek(pos);
                    raf.writeByte('*');
                    if(removerIndice){
                        indice.excluir(idqr);
                    }
                    result = true;
                }//end if

            }//end if
        }//end if
        return result;
    }//end remover

    //metodo para alterar um objeto (exclui o antigo e adiciona um novo com o mesmo id)
    public boolean alterar(int idqr, G objeto) throws Exception{
        boolean removeu;
        boolean result = false;
        raf.seek(0);
        int i = raf.readInt();
        if(i >= idqr){
            removeu = this.remover(idqr - 1, false);
            if(removeu){
                result = this.inserirAlterado(objeto, idqr);
            }//end if
        }//end if
        return result;
    }//end alterar

    //metodo para inserir sem alterar o id, modificando no indice apenas a posicao do objeto no arquivo
    public boolean inserirAlterado(G objeto, int idqr) throws Exception{
        objeto.setID(idqr);
        indice.atualizar(idqr - 1, raf.length());
        raf.seek(raf.length());
        byte[] b = objeto.toByteArray();
        raf.writeByte(' ');
        raf.writeShort(b.length);
        raf.write(b);
        return true;
    }//end inserirAlterado

}//end Arquivo
