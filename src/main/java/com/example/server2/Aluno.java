package com.example.server2;

public class Aluno {

    private Integer codigo;
    private String nome;
    private Double nota;

    public Aluno() {
    }

    public Aluno(Integer codigo, String nome, Double nota) {
        this.codigo = codigo;
        this.nome = nome;
        this.nota = nota;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    @Override
    public String toString() {
        return this.codigo + "\n" + this.nome + "\n" + this.nota;
    }
}
