public abstract class Conexao {
    private String nome;
    private String tipoDados;
    private Boolean isConectado; //a outra porta

    public Conexao(String nome, String tipoDados) {
        this.nome = nome;
        this.tipoDados = tipoDados;
        isConectado = false;
    }

    @Override
    public String toString() {
        return this.nome;
    }

    public String getNome() {
        return nome;
    }

    public String getTipoDados() {
        return tipoDados;
    }

    public void setTipoDados(String tipoDados) {
        this.tipoDados = tipoDados;
    }

    public void setConectado(Boolean conectado) {
        isConectado = conectado;
    }

    public Boolean isConectado() {
        return isConectado;
    }
}
