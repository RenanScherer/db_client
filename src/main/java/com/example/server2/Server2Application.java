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

        if (args.length < 3) {
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
        Integer cod;
        while (true) {
            System.out.println("Escolha a acao a ser executada:");
            System.out.println("    1: Visualizar dados");
            System.out.println("    2: Inserir registro");
            System.out.println("    3: Atualizar registro");
            System.out.println("    4: Deletar registro");
            System.out.println("    5: COMMIT");
            System.out.println("    6: ROLLBACK");
            option = Integer.parseInt(input.nextLine());

            PrintWriter writer;
            switch (option) {
            case 1:
                readFile(tempdatafile);
                break;
            case 2:
                aluno = new Aluno();
                System.out.print("codigo: ");
                aluno.setCodigo(Integer.parseInt(input.nextLine()));
                System.out.print("nome: ");
                aluno.setNome(input.nextLine());
                System.out.print("nota: ");
                aluno.setNota(Double.parseDouble(input.nextLine()));
                try {
                    insert(numero, aluno, tempdatafile, logfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                System.out.print("\nCodigo do registro: ");
                cod = Integer.parseInt(input.nextLine());
                aluno = verficaExistencia(tempdatafile, "codigo: " + cod);
                if (aluno == null) {
                    System.out.println("\n\nERRO: registro nao encontrado\n");
                    continue;
                }
                System.out.println(aluno.getCodigo());
                System.out.println(aluno.getNome());
                System.out.println(aluno.getNota());
                System.out.println("\nNovas informacoes:");
                System.out.println("    Codigo: ");
                aluno.setCodigo(Integer.parseInt(input.nextLine()));
                System.out.println("    Nome: ");
                aluno.setNome(input.nextLine());
                System.out.println("    Nota: ");
                aluno.setNota(Double.parseDouble(input.nextLine()));
                try {
                    update(numero, logfile, tempdatafile, aluno, cod);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            case 4:
                System.out.println("Codigo do registro a ser deletado: ");
                cod = Integer.parseInt(input.nextLine());
                aluno = verficaExistencia(tempdatafile, "codigo: " + cod);
                if (aluno == null) {
                    System.out.println("\n\nERRO: registro nao encontrado\n");
                    continue;
                }
                try {
                    delete(numero, logfile, tempdatafile, cod);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                    break;
                case 5: 
                    try {
                        writer = new PrintWriter(new FileWriter(logfile, true));
                        writer.println();
                        writer.println(numero + "transaction: COMMIT");
                        writer.close();
                        new File(tempdatafile).delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                case 6:
                try {
                    writer = new PrintWriter(new FileWriter(logfile, true));
                    writer.println();
                    writer.println(numero + "transaction: ROLLBACK");
                    writer.close();
                    new File(tempdatafile).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public static Aluno verficaExistencia(String file, String palavra) {
        Aluno aluno = null;
        Boolean codigoNaoExiste = true;
        String linha;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((linha = reader.readLine()) != null) {
                if (linha.contains(palavra)) {
                    aluno.setCodigo(Integer.parseInt(reader.readLine().substring(8)));
                    aluno.setNome(reader.readLine().substring(6));
                    aluno.setNota(Double.parseDouble(reader.readLine().substring(6)));
                    return aluno;
                }
            }
        reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void update(Integer num, String logfile, String datafile, Aluno novo, Integer codigo) throws IOException {
        String arquivoTmp = "temp";
        String linha;
        PrintWriter writer;
        BufferedReader reader;

        if (novo.getCodigo() != codigo) {
            reader = new BufferedReader(new FileReader(datafile));
            while ((linha = reader.readLine()) != null) {
                if (linha.contains("codigo: " + novo.getCodigo())) {
                    System.out.println("\n\nERRO: O codigo ja existe!\n");
                    reader.close();
                    return;
                }
            }
            reader.close();
        }

        writer = new PrintWriter(new FileWriter(logfile, true));
        writer.println("\n" + num + " transaction: update");
        writer.println(codigo);
        writer.println("codigo: " + novo.getCodigo());
        writer.println("nome: " + novo.getNome());
        writer.println("nota: " + novo.getNota());
        writer.close();

        reader = new BufferedReader(new FileReader(datafile));
        writer = new PrintWriter(new FileWriter(arquivoTmp));
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

        new File(datafile).delete();
        new File(arquivoTmp).renameTo(new File(datafile));
    }

    public static void delete(Integer num, String logfile, String file, Integer codigo) throws IOException {
        String arquivoTmp = "temp";
        String linha;
        PrintWriter writer;
        BufferedReader reader;

        writer = new PrintWriter(new FileWriter(logfile, true));
        writer.println("\n" + num + " transaction: delete");
        writer.println(codigo);
        writer.close();

        Boolean deletou = false;
        writer = new PrintWriter(new FileWriter(arquivoTmp));
        reader = new BufferedReader(new FileReader(file));
        while ((linha = reader.readLine()) != null) {
            if (linha.contains("codigo: " + codigo)) {
                //System.out.println(linha);
                if (((linha = reader.readLine()) != null)) {
                    //System.out.println(linha);
                }
                if (((linha = reader.readLine()) != null)) {
                    //System.out.println(linha);
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
