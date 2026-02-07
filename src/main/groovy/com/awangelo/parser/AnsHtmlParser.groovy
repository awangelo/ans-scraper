package com.awangelo.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

final class AnsHtmlParser {
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

    private static String extrairLinkPorTexto(Document doc, String textoParcial) {
        Element foundLink = doc.select("a:contains(${textoParcial})").first()
        if (!foundLink) throw new RuntimeException("Link contendo '${textoParcial}' não encontrado.")

        foundLink.attr('abs:href')
    }
}
