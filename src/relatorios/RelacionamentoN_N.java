/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package relatorios;
import java.io.File;

public class RelacionamentoN_N
{

    protected String relacionamentoN_N;

    public RelacionamentoN_N(String relacionamentoN_N){
        this.relacionamentoN_N = relacionamentoN_N;
    }

    public File addFile(String filename){
        File file = null;
        String os = System.getProperty("os.name");
        String folder = System.getProperty("user.home");
        if(os.contains("Windows")){
            file = new File(folder + "/AppData/Local/" + relacionamentoN_N+ "/");
            if (!file.exists()){
                if(file.mkdirs()){
                    System.out.println("O diretorio " + file.getAbsolutePath() + " foi criado para armazenar os dados!"); 
                    file = new File(folder + "/AppData/Local/" + relacionamentoN_N + "/" + filename);
                }
                else{
                    System.out.println("Erro ao criar pasta para os dados do programa!\nUsando fallback!");
                    file = new File(filename);
                }
            }
            else{ 
                file = new File(folder + "/AppData/Local/" + relacionamentoN_N + "/" + filename);
            }
        }
        else{
            file = new File(folder + "/.local/share/" + relacionamentoN_N + "/");
            if (!file.exists()){
                if(file.mkdirs()){
                    System.out.println("O diretorio " + file.getAbsolutePath() + " foi criado para armazenar os dados!"); 
                    file = new File(folder + "/.local/share/" + relacionamentoN_N + "/" + filename);
                }
                else{ 
                    System.out.println("Erro ao criar pasta para os dados do programa!\nUsando fallback!"); 
                    file = new File(filename);
                }
            }
            else{ 
                file = new File(folder + "/.local/share/" + relacionamentoN_N + "/" + filename);
            }
        }
        return file;
    }
}//Fim classe ProgramFile 

