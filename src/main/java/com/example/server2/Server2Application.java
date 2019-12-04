package com.example.server2;

import java.io.*;
import java.util.Scanner;

public class Server2Application {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        final Integer numero;
        final String datafile;
        final String logfile;
        String tempdatafile = null;

        if (args.length<3) {
            System.out.println("ERRO: parametros insuficientes");
            return;
        }
        numero = Integer.parseInt(args[0]);
        datafile = args[1];
        logfile = args[2];

        System.out.println("TRANSACAO " + numero);
        System.out.println(numero);
        System.out.println(datafile);
        System.out.println(logfile);
        try {
            tempdatafile = copiaDatafile(datafile, numero);
            System.out.println(tempdatafile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer option;
        Aluno aluno;
        while (true) {
            System.out.println("Escolha a acao a ser executada:");
            System.out.println("    1: Visualizar dados");
            System.out.println("    2: Inserir registro");
            System.out.println("    3: Atualizar registro");
            System.out.println("    4: Deletar registro");
            System.out.println("    5: COMMIT");
            System.out.println("    6: ROLLBACK");
            option = Integer.parseInt(input.nextLine());

            switch (option) {
                case 1:
                    readFile(tempdatafile);
                    break;
                case 2:
                    aluno = new Aluno();
                    aluno.setCodigo(Integer.parseInt(input.nextLine()));
                    aluno.setNome(input.nextLine());
                    aluno.setNota(Double.parseDouble(input.nextLine()));
                    try {
                        insert(numero, aluno, tempdatafile, logfile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    break;
                case 4:
                    Integer codigo = Integer.parseInt(input.nextLine());
                    String nome = input.nextLine();
                    Double nota = Double.parseDouble(input.nextLine());

                    aluno = new Aluno(codigo, nome, nota);

                    //insert(aluno, path);
                    break;
                case 5:
                    return;
                case 6:
                    return;
                default:
                    System.out.println("\nOpcao invalida\n");
            }
        }
    }

    public static String copiaDatafile(String datafile, Integer numero) throws IOException {
        String arquivoTmp = numero + "datafile.txt";
        String linha;
        PrintWriter writer = new PrintWriter(new FileWriter(arquivoTmp));
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(datafile));
        while ((linha = reader.readLine()) != null) {
            writer.println(linha);
        }

        writer.close();
        reader.close();

        return arquivoTmp;
    }

    public static void readFile(String file) {
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo " + file + " nao encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo");
            e.printStackTrace();
        }
    }

    public static void insert(Integer numero, Aluno aluno, String datafile, String logfile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(datafile));
        String linha;
        while ((linha = reader.readLine()) != null) {
            if (linha.contains("codigo: " + aluno.getCodigo())) {
                reader.close();
                System.out.println("ERRO: codigo ja existe");
                return;
            }
        }
        reader.close();

        PrintWriter writer;
        writer = new PrintWriter(new FileWriter(logfile, true));
        writer.println();
        writer.println(numero + " transaction: insert");
        writer.println(aluno.getCodigo());
        writer.println(aluno.getNome());
        writer.println(aluno.getNota());
        writer.close();

        writer = new PrintWriter(new FileWriter(datafile, true));
        writer.println();
        writer.println("codigo: " + aluno.getCodigo());
        writer.println("nome: " + aluno.getNome());
        writer.println("nota: " + aluno.getNota());
        writer.close();
    }

    public static void update(String file, Aluno novo, Integer codigo) throws IOException {
        String arquivoTmp = "temp";
        String linha;
        PrintWriter writer = new PrintWriter(new FileWriter(arquivoTmp));
        BufferedReader reader;

        Boolean codigoNaoExiste = true;
        if (novo.getCodigo() != codigo) {
            reader = new BufferedReader(new FileReader(file));
            while ((linha = reader.readLine()) != null) {
                if (linha.contains("codigo: " + codigo)) {
                    codigoNaoExiste = false;
                }
            }
            reader.close();
        }
        if (codigoNaoExiste == true) {
            System.out.println("\nERRO: O codigo nao existe!\n");
            return;
        }

        if (novo.getCodigo() != codigo) {
            reader = new BufferedReader(new FileReader(file));
            while ((linha = reader.readLine()) != null) {
                if (linha.contains("codigo: " + novo.getCodigo())) {
                    System.out.println("\nERRO: O codigo ja existe!\n");
                    reader.close();
                    return;
                }
            }
            reader.close();
        }

        reader = new BufferedReader(new FileReader(file));
        while ((linha = reader.readLine()) != null) {
            if (linha.contains("codigo: " + codigo)) {
                if (novo.getCodigo() != codigo) {
                    linha = linha.replace("codigo: " + codigo, "codigo: " + novo.getCodigo());
                    System.out.println(linha);
                    writer.println(linha);
                }
                if (((linha = reader.readLine()) != null) && (("nome: " + novo.getNome()) != linha)) {
                    System.out.println(linha);
                    writer.println(linha);
                }
                if (((linha = reader.readLine()) != null) && (("nota: " + novo.getNota()) != linha)) {
                    System.out.println(linha);
                    writer.println(linha);
                }
                if ((linha = reader.readLine()) == null) {
                    break;
                }
            }
            writer.println(linha);
        }

        writer.close();
        reader.close();

        new File(file).delete();
        new File(arquivoTmp).renameTo(new File(file));
    }

    public static void delete(String file, Integer codigo) throws IOException {
        String arquivoTmp = "temp";
        String linha;
        PrintWriter writer = new PrintWriter(new FileWriter(arquivoTmp));
        BufferedReader reader;

        Boolean deletou = false;
        reader = new BufferedReader(new FileReader(file));
        while ((linha = reader.readLine()) != null) {
            if (linha.contains("codigo: " + codigo)) {
                System.out.println(linha);
                if (((linha = reader.readLine()) != null)) {
                    System.out.println(linha);
                }
                if (((linha = reader.readLine()) != null)) {
                    System.out.println(linha);
                }
                deletou = true;
                if ((linha = reader.readLine()) == null) {
                    break;
                }
            }
            writer.println(linha);
        }
        if (deletou) {
            System.out.println("Registro " + codigo + " deletado");
        } else {
            System.out.println("ERRO: Nao foi possivel deletar o registro");
        }

        writer.close();
        reader.close();

        new File(file).delete();
        new File(arquivoTmp).renameTo(new File(file));
    }
}
