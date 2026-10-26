package br.com.senseidev.dao;

import br.com.senseidev.model.Avaliacao;
import br.com.senseidev.model.HorarioGrade;
import br.com.senseidev.model.Material;
import br.com.senseidev.model.Mentor;
import br.com.senseidev.model.Sessao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// DAO da área do mentor.
// Por enquanto os dados ficam em memória (listas static) pra as telas funcionarem
// antes do JDBC. Cada método tem o SQL que vai entrar no lugar, na mesma ideia do CarDao.
// Os servlets só chamam estes métodos, então trocar o corpo por JDBC não mexe neles.
public class MentorDao {

    // ainda não tem login, então o mentor logado é sempre o de id 1
    public static final int MENTOR_TESTE = 1;

    // static pra os dados continuarem existindo entre uma requisição e outra
    // (o servlet cria um MentorDao novo a cada chamada, igual nos labs)
    private static final Mentor MENTOR = criarMentor();
    private static final List<Sessao> SESSOES = criarSessoes();
    private static final List<Material> MATERIAIS = criarMateriais();
    private static final List<Avaliacao> AVALIACOES = criarAvaliacoes();
    private static final List<HorarioGrade> GRADE = criarGrade();
    private static int proximoIdMaterial = 3;

    // ---------------------------------------------------------------- perfil

    public Mentor buscarMentor(int mentorId) {
        // SELECT u.nome, u.semestre_atual, p.descricao, p.experiencia, p.nota_media, p.total_mentorias
        //   FROM usuario u JOIN mentor_perfil p ON p.id = u.id
        //  WHERE u.id = ?
        return MENTOR;
    }

    public List<String> listarDisciplinas(int mentorId) {
        // SELECT d.nome FROM mentor_disciplina md
        //   JOIN disciplina d ON d.id = md.disciplina_id
        //  WHERE md.mentor_id = ?
        List<String> disciplinas = new ArrayList<>();
        disciplinas.add("Programação Orientada a Objetos");
        disciplinas.add("Estrutura de Dados");
        disciplinas.add("Infraestrutura de TI");
        disciplinas.add("Desenvolvimento de Sistemas Web");
        return disciplinas;
    }

    public List<HorarioGrade> buscarGrade(int mentorId) {
        // SELECT dia_semana, hora_inicio FROM disponibilidade WHERE mentor_id = ? AND ativo = TRUE
        // (o A de "agendado" sairia de um cruzamento com a tabela mentoria)
        return GRADE;
    }

    // marcados vem do formulário no formato "hora-dia", por exemplo "9-2" = 9h na quarta
    public void salvarPerfil(int mentorId, String descricao, List<String> marcados) {
        // UPDATE mentor_perfil SET descricao = ? WHERE id = ?
        // DELETE FROM disponibilidade WHERE mentor_id = ?
        // e um INSERT INTO disponibilidade (mentor_id, dia_semana, hora_inicio, hora_fim) pra cada marcado
        // (as duas últimas num mesmo commit, senão o mentor pode ficar sem horário se der erro no meio)
        MENTOR.setDescricao(descricao);

        for (HorarioGrade linha : GRADE) {
            String[] dias = linha.getDias();
            for (int j = 0; j < dias.length; j++) {
                if (dias[j].equals("A")) {
                    continue; // horário com sessão marcada não muda por aqui
                }
                dias[j] = marcados.contains(linha.getHora() + "-" + j) ? "D" : "X";
            }
        }
    }

    // ---------------------------------------------------------------- sessões

    // devolve todas as sessões do mentor em ordem de data, o servlet separa por status
    public List<Sessao> listarSessoes(int mentorId) {
        // SELECT m.id, u.nome, d.nome, m.data_hora_inicio, m.data_hora_fim, m.status, a.nota_final
        //   FROM mentoria m
        //   JOIN usuario u ON u.id = m.mentorando_id
        //   JOIN disciplina d ON d.id = m.disciplina_id
        //   LEFT JOIN avaliacao a ON a.mentoria_id = m.id
        //  WHERE m.mentor_id = ?
        //  ORDER BY m.data_hora_inicio
        List<Sessao> lista = new ArrayList<>(SESSOES);
        lista.sort(Comparator.comparing(Sessao::getInicio));
        return lista;
    }

    public void atualizarStatusSessao(int mentorId, int sessaoId, String status) {
        // UPDATE mentoria SET status = ?, atualizado_em = CURRENT_TIMESTAMP
        //  WHERE id = ? AND mentor_id = ?
        // o "AND mentor_id" impede um mentor de mexer na sessão de outro
        for (Sessao s : SESSOES) {
            if (s.getId() == sessaoId) {
                s.setStatus(status);
            }
        }
    }

    // ---------------------------------------------------------------- materiais

    public List<Material> listarMateriais(int mentorId) {
        // SELECT id, titulo, arquivo_url, criado_em FROM material
        //  WHERE mentor_id = ? ORDER BY criado_em DESC
        List<Material> lista = new ArrayList<>(MATERIAIS);
        lista.sort(Comparator.comparing(Material::getCriadoEm).reversed());
        return lista;
    }

    public void inserirMaterial(int mentorId, String titulo, String url) {
        // INSERT INTO material (mentor_id, titulo, arquivo_url) VALUES (?, ?, ?)
        MATERIAIS.add(new Material(proximoIdMaterial++, titulo, url, LocalDateTime.now(), 0));
    }

    public void removerMaterial(int mentorId, int materialId) {
        // DELETE FROM material WHERE id = ? AND mentor_id = ?
        MATERIAIS.removeIf(m -> m.getId() == materialId);
    }

    // ---------------------------------------------------------------- avaliações

