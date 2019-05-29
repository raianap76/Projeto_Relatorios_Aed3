/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package relatorios;

import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

public class Relatorios{

    private static final NumberFormat tf = NumberFormat.getCurrencyInstance();
    private static final String RelacionamentoN_N = "Relacionamento N:N";
    private static Arquivo<Produto> arqProdutos;
    private static Arquivo<Categoria> arqCategorias;
    private static Arquivo<Cliente> arqClientes;
    private static Arquivo<Compra> arqCompra;
    private static Arquivo<ItemComprado> arqItemComprado;
    private static IndiceChaveComposta indice_Compra_ItemComprado;
    private static IndiceChaveComposta indice_ItemComprado_Compra;
    private static IndiceChaveComposta indice_Cliente_Produto;
    private static IndiceChaveComposta indice_Produto_Cliente;
    private static final Scanner read = new Scanner(System.in);
    private static final DateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        try {
            arqProdutos = new Arquivo<>(Produto.class.getConstructor(), "Produtos", RelacionamentoN_N);
            arqCategorias = new Arquivo<>(Categoria.class.getConstructor(), "Categorias", RelacionamentoN_N);
            arqClientes = new Arquivo<>(Cliente.class.getConstructor(), "Clientes", RelacionamentoN_N);
            arqCompra = new Arquivo<>(Compra.class.getConstructor(), "Compras", RelacionamentoN_N);
            arqItemComprado = new Arquivo<>(ItemComprado.class.getConstructor(), "ItensComprados", RelacionamentoN_N);

            RelacionamentoN_N svarq = new RelacionamentoN_N(RelacionamentoN_N);
            indice_Compra_ItemComprado = new IndiceChaveComposta(20, svarq.addFile("indice_Compra_ItemComprado.idxc"));
            indice_ItemComprado_Compra = new IndiceChaveComposta(20, svarq.addFile("indice_ItemComprado_Compra.idxc"));
            indice_Cliente_Produto = new IndiceChaveComposta(20, svarq.addFile("indice_Cliente_Produto.idxc"));
            indice_Produto_Cliente = new IndiceChaveComposta(20, svarq.addFile("indice_Produto_Cliente.idxc"));

            menuPrincipal();

            arqProdutos.close();
            arqCategorias.close();
            arqClientes.close();
            arqCompra.close();
            arqItemComprado.close();
            read.close();
        }//end try
        catch (Exception e) {
            e.printStackTrace();
        }//end catch
    }//end main

    /**
     *
     * @throws Exception
     */
    private static void menuPrincipal() throws Exception {
        byte opcao;
        boolean encerrarPrograma = false;
        do {
            try {
                System.out.println(
                        "\n\t*** Tela Principal ***\n"
                        + "0- Efetuar Login Com Email \n"
                        + "1 - É novo por aqui? Então efetue seu cadastro\n"
                        + "2 - Sair "
                );
                System.out.print("Digite a opção: ");
                opcao = read.nextByte();
                switch (opcao) {
                    case 0:
                        loginEmail();
                        break;
                    case 1:
                        menuCadastroCliente();
                        break;
                    case 2:
                        encerrarPrograma = true;
                        break;
                    default:
                        System.out.println("Entrada invalida, digite novamente!\n");
                        Thread.sleep(1000);
                        break;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("\nO padrão digitado é invalido!\nTente novamente!");
                Thread.sleep(1000);
                read.next();//Limpar teclado
            }
        } while (!encerrarPrograma);

    }//Fim menuPrincipal 

    /**
     * loginEmail
     *
     * @throws Exception 
     *
     */
    //  /**
    private static void loginEmail() throws Exception {
        String email;
        System.out.println("\n\t*** LOGIN ***\n");
        System.out.print("Email: ");
        email = read.next();

        if (email.equals("admin")) {
            gerenciadorSistema();
        } else {
            Cliente c = getCliente(email);
            if (c != null) {
                System.out.println("\nUltimo Login realizado em " + dt.format(c.ultimoLogin));
                c.ultimoLogin = new Date();
                arqClientes.alterar(c.getID(), c);
                Thread.sleep(1000);
                menuCliente(c.getID());
            } else {
                System.out.println("Usuario não encontrado!\nVerifique se o email esta correto!");
            }
        }
    }//loginEmail

    /**
     * Menu de cadastro de cliente
     *
     * @throws Exception 
     *
     */
    private static void menuCadastroCliente() throws Exception {
        String nome, email, cpf;
        int id;
        boolean erro = false;
        System.out.println("\n\t*** Cadastro ***\n");
        System.out.print("Nome completo: ");
        nome = read.nextLine();
        System.out.print("Email: ");
        email = read.next();
        do {
            System.out.print("CPF: ");
            cpf = read.next().replace(".", "").replace("-", "");
            System.out.println(cpf);
            if (cpf.length() != 11) {
                System.out.println("CPF inválido!\nDigite novamente!");
                erro = true;
            }
        } while (erro);
        if (getCliente(email) == null) {

            id = arqClientes.inserir(new Cliente(nome, email, cpf));
            System.out.println("Guarde seu email, pois é através dele que você efetua o login em nossa plataforma");
            System.out.println("Cadastro efetuado!");
            gerenciadorSistema();
        } else {
            System.out.println("\nJá existe Usuario com esse email!\nCadastro cancelado!");
        }
    }//Fim menuCadastroCliente

    /**
     * gerenciadorSistema
     *
     * @throws Exception 
     *
     */
    private static void gerenciadorSistema() throws Exception {
        byte opcao;
        boolean encerrarPrograma = false;
        do {
            try {
                System.out.println(
                        "\n\t*** MENU GERAL ***\n"
                        + "0 - Gerenciar produtos(Cadastrar e listar)\n"
                        + "1 - Gerenciar categorias(adicionar ou listar categorias)\n"
                        + "2 - Gerar relatorios\n"         
                        + "3 - Listar usuarios cadastrados no sistema(nome e email)\n"
                        + "4 - Sair"
                );
                System.out.print("Digite a opção: ");
                opcao = read.nextByte();
                switch (opcao) {
                    case 0:
                        menuProdutos();
                        break;
                    case 1:
                        menuCategoria();
                        break;
                    case 2:
                        Relatorio();
                        break;
                    case 3:
                        System.out.println();
                        ArrayList<Cliente> list = arqClientes.toList();
                        if (list.isEmpty()) {
                            System.out.println("Nao ha usuarios cadastrados!");
                        } else {
                            for (Cliente c : list) {
                                System.out.println(c);
                                Thread.sleep(500);
                            }
                        }
                        break;
                    case 4:
                        encerrarPrograma = true;
                        break;
                    default:
                        System.out.println("Opcao invalida!\n");
                        Thread.sleep(1000);
                        break;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("\n Erro!\nTente novamente!");
                Thread.sleep(1000);
                read.next();//Limpar buffer do Scanner
            }

        } while (!encerrarPrograma);
    }
    
      /**
     * Menu administrativo de acesso a relatorios de compra do sistema
     * @throws Exception
     * */
    private static void Relatorio() throws Exception 
    {//Inicio menuRelatorio
        byte opcao;
        boolean fecharMenu = false;
        int idCliente, idProduto, quant;
        Produto p = null;
        Cliente c = null;
        do{
            System.out.println(
                    "\n\t*** MENU RELATORIO ***\n"                       +
                    "0 - Mostrar os N produtos mais Vendidos\n"          +
                    "1 - Mostrar os N melhores clientes\n"               +
                    "2 - Mostrar os produtos comprados por um cliente\n" +
                    "3 - Mostrar Clientes que compraram um produto\n"    +
                    "4 - Sair"
                    );
            System.out.print("Digite sua opcao: ");
            opcao = read.nextByte();
            switch(opcao){
                case 0:
                    ArrayList<Produto> listP = arqProdutos.toList();
                    if(listP.isEmpty()) System.out.println("\nNão tem produtos em nosso sistema ainda!");
                    else{
                        System.out.print("Digite a quantidade de produtos que deseja saber: ");
                        quant = read.nextInt();
                        System.out.println();
                        //Ordena a lista de forma decrescente:
                        listP.sort((p1,p2) -> - Integer.compare(p1.getQuantVendidos(), p2.getQuantVendidos()));
                        for(Produto n: listP){
                            System.out.println("Produto de ID: " + n.getID() + " Nome: " + n.nomeProduto + "\tQuantidade vendida: " + n.getQuantVendidos());
                            quant--;
                            if(quant == 0) break;
                        }
                    }
                    break;
                case 1:
                    ArrayList<Cliente> listC = arqClientes.toList();
                    if(listC.isEmpty()) System.out.println("Não ha clientes para mostrar!");
                    else{
                        System.out.print("Digite a quantidade de Clientes: ");
                        quant = read.nextInt();
                        System.out.println();
                        //Ordena a lista de forma decrescente:
                        listC.sort((c1,c2) -> - Float.compare(c1.getTotalGasto(), c2.getTotalGasto()));
                        for(Cliente n: listC){
                            System.out.println("Cliente de ID: " + n.getID() + " Nome: " + n.nomeCliente + "\tGasto total: " + tf.format(n.getTotalGasto()));
                            quant--;
                            if(quant == 0) break; 
                        }
                    }
                    break;
                case 2:
                    System.out.print("Digite o id do cliente: ");
                    idCliente = read.nextInt();
                    c = arqClientes.pesquisar(idCliente - 1);
                    if (c != null){
                        int[] idsProdutos = indice_Cliente_Produto.lista(idCliente);
                        System.out.println("\nO cliente " + c.nomeCliente + " de ID " + c.getID() + " comprou: ");
                        for(int i = 0; i < idsProdutos.length; i++){
                            p = arqProdutos.pesquisar(idsProdutos[i] - 1);
                            System.out.println(
                                    "\n\tProduto " + i + " -> " + 
                                    " ID: " + p.getID() + 
                                    " Nome: " + p.nomeProduto + 
                                    " Marca: " + p.marca
                                    );
                        }
                    }
                    else {
                        System.out.println("\nID Invalido!");
                        Thread.sleep(1000);
                    }
                    break;
                case 3:
                    System.out.print("Digite o id do Produto a consultar: ");
                    idProduto = read.nextInt();
                    p = arqProdutos.pesquisar(idProduto - 1);
                    if(p != null){
                        int[] idsClientes = indice_Produto_Cliente.lista(idProduto);
                        System.out.println("\nO produto '" + p.nomeProduto + "' de ID " + p.getID() + " foi comprado por: ");
                        for(int i = 0; i < idsClientes.length; i++){
                            c = arqClientes.pesquisar(idsClientes[i] - 1);
                            System.out.println();
                            System.out.println(c);
                        }
                    }
                    else{
                        System.out.println("\nProduto Inexistende!");
                        Thread.sleep(1000);
                    }
                    break;
                case 4:
                    fecharMenu = true;
                    break;    
                default:
                    System.out.println("Opcao invalida!\n");
                    Thread.sleep(1000);
                    break;
            }
        }while(!fecharMenu); 
    }//Fim Relatorio 

    /**
     * Menu de acoes do Usuario
     *
     * @param idCliente O id do cliente que fez login
     * @throws Exception 
     *
     */
    private static void menuCliente(int idCliente) throws Exception {//Inicio menuCliente 
        byte opcao;
        int idCompra;
        ArrayList<Compra> minhasCompras = null;
        ArrayList<ItemComprado> meusItensComprados = null;
        ArrayList<Produto> listProdutos = null;
        boolean encerrarPrograma = false;
        do {
            try {
                System.out.println(
                        "\n\t*** MENU CLIENTE ***\n"
                        + "0 - Nova compra\n"
                        + "1 - Minhas compras\n"
                        + "2 - Alterar meus dados\n"
                        + "3 - Excluir conta\n"
                        + "4 - Sair"
                );
                System.out.print("Digite sua opcao: ");
                opcao = read.nextByte();
                switch (opcao) {
                    case 0:
                        idCompra = arqCompra.inserir(new Compra(idCliente, new Date()));
                        compraProdutos(idCliente, idCompra);
                        break;
                    case 1:
                        listProdutos = arqProdutos.toList();
                        minhasCompras = listComprasDoCliente(idCliente);
                        for (Compra c : minhasCompras) {
                            System.out.println("\n*** Compra de ID: " + c.getID() + " Data: " + dt.format(c.dataCompra));
                            meusItensComprados = listarOsItensComprados(c.getID());
                            for (ItemComprado ic : meusItensComprados) {
                                for (Produto p : listProdutos) {
                                    if (p.getID() == ic.idProduto) {
                                        System.out.println(
                                                "\n\tProduto: " + p.nomeProduto
                                                + "\n\tMarca: " + p.marca
                                                + "\n\tPreço: " + tf.format(ic.precoUnitario)
                                                + "\n\tQuant: " + ic.qtdProduto
                                        );
                                    }
                                }
                            }
                        }
                        break;
                    case 2:
                        uptadeDados(idCliente);
                        break;
                    case 3:
                        System.out.println("\nConfirmar opção? \n0 - Sim\n1 - Nao");
                        System.out.println("Digite a opcao desejada: ");
                        switch (read.nextInt()) {
                            case 0:
                                if (arqClientes.remover(idCliente - 1)) {
                                    System.out.println("Remoção feita com sucesso!");
                                }
                                break;
                            case 1:
                                System.out.println("\nObrigado por continuar!");
                                break;
                            default:
                                System.out.println("\nOpcao invalida!");
                                Thread.sleep(1000);
                                break;
                        }
                        encerrarPrograma = true;
                        break;
                    case 4:
                        encerrarPrograma = true;
                        break;
                    default:
                        System.out.println("Opcao invalida!\n");
                        Thread.sleep(1000);
                        break;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("\nErro !\nTente novamente!");
                Thread.sleep(1000);
                read.next();//Limpar buffer do Scanner
            }
        } while (!encerrarPrograma);
    }

    /**
     * uptadeDados
     *
     * @param idCliente ID do cliente
     * @throws Exception
     *
     */
    private static void uptadeDados(int idCliente) throws Exception {//Inicio
        byte opcao;
        boolean encerrarPrograma = false;
        Cliente c = arqClientes.pesquisar(idCliente - 1);
        do {
            System.out.println(
                    "\n\t*** Alterar meus dados ***\n"
                    + "0 - Alterar email\n"
                    + "1 - Sair "
            );
            System.out.print("Digite a opção: ");
            opcao = read.nextByte();
            System.out.println();
            switch (opcao) {
                case 0:
                    System.out.print("\nDigite email: ");
                    String email = read.nextLine();
                    c.email = email;
                    if (arqClientes.alterar(c.getID(), c)) {
                        System.out.println("\nAlterado Corretamente, guarde seu email para acesso em nossa plataforma!");
                    } else {
                        System.out.println("\nErro tente novamente!");
                    }
                    break;
                case 1:
                    encerrarPrograma = true;
                    break;
                default:
                    System.out.println("Opcao invalida!\n");
                    Thread.sleep(1000);
                    break;
            }

        } while (!encerrarPrograma);
    }

    /**
     * Opcao comprar produtos
     *
     * @param idCliente ID do cliente que fez login
     * @param idCompra ID da nova compra criada
     * @throws Exception 
     *
     */
    private static void compraProdutos(int idCliente, int idCompra) throws Exception {
        byte opcao;
        float gasto = 0;
        boolean encerrarPrograma = false;
        int[] lista;
        int idItemComprado = 0;
        do {
            try {
                System.out.println(
                        "\n\t*** MENU DE COMPRA ***\n"
                        + "0 - Listar produtos\n"
                        + "1 - Adicionar produto a compra\n"
                        + "2 - Remover produto da compra\n"
                        + "3 - Visualizar compra\n"
                        + "4 - Finalizar compra\n"
                        + "5 - Cancelar compra\n"
                );
                System.out.print("Digite a opção: ");
                opcao = read.nextByte();
                System.out.println();
                switch (opcao) {
                    case 0:
                        listaProdutosCadastrados();
                        break;
                    case 1:
                        gasto += adicionarItem(idCliente, idCompra);
                        break;
                    case 2:
                        if (indice_Compra_ItemComprado.lista(idCompra).length == 0) {
                            System.out.println("\nNão ha itens a serem removidos");
                        } else {
                            System.out.print("Qual o id do item a ser removido: ");
                            idItemComprado = read.nextInt();
                            ItemComprado ic = arqItemComprado.pesquisar(idItemComprado - 1);
                            if (ic != null) {
                                if (arqItemComprado.remover(ic.getID() - 1)) {
                                    indice_Compra_ItemComprado.excluir(idCompra, ic.getID());
                                    indice_ItemComprado_Compra.excluir(ic.getID(), idCompra);
                                    indice_Produto_Cliente.excluir(ic.idProduto, idCliente);
                                    indice_Cliente_Produto.excluir(idCliente, ic.idProduto);
                                    Produto p = arqProdutos.pesquisar(ic.idProduto - 1);
                                    gasto -= ic.precoUnitario * ic.qtdProduto;
                                    p.addQuantVendidos(-ic.qtdProduto);
                                    arqProdutos.alterar(p.getID(), p);
                                    System.out.println("Removido " + ic.qtdProduto + "x '" + p.nomeProduto + "' com sucesso!");
                                } else {
                                    System.out.println("\nErro! tente novamente!");
                                }
                            } else {
                                System.out.println("\nID invalido!");
                            }
                        }
                        break;
                    case 3:
                        lista = indice_Compra_ItemComprado.lista(idCompra);
                        String nomeProduto;
                        ItemComprado ic = null;
                        if (lista.length == 0) {
                            System.out.println("\nVoce ainda não adicionou um produto!");
                        } else {
                            for (int i = 0; i < lista.length; i++) {
                                ic = arqItemComprado.pesquisar(lista[i] - 1);
                                nomeProduto = arqProdutos.pesquisar(ic.idProduto - 1).nomeProduto;
                                System.out.println("\tID: " + ic.getID() + " "
                                        + ic.qtdProduto + "x'" + nomeProduto + "'\tPreço Uni.: " + tf.format(ic.precoUnitario));
                            }
                        }
                        break;
                    case 4:
                        Cliente c = arqClientes.pesquisar(idCliente - 1);
                        c.adicionarGasto(gasto);
                        arqClientes.alterar(c.getID(), c);
                        System.out.println("Compra efetuada com sucesso!\nGasto total nessa compra: " + tf.format(gasto));
                        encerrarPrograma = true;
                        break;
                    case 5:
                        encerrarPrograma = true;
                        lista = indice_Compra_ItemComprado.lista(idCompra);
                        for (int i = 0; i < lista.length; i++) {
                            indice_Compra_ItemComprado.excluir(idCompra, lista[i]);
                            indice_ItemComprado_Compra.excluir(lista[i], idCompra);
                            indice_Produto_Cliente.excluir(arqItemComprado.pesquisar(lista[i]).idProduto, idCliente);
                            indice_Cliente_Produto.excluir(idCliente, arqItemComprado.pesquisar(lista[i]).idProduto);
                            arqItemComprado.remover(lista[i] - 1);
                        }
                        arqCompra.remover(idCompra - 1);
                        System.out.println("Sua compra foi cancelada!");
                        break;
                    default:
                        System.out.println("Opcao invalida!\n");
                        Thread.sleep(1000);
                        break;
                }
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("\n Erro!\nTente novamente!");
                Thread.sleep(1000);
                read.next();//Limpar buffer do Scanner
            }
        } while (!encerrarPrograma);
    }

    /**
     * Metodo para Adicionar um novo produto a compra
     *
     * @param idCliente ID do cliente
     * @param idCompra ID da compra
     * @return O valor da compra do produto
     * @throws Exception 
     *
     */
    private static float adicionarItem(int idCliente, int idCompra) throws Exception {
        int idItemComprado;
        float gasto = 0;
        boolean qtdInvalida;
        boolean idInvalido = false;
        do {
            qtdInvalida = false;
            System.out.print("Digite o id do produto desejado: ");
            int id = read.nextInt();
            Produto p = arqProdutos.pesquisar(id - 1);
            if (p != null && p.getID() != -1) {
                do {
                    qtdInvalida = false;
                    System.out.print("Qual a quantidade desejada? ");
                    byte qtdProduto = read.nextByte();
                    if (qtdProduto > 0 && qtdProduto <= 255) {
                        ItemComprado ic = new ItemComprado(idCompra, qtdProduto, p);
                        idItemComprado = arqItemComprado.inserir(ic);
                        indice_Compra_ItemComprado.inserir(idCompra, idItemComprado);
                        indice_ItemComprado_Compra.inserir(idItemComprado, idCompra);
                        indice_Produto_Cliente.inserir(p.getID(), idCliente);
                        indice_Cliente_Produto.inserir(idCliente, p.getID());
                        System.out.println("Adicionado " + qtdProduto + "x '" + p.nomeProduto + "'");
                        gasto = qtdProduto * p.preco;
                        p.addQuantVendidos(qtdProduto);
                        arqProdutos.alterar(p.getID(), p);
                    } else {
                        System.out.println("Valor invalido!");
                        qtdInvalida = true;
                    }
                } while (qtdInvalida);
            } else {
                System.out.println("\nId invalido!");
                idInvalido = true;
            }
        } while (idInvalido);
        return gasto;
    }//end adicionaritem

    /**
     * Menu de categorias de produtos
     *
     * @throws Exception
     *
     */
    private static void menuCategoria() throws Exception {//Inicio menuCategoria
        byte opcao;
        boolean encerrarPrograma = false;
        Integer[] listaC = null;
        do {
            System.out.println(
                    "\n\t*** MENU DE CATEGORIAS ***\n"
                    + "0 - Adicionar categoria\n"
                    + "1 - Remover categoria\n"
                    + "2 - Listar categorias cadastradas\n"
                    + "3 - Listar produtos cadastrados em uma categoria\n"
                    + "4 - Sair"
            );
            System.out.print("Digite a opção: ");
            opcao = read.nextByte();
            System.out.println();
            switch (opcao) {
                case 0:
                    adicionarCategoria();
                    break;
                case 1:
                    removerCategoria();
                    break;
                case 2:
                    listaC = listaCategoriasCadastradas();
                    if (listaC == null) {
                        System.out.println("Não ha categorias cadastradas!");
                    }
                    break;
                case 3:
                    consultaCategoria();
                    break;
                case 4:
                    encerrarPrograma = true;
                    break;
                default:
                    System.out.println("Opcao invalida!\n");
                    Thread.sleep(1000);
                    break;
            }
        } while (!encerrarPrograma);
    }//Fim menuCategoria

    /**
     * Menu administrativo de Produtos
     *
     * @throws Exception
     *
     */
    private static void menuProdutos() throws Exception {//Inicio menuProdutos
        byte opcao;
        boolean encerrarPrograma = false;
        do {
            System.out.println(
                    "\n\t*** MENU DE PRODUTOS ***\n"
                    + "0 - Adicionar produto\n"
                    + "1 - Remover produto\n"
                    + "2 - Alterar produto\n"
                    + "3 - Consultar produto\n"
                    + "4 - Listar produtos cadastrados\n"
                    + "5 - Sair"
            );
            System.out.print("Digite a opção: ");
            opcao = read.nextByte();
            System.out.println();
            switch (opcao) {
                case 0:
                    adicionarProduto();
                    break;
                case 1:
                    removerProduto();
                    break;
                case 2:
                    alterarProduto();
                    break;
                case 3:
                    consultaProduto();
                    break;
                case 4:
                    listaProdutosCadastrados();
                    break;
                case 5:
                    encerrarPrograma = true;
                    break;
                default:
                    System.out.println("Opcao invalida!\n");
                    Thread.sleep(1000);
                    break;
            }
        } while (!encerrarPrograma);
    }//Fim menuProdutos

    /**
     * Metodo para adicionar um produto.
     *
     * @throws Exception
     *
     */
    private static void adicionarProduto() throws Exception {
        //inserir produto  
        String nomeProduto, descricao, marca, origem;
        int idCategoria = 0;
        Integer[] idsValidosC;
        float preco;
        int id;
        boolean erro, valido;
        System.out.println("\t** Adicionar produto **\n");
        System.out.print("Nome do produto: ");
        nomeProduto = read.nextLine();
        nomeProduto = read.nextLine();
        System.out.print("Descricao do produto: ");
        descricao = read.nextLine();
        System.out.print("Preco do produto: ");
        preco = read.nextFloat();
        System.out.print("Marca do produto: ");
        marca = read.nextLine();
        marca = read.nextLine();
        System.out.print("Origem do produto: ");
        origem = read.nextLine();
        System.out.println();
        if ((idsValidosC = listaCategoriasCadastradas()) != null) {
            System.out.print("\nEscolha uma categoria para o produto,\ne digite o ID: ");
            do {
                valido = false;
                idCategoria = read.nextInt();
                valido = Arrays.asList(idsValidosC).contains(idCategoria);
                if (!valido) {
                    System.out.println("Esse ID não é valido!\nDigite um ID valido: ");
                }
            } while (!valido);
            do {
                erro = false;
                System.out.println("\nAdicionar novo produto?");
                System.out.print("1 - SIM\n2 - NÂO\nR: ");
                switch (read.nextByte()) {
                    case 1:
                        id = arqProdutos.inserir(new Produto(nomeProduto, descricao, preco, marca, origem, idCategoria));
                        System.out.println("\nProduto inserido com o ID: " + id);
                        break;
                    case 2:
                        System.out.println("\nNovo produto não foi inserido!");
                        break;
                    default:
                        System.out.println("\nOpção Inválida!\n");
                        erro = true;
                        break;
                }
            } while (erro);
        } else {
            System.out.println("\nOps..! Aparentemente não existem categorias para associar o novo produto!");
            System.out.println("Por favor, crie ao menos uma categoria antes de adicionar um produto!");
            Thread.sleep(1000);
        }
    }

    /**
     * Metodo para adicionar uma categoria.
     *
     * @throws Exception
     *
     */
    private static void adicionarCategoria() throws Exception {
        String nomeCategoria;
        boolean erro;
        boolean outro;
        int id;
        System.out.println("\t** Adicionar categoria **\n");
        do {
            outro = false;
            System.out.print("Digite o nome da categoria: ");
            nomeCategoria = read.nextLine();
            nomeCategoria = read.nextLine();
            do {
                erro = false;
                System.out.println("\nAdicionar nova categoria '" + nomeCategoria + "' ?");
                System.out.print("1 - SIM\n2 - NÂO\nR: ");
                switch (read.nextByte()) {
                    case 1:
                        id = arqCategorias.inserir(new Categoria(nomeCategoria));
                        System.out.println("\nCategoria criada com o ID: " + id);
                        System.out.println("Operacao concluida com sucesso!");
                        Thread.sleep(1000);
                        System.out.println("\nDeseja criar outra categoria ?");
                        System.out.print("1 - SIM\n2 - NÂO\nR: ");
                        if (read.nextByte() == 1) {
                            outro = true;
                        }
                        break;
                    case 2:
                        System.out.println("\nNova categoria não foi criada!");
                        break;
                    default:
                        System.out.println("\nOpção Inválida!\n");
                        erro = true;
                        break;
                }
            } while (erro);
        } while (outro);
    }

    /**
     * Metodo para remover um produto
     *
     * @throws Exception
     *
     */
    private static void removerProduto() throws Exception {
        int id;
        boolean erro, result;
        System.out.println("\t** Remover produto **\n");
        System.out.print("ID do produto a ser removido: ");
        id = read.nextInt();
        if (indice_Produto_Cliente.lista(id).length != 0) {
            System.out.println("Esse produto não pode ser removido pois foi comprado por algum Cliente!");
        } else {
            do {
                erro = false;
                System.out.println("\nRemover produto?");
                System.out.print("1 - SIM\n2 - NÂO\nR: ");
                switch (read.nextByte()) {
                    case 1:
                        result = arqProdutos.remover(id - 1);
                        if (result) {
                            System.out.println("Removido com sucesso!");
                        } else {
                            System.out.println("Produto não encontrado!");
                        }
                        break;
                    case 2:
                        System.out.println("\nOperação Cancelada!");
                        break;
                    default:
                        System.out.println("\nOpção Inválida!\n");
                        erro = true;
                        break;
                }
            } while (erro);
        }
    }

    /**
     * Metodo para remover uma categoria
     *
     * @throws Exception
     *
     */
    private static void removerCategoria() throws Exception {
        int idCategoria;
        int idCategoriaNew;
        boolean erro, valido, result = false;
        Integer[] idsValidosC;
        ArrayList<Produto> lista;
        String nomeCategoria = null;
        System.out.println("\t** Remover categoria **\n");
        do {
            erro = false;
            System.out.print("ID da categoria a ser removida: ");
            idCategoria = read.nextInt();
            if (idCategoria > 0) {
                nomeCategoria = getNomeCategoria(idCategoria - 1);
                if (nomeCategoria == null) {
                    erro = true;
                    System.out.println("Categoria inexistente!");
                }
            } else {
                erro = true;
                System.out.println("ID Inválida!");
            }
            System.out.println();
        } while (erro);

        do {
            erro = false;
            System.out.println("\nRemover a categoria '" + nomeCategoria + "' ?");
            System.out.print("1 - SIM\n2 - NÂO\nR: ");
            switch (read.nextByte()) {
                case 1:
                    lista = listProdutosPorCategoria(idCategoria);
                    if (lista.isEmpty()) {
                        System.out.println("Não ha produtos associados a '" + nomeCategoria + "', procedendo com remoção...");
                        result = arqCategorias.remover(idCategoria - 1);
                        if (result) {
                            System.out.println("Removido com sucesso!");
                        }
                    } else {
                        System.out.println("\nExistem produtos nessa categoria!!");
                        System.out.println(
                                "O que deseja fazer?\n"
                                + "0 - Apagar todos os produtos pertencentes e a categoria\n"
                                + "1 - Mudar a categoria dos produtos e remover\n"
                                + "2 - Cancelar remoção\n"
                        );
                        do {
                            erro = false;
                            System.out.print("Opção: ");
                            switch (read.nextByte()) {//Inicio switch
                                case 0:
                                    for (Produto p : lista) {
                                        System.out.println("Removendo '" + p.nomeProduto + "'...");
                                        result = arqProdutos.remover(p.getID() - 1);
                                    }
                                    System.out.println("Excluindo categoria '" + nomeCategoria + "'...");
                                    result = arqCategorias.remover(idCategoria - 1);
                                    System.out.println("Concluido exclusão de " + lista.size() + " produtos e 1 categoria.");
                                    lista = null;
                                    break;
                                case 1:
                                    idsValidosC = listaCategoriasCadastradas();
                                    if (idsValidosC.length == 1) {
                                        System.out.println("\nOperação não é possivel!\nSo tem uma categoria!");
                                        Thread.sleep(1000);
                                    } else {
                                        System.out.println("\nProdutos:");
                                        for (Produto p : lista) {
                                            System.out.println(
                                                    "\nId: " + p.getID()
                                                    + "\nNome: " + p.nomeProduto
                                                    + "\nDescricao: " + p.descricao
                                                    + "\nMarca: " + p.marca
                                            );
                                            System.out.print("\nEscolha uma outra categoria para o produto,\ne digite o ID: ");
                                            do {
                                                valido = false;
                                                idCategoriaNew = read.nextInt();
                                                valido = Arrays.asList(idsValidosC).contains(idCategoria);
                                                if (!valido) {
                                                    System.out.println("Esse ID não é valido!\nDigite um ID valido: ");
                                                } else if (idCategoriaNew == idCategoria) {
                                                    System.out.println("Não pode escolher a mesma categoria antiga!\nDigite um ID valido: ");
                                                    valido = false;
                                                }
                                            } while (!valido);
                                            p.idCategoria = idCategoriaNew;
                                            if (arqProdutos.alterar(p.getID(), p)) {
                                                System.out.println("Movido com sucesso!");
                                            } else {
                                                System.out.println("Algo de errado aconteceu!\nNão foi possivel mover!");
                                            }
                                        }
                                        result = arqCategorias.remover(idCategoria - 1);
                                    }//Fim else
                                    break;
                                case 2:
                                    System.out.println("\nOperação Cancelada!");
                                    break;
                                default:
                                    System.out.println("\nOpção Inválida!\n");
                                    erro = true;
                                    break;
                            }//Fim switch
                        } while (erro);
                    }
                    break;
                case 2:
                    System.out.println("\nOperação Cancelada!");
                    break;
                default:
                    System.out.println("\nOpção Inválida!\n");
                    erro = true;
                    break;
            }
        } while (erro);
    }

    /**
     * Metodo para alterar um produto
     *
     * @throws Exception
     *
     */
    private static void alterarProduto() throws Exception {
        String nomeProduto, descricao, marca, origem;
        int idCategoria;
        Integer[] idsValidosC;
        float preco;
        int id;
        boolean erro, result, valido;
        System.out.println("\t** Alterar produto **\n");
        do {
            erro = false;
            System.out.print("ID do produto a ser alterado: ");
            id = read.nextInt();
            if (id <= 0) {
                erro = true;
                System.out.println("ID Inválida! ");
            }
            System.out.println();
        } while (erro);
        System.out.print("Nome do produto: ");
        nomeProduto = read.nextLine();
        nomeProduto = read.nextLine();
        System.out.print("Descricao do produto: ");
        descricao = read.nextLine();
        System.out.print("Preco do produto: ");
        preco = read.nextFloat();
        System.out.print("Marca do produto: ");
        marca = read.nextLine();
        marca = read.nextLine();
        System.out.print("Origem do produto: ");
        origem = read.nextLine();
        System.out.println();
        if ((idsValidosC = listaCategoriasCadastradas()) != null) {
            System.out.print("Escolha uma categoria para o produto,\ne digite o ID: ");
            do {
                valido = false;
                idCategoria = read.nextInt();
                valido = Arrays.asList(idsValidosC).contains(idCategoria);
                if (!valido) {
                    System.out.print("Esse ID não é valido!\nDigite um ID valido: ");
                }
            } while (!valido);
            do {
                erro = false;
                System.out.println("\nAlterar produto?");
                System.out.print("1 - SIM\n2 - NÂO\nR: ");
                switch (read.nextByte()) {
                    case 1:
                        result = arqProdutos.alterar(id, new Produto(nomeProduto, descricao, preco, marca, origem, idCategoria));
                        if (result) {
                            System.out.println("Alterado com sucesso!");
                        } else {
                            System.out.println("Produto para alterar não encontrado!");
                        }
                        break;
                    case 2:
                        System.out.println("\nOperação Cancelada!");
                        break;
                    default:
                        System.out.println("\nOpção Inválida!\n");
                        erro = true;
                        break;
                }
            } while (erro);
        } else {
            System.out.println("\nOps..! Aparentemente não existem categorias para associar ao produto!");
            System.out.println("Por favor, crie ao menos uma categoria antes de adicionar um produto!");
            Thread.sleep(1000);
        }
    }

    /**
     *consultar um produto pelo ID
     *
     * @throws Exception
     *
     */
    private static void consultaProduto() throws Exception {
        boolean erro;
        int id;
        Produto p;
        Categoria c;
        System.out.println("\t** Consultar produto **\n");
        do {
            erro = false;
            System.out.print("ID do produto a ser consultado: ");
            id = read.nextInt();
            if (id <= 0) {
                erro = true;
                System.out.println("ID Inválida! ");
            }
            System.out.println();
        } while (erro);
        p = arqProdutos.pesquisar(id - 1);
        if (p != null && p.getID() != -1) {
            c = arqCategorias.pesquisar(p.idCategoria - 1);
            System.out.println(
                    "Id: " + p.getID()
                    + "\nNome: " + p.nomeProduto
                    + "\nDescricao: " + p.descricao
                    + "\nPreco: " + tf.format(p.preco)
                    + "\nMarca: " + p.marca
                    + "\nOrigem: " + p.origem
            );
            if (c != null) {
                System.out.println("Categoria: " + c.nome);
            } else {
                System.out.println("Categoria: " + p.idCategoria);
            }
        } else {
            System.out.println("Produto não encontrado!");
        }
    }

    /**
     * Mostra Todos os produtos daquela categoria escolhida
     *
     * @throws Exception
     *
     */
    private static void consultaCategoria() throws Exception {
        boolean erro;
        int idCategoria;
        String nomeCategoria;
        ArrayList<Produto> lista;
        System.out.println("\t** Listar produtos de uma categoria **\n");
        do {
            erro = false;
            System.out.print("ID da categoria a ser consultada: ");
            idCategoria = read.nextInt();
            if (idCategoria <= 0) {
                erro = true;
                System.out.println("ID Inválida! ");
            }
            System.out.println();
        } while (erro);
        lista = listProdutosPorCategoria(idCategoria);
        if (lista != null && !lista.isEmpty()) {
            nomeCategoria = getNomeCategoria(idCategoria - 1);
            System.out.println("Produtos pertencentes a '" + nomeCategoria + "'");
            for (Produto p : lista) {
                System.out.println(
                        "Id: " + p.getID()
                        + "\nNome: " + p.nomeProduto
                        + "\nDescricao: " + p.descricao
                        + "\nPreco: " + tf.format(p.preco)
                        + "\nMarca: " + p.marca
                        + "\nOrigem: " + p.origem
                        + "\nCategoria: " + nomeCategoria
                );
            }
        } else {
            System.out.println("Não ha produtos nessa categoria, ou ela não existe!");
        }
    }

    /**
     * Obter todos os produtos cadastrados
     *
     * @throws Exception
     *
     */
    private static void listaProdutosCadastrados() throws Exception {
        String nomeCategoria;
        ArrayList<Produto> lista = arqProdutos.toList();
        if (!lista.isEmpty()) {
            System.out.println("\t** Lista dos produtos cadastrados **\n");
        }
        for (Produto p : lista) {
            if (p != null && p.getID() != -1) {
                nomeCategoria = getNomeCategoria(p.idCategoria - 1);
                System.out.println(
                        "Id: " + p.getID()
                        + "\nNome: " + p.nomeProduto
                        + "\nDescricao: " + p.descricao
                        + "\nPreco: " + tf.format(p.preco)
                        + "\nMarca: " + p.marca
                        + "\nOrigem: " + p.origem
                );
                if (nomeCategoria != null) {
                    System.out.println("Categoria: " + nomeCategoria);
                } else {
                    System.out.println("Categoria: " + p.idCategoria);
                }
                System.out.println();
                Thread.sleep(500);
            }
        }
    }

    /**
     * Obter todas as categorias cadastradas no sistema
     * @throws Exception
     *
     */
    private static Integer[] listaCategoriasCadastradas() throws Exception {
        int count = 0;
        ArrayList<Categoria> lista = arqCategorias.toList();
        Integer[] idsValidos = null; //Lista retornando os ids de categorias validos para consulta
        if (!lista.isEmpty()) {
            idsValidos = new Integer[lista.size()];
            System.out.println("\t** Lista de categorias cadastradas **\n");
            for (Categoria c : lista) {
                System.out.println(c);
                idsValidos[count] = c.getID();
                count++;
            }
        }
        return idsValidos;
    }//Fim listaCategoriasCadastradas

    /**
     * Obter lista de produtos pertencente a x categoria
     * @throws Exception
     *
     */
    private static ArrayList<Produto> listProdutosPorCategoria(int idCategoria) throws Exception {
        ArrayList<Produto> lista = arqProdutos.toList();
        lista.removeIf(p -> p.idCategoria != idCategoria);
        return lista;
    }

    /**
     * Por meio do id da categoria obter seu nome
     *
     */
    private static String getNomeCategoria(int idCategoria) throws Exception {
        String nome = null;
        Categoria c = arqCategorias.pesquisar(idCategoria);
        if (c != null) {
            nome = c.nome;
        }
        return nome;
    }//Fim getNomeCategoria

    /**
     * Obter o cliente por meio de seu email de cadastro
     * @throws Exception 
     *
     */
    private static Cliente getCliente(String email) throws Exception {
        Cliente cliente = null;
        ArrayList<Cliente> lista = arqClientes.toList();
        for (Cliente c : lista) {
            if (c.email.equals(email)) {
                cliente = c;
                break;
            }
        }
        return cliente;
    }//Fim getCliente

    /**
     * Metodo para obter uma lista de compras de um cliente
     * @throws Exception
     *
     */
    private static ArrayList<Compra> listComprasDoCliente(int idCliente) throws Exception {//Inicio mostraCompras
        ArrayList<Compra> lista = arqCompra.toList();
        lista.removeIf(c -> c.idCliente != idCliente);
        return lista;
    }

    /**
     * Metodo para obter a lista de items comprados pertencentes a uma compra
     * @throws Exception
     *
     */
    private static ArrayList<ItemComprado> listarOsItensComprados(int idCompra) throws Exception {
        ArrayList<ItemComprado> lista = arqItemComprado.toList();
        lista.removeIf(ic -> ic.idCompra != idCompra);
        return lista;
    }
}//end Principal
