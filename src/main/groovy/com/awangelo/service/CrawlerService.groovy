package com.awangelo.service

import com.awangelo.parser.AnsHtmlParser
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static groovyx.net.http.HttpBuilder.configure

final class CrawlerService {
    private final HttpBuilder httpBuilder
    private static final String BASE_URL = 'https://www.gov.br/ans/pt-br'
    static final String USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36'

    CrawlerService() {
        this.httpBuilder = configure {
            request.uri = BASE_URL
            request.headers['User-Agent'] = USER_AGENT
        }
    }

    Map<String, Object> buscarDocumentosTiss() {
        println '1. Buscando página inicial...'
        Document home = fetchPage()

        println '2. Buscando URL Espaço do Prestador...'
        String urlPrestador = AnsHtmlParser.extrairLinkPrestador(home)

        println '3. Buscando TISS...'
        Document docPrestador = fetchPage(urlPrestador)
        String urlTiss = AnsHtmlParser.extrairLinkTiss(docPrestador)

        println '--> Acessando página central do TISS...'
        Document docPageTiss = fetchPage(urlTiss)

        println '4.1. Buscando Link da Versão Atual...'
        String urlVersao = AnsHtmlParser.extrairLinkVersaoAtual(docPageTiss)

        println '4.2. Buscando e Processando Histórico das versões...'
        String urlHist = AnsHtmlParser.extrairLinkHistorico(docPageTiss)

        println '4.3. Buscando Tabelas Relacionadas...'
        String urlRel = AnsHtmlParser.extrairLinkTabelas(docPageTiss)

        println '5.1. Buscando Componente de Comunicação...'
        Document docVersaoAtual = fetchPage(urlVersao)
        String urlDownload = AnsHtmlParser.extrairLinkComponenteComunicacao(docVersaoAtual)
        println ' --> Link encontrado'

        println '5.2. Buscando Tabela de competência, publicação e início de vigência'
        Document docHistorico = fetchPage(urlHist)
        List<Map<String, String>> dadosTabela = AnsHtmlParser.extrairTabelaHistorico(docHistorico)
        println ' --> Dados encontrados'

        println '5.3. Buscando Tabelas Relacionadas...'
        Document docTabelas = fetchPage(urlRel)
        String urlErros = AnsHtmlParser.extrairLinkTabelaDeErros(docTabelas)
        println ' --> Link encontrado'

        [
                urlDownload    : urlDownload,
                urlTabelaErros : urlErros,
                tabelaHistorico: dadosTabela
        ]

    }

    private Document fetchPage() {
        fetchPage(BASE_URL)
    }

    private Document fetchPage(String url) {
        try {
            httpBuilder.get {
                request.uri = url

                response.success { _, def body ->
                    if (body instanceof Document) {
                        body
                    }

                    Jsoup.parse(body.toString(), url)
                }

                // status >= 400
                response.failure { FromServer fs, _ ->
                    throw new RuntimeException("Falha HTTP ${fs.statusCode} ao acessar ${url}")
                }

                // "This is different from a failure condition because there is no response, no status code, no headers, etc"
                response.exception { t ->
                    throw new RuntimeException("Erro de conexão ao acessar ${url}: ${t.message}", t)
                }
            } as Document
        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar página: ${url}", e)
        }
    }
}