    public List<Avaliacao> listarAvaliacoes(int mentorId) {
        // SELECT u.nome, d.nome, a.comentario, a.nota_final, a.criado_em
        //   FROM avaliacao a
        //   JOIN mentoria m ON m.id = a.mentoria_id
        //   JOIN usuario u ON u.id = m.mentorando_id
        //   JOIN disciplina d ON d.id = m.disciplina_id
        //  WHERE m.mentor_id = ? ORDER BY a.criado_em DESC
        return new ArrayList<>(AVALIACOES);
    }

    public double buscarNotaGeral(int mentorId) {
        // SELECT AVG(a.nota_final) FROM avaliacao a
        //   JOIN mentoria m ON m.id = a.mentoria_id WHERE m.mentor_id = ?
        return 4.9;
    }

    // LinkedHashMap pra manter a ordem em que os critérios foram inseridos
    public Map<String, Double> buscarMediasPorCriterio(int mentorId) {
        // SELECT AVG(a.clareza), AVG(a.dominio_assunto), AVG(a.pontualidade),
        //        AVG(a.capacidade_orientar), AVG(a.experiencia_geral)
        //   FROM avaliacao a JOIN mentoria m ON m.id = a.mentoria_id WHERE m.mentor_id = ?
        Map<String, Double> criterios = new LinkedHashMap<>();
        criterios.put("Clareza da explicação", 4.9);
        criterios.put("Domínio do assunto", 4.8);
        criterios.put("Pontualidade", 5.0);
        criterios.put("Capacidade de orientar", 4.7);
        criterios.put("Experiência geral", 4.9);
        return criterios;
    }

    // ---------------------------------------------------------------- dados de exemplo

    // as datas são calculadas a partir de hoje pra a demo nunca ficar com sessão "antiga"
    private static Mentor criarMentor() {
        Mentor m = new Mentor("Nome_Mentor", "mentor@sensei.dev", "hash", 3, null,
                "Veterano de ADS que curte ajudar quem tá começando. Gosto de explicar debugando junto, "
                        + "passo a passo, e de mostrar onde eu mesmo travei.",
                "Monitoria de POO e projetos de sistemas web.");
        m.setId(MENTOR_TESTE);
        m.setNotaMedia(4.8);
        m.setTotalMentorias(48);
        return m;
    }

    private static List<Sessao> criarSessoes() {
        LocalDate hoje = LocalDate.now();
        List<Sessao> lista = new ArrayList<>();
        lista.add(new Sessao(1, "Mariano Silva", "Infraestrutura de TI", hoje.minusDays(18).atTime(15, 0), 1, "REALIZADA", 5.0));
        lista.add(new Sessao(2, "Isabella Silva", "Programação Orientada a Objetos", hoje.minusDays(22).atTime(14, 0), 1, "REALIZADA", 4.8));
        lista.add(new Sessao(3, "Mariano Silva", "Estrutura de Dados", hoje.minusDays(24).atTime(10, 0), 1, "REALIZADA", 4.7));
        lista.add(new Sessao(4, "Mariano Silva", "Desenvolvimento de Sistemas Web", hoje.plusDays(3).atTime(15, 0), 1, "PENDENTE", null));
        lista.add(new Sessao(5, "Isabella Silva", "Programação Orientada a Objetos", hoje.plusDays(3).atTime(17, 0), 1, "PENDENTE", null));
        lista.add(new Sessao(6, "Isabella Silva", "Estrutura de Dados", hoje.plusDays(7).atTime(10, 0), 1, "CONFIRMADA", null));
        return lista;
    }

    private static List<Material> criarMateriais() {
        LocalDate hoje = LocalDate.now();
        List<Material> lista = new ArrayList<>();
        lista.add(new Material(1, "Repositório GitHub de POO", "https://github.com/", hoje.minusDays(18).atTime(10, 0), 20));
        lista.add(new Material(2, "Repositório GitHub de Estrutura de Dados", "https://github.com/", hoje.minusDays(18).atTime(10, 30), 20));
        return lista;
    }

    private static List<Avaliacao> criarAvaliacoes() {
        LocalDate hoje = LocalDate.now();
        List<Avaliacao> lista = new ArrayList<>();
        lista.add(new Avaliacao("Isabella Silva", "Infraestrutura de TI", "Ótima explicação", 4.9, hoje.minusDays(18).atTime(15, 0)));
        lista.add(new Avaliacao("Mariano Silva", "Estrutura de Dados", "Explicou listas encadeadas de um jeito que fez sentido.", 4.8, hoje.minusDays(22).atTime(14, 0)));
        lista.add(new Avaliacao("Isabella Silva", "Programação Orientada a Objetos", "Paciente e pontual, recomendo.", 5.0, hoje.minusDays(24).atTime(10, 0)));
        return lista;
    }

    // cada texto é um horário e cada letra um dia (seg a sáb): D disponível, X indisponível, A agendado
    private static List<HorarioGrade> criarGrade() {
        String[][] modelo = {
                {"7", "XDXXAX"}, {"8", "XDXDAX"}, {"9", "XXXXAX"}, {"10", "XXDXAX"},
                {"11", "XDXDAX"}, {"12", "XXDXDX"}, {"13", "XXXXXX"}, {"14", "XDXDDX"},
                {"15", "XXDXXX"}, {"16", "XXXXXX"}, {"17", "XDXXDX"}, {"18", "XXXXXX"}
        };
        List<HorarioGrade> grade = new ArrayList<>();
        for (String[] linha : modelo) {
            grade.add(new HorarioGrade(Integer.parseInt(linha[0]), linha[1].split("")));
        }
        return grade;
    }
}
