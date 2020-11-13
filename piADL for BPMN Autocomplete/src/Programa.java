import java.util.ArrayList;

public class Programa {
    private String nome;
    private ArrayList<Elemento> elementos;

    Programa() {
        elementos = new ArrayList<>();
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void addElemento(Elemento elemento) {
        elementos.add(elemento);
    }

    public int getPosicaoUltimoElemento() {
        return elementos.size()-1;
    }

    public Elemento getUltimoElemento() {
        return elementos.get(elementos.size()-1);
    }

    public void setNomeUltimoElemento(String nome) {
        elementos.get(elementos.size()-1).setNome(nome);
    }

    public void addConexaoAoUltimoElemento(Conexao c) {
        elementos.get(elementos.size()-1).addConexao(c);
    }

    public void removerConexoesDesnecessarias() {
        for (Elemento e : elementos) {
            e.gerenciarConexoes();
        }
    }

    public void updateTiposElementos() {
        for (Elemento e : elementos) {
            e.updateTipo();
        }
    }

    public void setNomeInstanciaElementos() {
        int contagemInicio = 0;
        int contagemFim = 0;
        int contagemGateway = 0;
        int contagemConector = 0;
        int contagemComponente = 0;
        String nomeInst = "";
        for (Elemento e : elementos) {

            switch(e.getTipo()) {
                case INICIO:
                    if (contagemInicio != 0) nomeInst = "i" + ++contagemInicio;
                    else nomeInst = "i";
                    contagemInicio++;
                    break;
                case FIM:
                    if (contagemFim != 0) nomeInst = "f" + ++contagemFim;
                    else nomeInst = "f";
                    contagemFim++;
                    break;
                case GATEWAY:
                    if (contagemGateway != 0) nomeInst = "gw" + ++contagemGateway;
                    else nomeInst = "gw";
                    contagemGateway++;
                    break;
                case CONECTOR:
                    if (contagemConector != 0) nomeInst = "fl" + contagemConector;
                    else {
                        nomeInst = "fl" + ++contagemConector;
                    }
                    contagemConector++;
                    break;
                case COMPONENTE:
//                    if (contagemComponente != 0) nomeInst = "t" + ++contagemComponente;
//                    else nomeInst = "t";
//                    contagemComponente++;

                    if (contagemComponente == 0) {
                        nomeInst = "t";
                        contagemComponente++;
                    } else {
                        nomeInst = "t" + ++contagemComponente;
                    }
                    break;
            }
            e.setNomeInst(nomeInst);
        }
    }

    public Elemento getElementoPorPosicao(int i) {
        if (i < 0 || i >= elementos.size()) { // inválido
            return null;
        }
        return elementos.get(i);
    }

    public int getQuantidadeElementos() {
        return elementos.size();
    }
}
