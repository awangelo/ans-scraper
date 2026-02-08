package com.awangelo.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.time.YearMonth

final class AnsHtmlParser {
    private static final Map<String, Integer> MESES = [
            'jan': 1, 'fev': 2, 'mar': 3, 'abr': 4, 'mai': 5, 'jun': 6,
            'jul': 7, 'ago': 8, 'set': 9, 'out': 10, 'nov': 11, 'dez': 12
    ]

    static String extrairLinkPrestador(Document doc) {
        extrairLinkPorTexto(doc, 'Espaço do Prestador de Serviços de Saúde')
    }

    static String extrairLinkTiss(Document doc) {
        extrairLinkPorTexto(doc, 'TISS - Padrão para Troca de Informação de Saúde Suplementar')
    }

    static String extrairLinkVersaoAtual(Document doc) {
        extrairLinkPorTexto(doc, 'Clique aqui para acessar a versão')
    }

    static String extrairLinkHistorico(Document doc) {
        extrairLinkPorTexto(doc, 'Clique aqui para acessar todas as versões dos Componentes')
    }

    static String extrairLinkTabelas(Document doc) {
        extrairLinkPorTexto(doc, 'Clique aqui para acessar as planilhas')
    }

    static String extrairLinkComponenteComunicacao(Document doc) {
        // Procura pelo texto do elemento screen reader.
        Element link = doc.select('a:contains(Componente de Comunicação.)').first()
        if (!link) throw new RuntimeException('Link não encontrado.')

        link.attr("abs:href")
    }

    static List<Map<String, String>> extrairTabelaHistorico(Document doc) {
        List<Map<String, String>> resultados = []
        YearMonth dataCorte = YearMonth.of(2016, 1) // Jan/2016

        doc.select("table tbody tr").each { Element row ->
            Elements colunas = row.select("td")

            if (colunas.size() >= 3) {
                String compTxt = colunas[0].text().trim()
                String pubTxt = colunas[1].text().trim()
                String vigTxt = colunas[2].text().trim()

                YearMonth dataLinha = converterDataPtBr(compTxt)

                if (!dataLinha.isBefore(dataCorte)) {
                    resultados.add([
                            competencia: compTxt,
                            publicacao : pubTxt,
                            vigencia   : vigTxt
                    ])
                }
            }
        }
        resultados
    }

    private static YearMonth converterDataPtBr(String dataTexto) {
        String limpo = dataTexto.replace('\u00A0', '').trim().toLowerCase()

        String[] partes = limpo.split('/')
        if (partes.length < 2) {
            throw new IllegalArgumentException("Formato inválido")
        }

        String nomeMes = partes[0].trim().replace('.', '')
        Integer ano = Integer.parseInt(partes[1].trim())

        Integer numeroMes = MESES[nomeMes]
        if (!numeroMes) {
            throw new IllegalArgumentException("Mês não reconhecido: ${nomeMes}")
        }

        YearMonth.of(ano, numeroMes)
    }

    static String extrairLinkTabelaDeErros(Document doc) {
        extrairLinkPorTexto(doc, 'Clique aqui para baixar a tabela de erros no envio para a ANS (.xlsx)')
    }

    private static String extrairLinkPorTexto(Document doc, String textoParcial) {
        Element foundLink = doc.select("a:contains(${textoParcial})").first()
        if (!foundLink) throw new RuntimeException("Link contendo '${textoParcial}' não encontrado.")

        foundLink.attr('abs:href')
    }
}
