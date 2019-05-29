package relatorios;
//interface para o arquivo generico
public interface Entidade{
    public int getID();
    public void setID(int id);
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] b) throws Exception;   
}//end Entidade
