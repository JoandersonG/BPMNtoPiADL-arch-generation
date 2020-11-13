import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    private static boolean isPalavraDeInteresse(String palavra) {
//        switch (palavra) {
//            case "connection":
//        }
        return true;
    }

    private static String getProximaPalavra(Scanner sc) {

        if (sc == null || !sc.hasNext()) {
            return ""; //não há mais palavra para retornar ou "sc" é inválido
        }
        String proxima = sc.next();
        if (proxima.matches("//.")) { //linha de comentário
            sc.nextLine();//consumo a linha inteira
            proxima = getProximaPalavra(sc);
        }

        return proxima;
    }

    public static void main(String[] args) {

        Programa programa = new Programa();
        int posicaoElementoAtual = -1;//valor que indica não haver nenhum elemento atual
        String connection = "";

        // Preparo o arquivo para ser lido
        try {
            String path = "/home/joanderson/IdeaProjects/piADL for BPMN Autocomplete/src/vendalivro2.piadl";
            File arquivo = new File(path);
            Scanner sc = new Scanner(arquivo);    //TODO: recuperar de um arquivo ao invés do System.in

            while(sc.hasNext()) {
                // Leio a próxima palavra
                String palavraAtual = getProximaPalavra(sc);
                //System.out.println("*******************Palavra atual: " + palavraAtual);
                switch (palavraAtual) {
                    case "": //não há mais próxima palavra
                        break;
                    case "connector":
                    case "component":
                        programa.addElemento(new Elemento());
                        posicaoElementoAtual = programa.getPosicaoUltimoElemento();
                        break;
                    case "connection":
                        connection = "connection";
                        break;
//                    case "protocol":
//                    case "behavior":
//                        //concluí último elemento
//                        break;

                    default: //pode ser id ou outra palavra reservada da linguagem ou nome de função ou variável
                        if (programa.getUltimoElemento().getNome().equals("")) { //é id de componente ou conector
                            programa.setNomeUltimoElemento(palavraAtual);
                            break;
                        }
                        if (connection.equals("connection")) {//é id de uma conexão, resta saber o tipo (in/out)
                            //é o id de uma connection
                            connection = palavraAtual; //tenho nome da conexão
                            break;
                        }
                        String tipoDados = "";
//                        if (palavraAtual.matches("in(.*)") || palavraAtual.matches("in")) { //é do tipo ConexaoEntrada da última conexao
//                            //tipoDados = palavraAtual.replace(' ', "");
//                            tipoDados = palavraAtual.replaceFirst("in", "");
//                            tipoDados = tipoDados.replaceFirst("(","");
//                            programa.addConexaoAoUltimoElemento(new ConexaoEntrada(connection, tipoDados)); //salvo a conexão no último elemento
//                            break;
//                        }
                        //-------> para salvar o tipo de dados da conexão
                        if (palavraAtual.matches("in(.*)")) {

                            tipoDados = palavraAtual.replace(" ", "")
                                                    .replace("in(", "")
                                                    .replace(")","");


                            programa.addConexaoAoUltimoElemento(new ConexaoEntrada(connection, tipoDados)); //salvo a conexão no último elemento
                        }
                        if (palavraAtual.matches("in")) {
                            //sei que o programa é valido, então após "in" virá:
                            // 1: (Tipo)
                            // 2: (Tipo )
                            // 3: ( Tipo)
                            // 4: ( Tipo )
                            //O que eu quero: tudo que está entre parênteses, exceto espaços em branco
                            palavraAtual = getProximaPalavra(sc); //pego a próxima palavra

                            if (palavraAtual.equals("(")) {
                                palavraAtual = getProximaPalavra(sc);// ignoro o parênteses
                            }
                            // sei que a palavra atual é
                            // 1: (Tipo)
                            // 2: (Tipo
                            // 3: Tipo)
                            //4: Tipo

                            tipoDados = palavraAtual.replace(" ", "")
                                                    .replace("(","")
                                                    .replace(")","");
                            //agora sei que palavra atual é Tipo (e sem nenhum espaço)

                            programa.addConexaoAoUltimoElemento(new ConexaoEntrada(connection, tipoDados)); //salvo a conexão no último elemento
                            break;
                        }

                        if (palavraAtual.matches("out(.*)")){
                            tipoDados = palavraAtual.replace(" ", "")
                                    .replace("out(", "")
                                    .replace(")","");


                            programa.addConexaoAoUltimoElemento(new ConexaoSaida(connection, tipoDados)); //salvo a conexão no último elemento
                            break;
                        }

                        if (palavraAtual.matches("out")) { //é do tipo ConexaoSaida

                            palavraAtual = getProximaPalavra(sc); //pego a próxima palavra

                            if (palavraAtual.equals("(")) {
                                palavraAtual = getProximaPalavra(sc);// ignoro o parênteses
                            }

                            tipoDados = palavraAtual.replace(" ", "")
                                    .replace("(","")
                                    .replace(")","");

                            programa.addConexaoAoUltimoElemento(new ConexaoSaida(connection, tipoDados));
                            break;
                        }

                }
            }

            // agora que todos os Elementos estão finalizados, atualizar seus tipos
            programa.updateTiposElementos();

            // corrigir conexões de elementos component, pois conexões internas são desnecessárias
            programa.removerConexoesDesnecessarias();

            //gerar bloco compose------------------------------------------------------------------------------------

            StringBuilder compose = new StringBuilder();
            /*heurísticas para nome dos Elementos:
                caso Inicio: "i"
                caso Conector: "fl"
                caso Gateway: "gw"
                caso FIm: "f"
                outro caso: t;
                se repetido: concatena um número, começando em 2;
            */

            // nomear nomeInst dos Elementos
            programa.setNomeInstanciaElementos();

            compose.append("\t\tcompose{\n\t\t\t");

            int i = 0;
            Elemento elementoAtual = programa.getElementoPorPosicao(i);
            while (elementoAtual != null) {
                if (i != 0) {
                    compose.append("and ");
                }
                compose.append(elementoAtual.getNomeInst())
                        .append(" is ")
                        .append(elementoAtual.getNome())
                        .append("()")
                        .append("\n\t\t\t");
                i++;
                elementoAtual = programa.getElementoPorPosicao(i);
            }
            compose.deleteCharAt(compose.length()-1);
            compose.append("}");
            //fim do bloco compose -------------------------------------------------------------------------------------

            //inicio do bloco where ------------------------------------------------------------------------------------

            StringBuilder where = new StringBuilder();
            i = 0;
            elementoAtual = programa.getElementoPorPosicao(i);


            where.append(" where {\n\t\t\t");

            while (elementoAtual != null) {

                for (int j = 0; j < elementoAtual.getQuantidadeConexoes(); j++) {
                    if (elementoAtual.getConexao(j).getClass() == ConexaoEntrada.class) { //desconsidero
                        continue;
                    }
                    // sei que é uma saída:

                    //se elemento atual for conector
                    if (elementoAtual.getTipo() == TipoElemento.CONECTOR) {

                        for (int k = 0; k < programa.getQuantidadeElementos(); k++) {
                            //sei que elemento atual é um conector, logo, tem 2 conexões: uma de entrada, uma de saída.
                            if (("para" +programa.getElementoPorPosicao(k).getNome()).equals(elementoAtual.getConexaoSaida().toString())) {
                                where.append(elementoAtual.getNomeInst())
                                        .append("::")
                                        .append(elementoAtual.getConexao(j))
                                        .append(" unifies ")
                                        .append(programa.getElementoPorPosicao(k).getNomeInst())
                                        .append("::")
                                        .append(programa.getElementoPorPosicao(k).getEntradaLivre(elementoAtual.getConexao(j).getTipoDados()))
                                        .append("\n\t\t\t");
                                break; //serve?
                            }
                        }
                    }
                    //se elemento for Gateway
                    else if (elementoAtual.getTipo() == TipoElemento.GATEWAY) {
                        Conexao conexaoAtual;
                        for (int k = 0; k < elementoAtual.getQuantidadeConexoes(); k++) {
                            conexaoAtual = elementoAtual.getConexao(k);
                            if (conexaoAtual.getClass() == ConexaoEntrada.class) {
                                continue; //descarto
                            }
                            for (int l = 0; l < programa.getQuantidadeElementos(); l++) {
                                if (programa.getElementoPorPosicao(l).temConexao("de" + elementoAtual.getNome())
                                        && !programa.getElementoPorPosicao(l).getConexao("de" + elementoAtual.getNome()).isConectado()) {
                                    programa.getElementoPorPosicao(l).getConexao("de" + elementoAtual.getNome()).setConectado(true);

                                    where.append(elementoAtual.getNomeInst())
                                            .append("::")
                                            .append(conexaoAtual)
                                            .append(" unifies ")
                                            .append(programa.getElementoPorPosicao(l).getNomeInst())
                                            .append("::de")
                                            .append(elementoAtual.getNome())
                                            .append("\n\t\t\t");
                                    break;
                                }
                            }

                        }
                    }

                    // se elemento for Componente
                    else {
                        //encontro a entrada de conector com o nome "de"+nome do elemento atual
                        for (int k = 0; k < programa.getQuantidadeElementos(); k++) {
                            if (programa.getElementoPorPosicao(k).temConexao("de" + elementoAtual.getNome())) {
                                //encontrei a entrada correta, agora escrevo em where
                                where.append(elementoAtual.getNomeInst())
                                        .append("::")
                                        .append(elementoAtual.getConexao(j))
                                        .append(" unifies ")
                                        .append(programa.getElementoPorPosicao(k).getNomeInst())
                                        .append("::de")
                                        .append(elementoAtual.getNome())
                                        .append("\n\t\t\t");

                            }
                        }
                    }
                }

                i++;
                elementoAtual = programa.getElementoPorPosicao(i);
            }
            where.deleteCharAt(where.length()-1);
            where.append("}");

            // Fim do bloco where --------------------------------------------------------------------------------------

            // Construindo o bloco architecture:

            StringBuilder architecture = new StringBuilder();

            String nomeArquitetura = arquivo.getName().replaceAll("\\..*", "");
            if (nomeArquitetura.matches("[a-z].*")) { // começa com letra minúscula
                nomeArquitetura = nomeArquitetura.replaceFirst("[a-z]", String.valueOf((char) (nomeArquitetura.charAt(0) - 32)));
            }
            architecture.append("architecture ")
                    .append(nomeArquitetura)
                    .append(" is abstraction () {\n\t")
                    .append("behavior is {\n")
                    .append(compose)
                    .append(" ")
                    .append(where)
                    .append("\n\t}\n}\n")
                    .append("behavior is{\n\t")
                    .append("become(")
                    .append(nomeArquitetura)
                    .append("())\n}");


            System.out.println(architecture.toString());
            sc.close();
        } catch (FileNotFoundException e) {//todo: especificar melhor os possíveis catch possíveis
            System.out.println("Falha ao abrir arquivo: arquivo não encontrado");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erro desconhecido: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
