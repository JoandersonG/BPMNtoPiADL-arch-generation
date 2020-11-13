import java.util.ArrayList;

public class Elemento {
    private String nome;
    private String nomeInst;
    private TipoElemento tipo;
    private ArrayList<Conexao> conexoes;

    public Elemento() {
        this("");
    }

    public Elemento(String nome) {
        this.nome = nome;
        nomeInst = "";
        conexoes = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
//        this.nomeInst = nome.toLowerCase();
    }

    public String getNomeInst() {
        return nomeInst;
    }

    public void setNomeInst(String nomeInst) {
        this.nomeInst = nomeInst;
    }

    public TipoElemento getTipo() {
        return tipo;
    }

    public void setTipo(TipoElemento tipo) {
        this.tipo = tipo;
    }

    public void addConexao(Conexao c) {
        conexoes.add(c);
    }

    public int quantidadeConexoesEntrada() {
        int cont = 0;
        for (Conexao c : conexoes) {
            if (c.getClass() == ConexaoEntrada.class) {
                cont++;
            }
        }
        return cont;
    }

    public int quantidadeConexoesSaida() {
        int cont = 0;
        for (Conexao c : conexoes) {
            if (c.getClass() == ConexaoSaida.class) {
                cont++;
            }
        }
        return cont;
    }

    public void gerenciarConexoes() {
        if (conexoes.size() > 2 && quantidadeConexoesEntrada() == quantidadeConexoesSaida()) {
            // é um componente com entradas e saídas desnecessárias, preciso remover as conexões internas (segunda a antepenúltima)

            int i = 1;//não é o primeiro elemento
            while (i != conexoes.size()-1) { //enquanto não for o último elemento
                conexoes.remove(i);
            }
        }
    }
//todo: testar comentários
    public void updateTipo() {
        if (conexoes.size() == 1) { //é inicio ou fim
            if (conexoes.get(0).getClass() == ConexaoSaida.class) { //é inicio
                tipo = TipoElemento.INICIO;
            } else {
                tipo = TipoElemento.FIM;
            }
            return;
        }
        if (quantidadeConexoesEntrada() != quantidadeConexoesSaida()) { // é gateway
            tipo = TipoElemento.GATEWAY;
            return;
        }
        if (quantidadeConexoesEntrada() == 1) { // é conector
            tipo = TipoElemento.CONECTOR;
            return;
        }
        // é componente
        tipo = TipoElemento.COMPONENTE;
    }

    public int getQuantidadeConexoes() {
        return conexoes.size();
    }

    public Conexao getConexao(int i) {
        return conexoes.get(i);
    }

    public Conexao getConexao(String nomeConexao) {
        for ( Conexao c : conexoes) {
            if (c.getNome().equals(nomeConexao)) {
                return c;
            }
        }
        return null;
    }

    public boolean temConexao(String nomeConexao) {
        for (Conexao c : conexoes) {
            if (c.getNome().equals(nomeConexao)) {
                return true;
            }
        }
        return false;
    }

    public Conexao getConexaoSaida() {
        for (Conexao c : conexoes) {
            if (c.getClass() == ConexaoSaida.class) {
                return c;
            }
        }
        return null;
    }

    public Conexao getEntradaLivre(String tipoDados) {
        for (Conexao c : conexoes) {
            if (c.getTipoDados().equals(tipoDados) && c.getClass() == ConexaoEntrada.class) {
                return c;
            }
        }
        return null;
    }
}
